package br.com.teadescomplica.data.repository

import android.content.Context
import android.net.Uri
import androidx.room.withTransaction
import br.com.teadescomplica.core.database.*
import br.com.teadescomplica.core.datastore.*
import br.com.teadescomplica.data.crypto.*
import br.com.teadescomplica.data.mapper.BackupSnapshotSerializer
import br.com.teadescomplica.domain.model.BackupSnapshot
import br.com.teadescomplica.domain.repository.BackupFeedback
import br.com.teadescomplica.domain.repository.BackupRepository
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class BackupRepositoryImpl(
    context: Context,
    private val preferences: PreferenceRepository,
    private val database: AppDatabase,
) : BackupRepository {
    private val resolver = context.contentResolver
    private val operations = Mutex()
    private val backingUp = MutableStateFlow(false)
    private val restoring = MutableStateFlow(false)
    private val error = MutableStateFlow<BackupFeedback?>(null)
    private val success = MutableStateFlow<BackupFeedback?>(null)

    override fun isBackupInProgress(): StateFlow<Boolean> = backingUp.asStateFlow()

    override fun isRestoreInProgress(): StateFlow<Boolean> = restoring.asStateFlow()

    override fun getLastError(): StateFlow<BackupFeedback?> = error.asStateFlow()

    override fun getLastSuccess(): StateFlow<BackupFeedback?> = success.asStateFlow()

    override fun clearFeedback() {
        error.value = null
        success.value = null
    }

    override suspend fun createSnapshot(): BackupSnapshot =
        withContext(Dispatchers.IO) {
            database.withTransaction {
                BackupSnapshot(
                    scenarios =
                        database.scenarioDao().getAllScenarios().first().map { it.toDomain() },
                    scenarioPreparations =
                        database
                            .scenarioPreparationDao()
                            .getAllPreparations()
                            .first()
                            .sortedWith(
                                compareByDescending<ScenarioPreparationEntity> { it.updatedAt }
                                    .thenByDescending { it.id }
                            )
                            .distinctBy { it.scenarioId }
                            .map { it.toDomain() },
                    possibleSituations =
                        database.possibleSituationDao().getAllPossibleSituations().first().map {
                            it.toDomain()
                        },
                    alternativePlans =
                        database.alternativePlanDao().getAllAlternativePlans().first().map {
                            it.toDomain()
                        },
                    personalPhrases =
                        database.personalPhraseDao().getAllPersonalPhrases().first().map {
                            it.toDomain()
                        },
                    personalExperiences =
                        database.personalExperienceDao().getAllExperiences().first().map {
                            it.toDomain()
                        },
                    personalStrategies =
                        database.personalStrategyDao().getAllStrategies().first().map {
                            it.toDomain()
                        },
                    experienceStrategyAssociations =
                        database.experienceStrategyCrossRefDao().getAllCrossRefs().first().map {
                            it.toDomain()
                        },
                    themePreference = preferences.getThemePreferenceOnce().key,
                    reducedMotion = preferences.getReducedMotion().first(),
                )
            }
        }

    override suspend fun exportEncryptedBackup(
        snapshot: BackupSnapshot,
        password: String,
        outputUri: Uri,
    ): Boolean =
        withContext(Dispatchers.IO) {
            operations.withLock {
                backingUp.value = true
                clearFeedback()
                try {
                    require(password.length >= 8) { "Use pelo menos 8 caracteres na senha." }
                    val json = BackupSnapshotSerializer.serialize(snapshot)
                    val salt = CryptoManager.generateSalt()
                    val nonce = CryptoManager.generateNonce()
                    val aad =
                        BackupFormat.serializeAad(
                            1,
                            CryptoManager.KDF_VERSION,
                            CryptoManager.CRYPTO_VERSION,
                        )
                    val encrypted =
                        CryptoManager.encrypt(
                            json.toByteArray(Charsets.UTF_8),
                            CryptoManager.deriveKey(password, salt),
                            nonce,
                            aad,
                        )
                    val bytes = BackupFormat.buildBackupFile(salt, nonce, aad, encrypted)
                    require(bytes.size <= BackupFormat.MAX_BACKUP_SIZE_BYTES) {
                        "Backup excede 50 MB."
                    }
                    val stream =
                        resolver.openOutputStream(outputUri, "wt")
                            ?: error("Não foi possível abrir o destino.")
                    stream.use {
                        it.write(bytes)
                        it.flush()
                    }
                    success.value = BackupFeedback.CREATED
                    true
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    error.value = BackupFeedback.CREATE_FAILED
                    false
                } finally {
                    backingUp.value = false
                }
            }
        }

    override suspend fun importEncryptedBackup(inputUri: Uri, password: String): BackupSnapshot? =
        withContext(Dispatchers.IO) {
            operations.withLock {
                restoring.value = true
                clearFeedback()
                try {
                    val input = resolver.openInputStream(inputUri) ?: error("Arquivo indisponível")
                    val bytes =
                        input.use {
                            val out = ByteArrayOutputStream()
                            val buffer = ByteArray(8192)
                            while (true) {
                                val count = it.read(buffer)
                                if (count == -1) break
                                require(
                                    out.size().toLong() + count <=
                                        BackupFormat.MAX_BACKUP_SIZE_BYTES
                                ) {
                                    "Arquivo excede 50 MB"
                                }
                                out.write(buffer, 0, count)
                            }
                            out.toByteArray()
                        }
                    val parsed = BackupFormat.parseBackupFile(bytes) ?: error("Formato inválido")
                    val plain =
                        CryptoManager.decrypt(
                            parsed.ciphertext,
                            CryptoManager.deriveKey(password, parsed.salt),
                            parsed.nonce,
                            parsed.aad,
                        ) ?: error("Senha incorreta ou arquivo adulterado")
                    BackupSnapshotSerializer.deserialize(plain.toString(Charsets.UTF_8))
                        ?: error("Dados inválidos")
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    error.value =
                        BackupFeedback.READ_FAILED
                    null
                } finally {
                    restoring.value = false
                }
            }
        }

    override suspend fun applySnapshot(snapshot: BackupSnapshot): Boolean =
        withContext(Dispatchers.IO) {
            operations.withLock {
                restoring.value = true
                clearFeedback()
                val oldTheme = preferences.getThemePreferenceOnce()
                val oldMotion = preferences.getReducedMotion().first()
                try {
                    require(BackupSnapshotSerializer.isValid(snapshot)) { "Snapshot inválido" }
                    database.withTransaction {
                        database.experienceStrategyCrossRefDao().deleteAllCrossRefs()
                        database.personalExperienceDao().deleteAllPersonalExperiences()
                        database.personalStrategyDao().deleteAllPersonalStrategies()
                        database.scenarioDao().deleteAllScenarios()
                        snapshot.scenarios.forEach {
                            database.scenarioDao().insertScenario(ScenarioEntity.fromDomain(it))
                        }
                        snapshot.scenarioPreparations.forEach {
                            database
                                .scenarioPreparationDao()
                                .savePreparation(ScenarioPreparationEntity.fromDomain(it))
                        }
                        snapshot.possibleSituations.forEach {
                            database
                                .possibleSituationDao()
                                .insertPossibleSituation(PossibleSituationEntity.fromDomain(it))
                        }
                        snapshot.alternativePlans.forEach {
                            database
                                .alternativePlanDao()
                                .insertAlternativePlan(AlternativePlanEntity.fromDomain(it))
                        }
                        snapshot.personalPhrases.forEach {
                            database
                                .personalPhraseDao()
                                .insertPersonalPhrase(PersonalPhraseEntity.fromDomain(it))
                        }
                        snapshot.personalExperiences.forEach {
                            database
                                .personalExperienceDao()
                                .insertExperience(PersonalExperienceEntity.fromDomain(it))
                        }
                        snapshot.personalStrategies.forEach {
                            database
                                .personalStrategyDao()
                                .insertStrategy(PersonalStrategyEntity.fromDomain(it))
                        }
                        snapshot.experienceStrategyAssociations.forEach {
                            database
                                .experienceStrategyCrossRefDao()
                                .insertCrossRef(
                                    ExperienceStrategyCrossRefEntity(it.experienceId, it.strategyId)
                                )
                        }
                        preferences.setThemePreference(
                            ThemePreference.fromStringKey(snapshot.themePreference)
                        )
                        preferences.setReducedMotion(snapshot.reducedMotion)
                    }
                    success.value = BackupFeedback.RESTORED
                    true
                } catch (e: Exception) {
                    withContext(NonCancellable) {
                        preferences.setThemePreference(oldTheme)
                        preferences.setReducedMotion(oldMotion)
                    }
                    if (e is CancellationException) throw e
                    error.value =
                        BackupFeedback.RESTORE_FAILED
                    false
                } finally {
                    restoring.value = false
                }
            }
        }
}
