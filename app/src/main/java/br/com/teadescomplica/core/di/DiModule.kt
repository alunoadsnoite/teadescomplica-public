package br.com.teadescomplica.core.di

import android.content.Context
import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.datastore.PreferenceRepository
import br.com.teadescomplica.data.repository.AlternativePlanRepositoryImpl
import br.com.teadescomplica.data.repository.BackupRepositoryImpl
import br.com.teadescomplica.data.repository.PersonalExperienceRepositoryImpl
import br.com.teadescomplica.data.repository.PersonalPhraseRepositoryImpl
import br.com.teadescomplica.data.repository.PersonalStrategyRepositoryImpl
import br.com.teadescomplica.data.repository.PossibleSituationRepositoryImpl
import br.com.teadescomplica.data.repository.ScenarioPreparationRepositoryImpl
import br.com.teadescomplica.data.repository.ScenarioRepositoryImpl
import br.com.teadescomplica.domain.repository.AlternativePlanRepository
import br.com.teadescomplica.domain.repository.BackupRepository
import br.com.teadescomplica.domain.repository.PersonalExperienceRepository
import br.com.teadescomplica.domain.repository.PersonalPhraseRepository
import br.com.teadescomplica.domain.repository.PersonalStrategyRepository
import br.com.teadescomplica.domain.repository.PossibleSituationRepository
import br.com.teadescomplica.domain.repository.ScenarioPreparationRepository
import br.com.teadescomplica.domain.repository.ScenarioRepository

object DiModule {
    private lateinit var database: AppDatabase
    private lateinit var scenarioRepository: ScenarioRepository
    private lateinit var scenarioPreparationRepository: ScenarioPreparationRepository
    private lateinit var possibleSituationRepository: PossibleSituationRepository
    private lateinit var alternativePlanRepository: AlternativePlanRepository
    private lateinit var personalPhraseRepository: PersonalPhraseRepository
    private lateinit var personalExperienceRepository: PersonalExperienceRepository
    private lateinit var personalStrategyRepository: PersonalStrategyRepository
    private lateinit var preferenceRepository: PreferenceRepository
    private lateinit var backupRepository: BackupRepository

    fun init(context: Context, databaseOverride: AppDatabase? = null) {
        database = databaseOverride ?: AppDatabase.getDatabase(context.applicationContext)
        scenarioRepository = ScenarioRepositoryImpl.create(database)
        scenarioPreparationRepository = ScenarioPreparationRepositoryImpl.create(database)
        possibleSituationRepository = PossibleSituationRepositoryImpl.create(database)
        alternativePlanRepository = AlternativePlanRepositoryImpl.create(database)
        personalPhraseRepository = PersonalPhraseRepositoryImpl.create(database)
        personalExperienceRepository = PersonalExperienceRepositoryImpl.create(database)
        personalStrategyRepository = PersonalStrategyRepositoryImpl.create(database)
        preferenceRepository = PreferenceRepository(context.applicationContext)
        backupRepository =
            BackupRepositoryImpl(context.applicationContext, preferenceRepository, database)
    }

    fun getScenarioRepository(): ScenarioRepository = scenarioRepository

    fun getScenarioPreparationRepository(): ScenarioPreparationRepository =
        scenarioPreparationRepository

    fun getPossibleSituationRepository(): PossibleSituationRepository = possibleSituationRepository

    fun getAlternativePlanRepository(): AlternativePlanRepository = alternativePlanRepository

    fun getPersonalPhraseRepository(): PersonalPhraseRepository = personalPhraseRepository

    fun getPersonalExperienceRepository(): PersonalExperienceRepository =
        personalExperienceRepository

    fun getPersonalStrategyRepository(): PersonalStrategyRepository = personalStrategyRepository

    fun getPreferenceRepository(): PreferenceRepository = preferenceRepository

    fun getBackupRepository(): BackupRepository = backupRepository

    fun getDatabase(): AppDatabase = database

    fun clear() {
        AppDatabase.clearInstance()
    }
}
