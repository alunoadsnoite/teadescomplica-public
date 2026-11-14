package br.com.teadescomplica.data.repository

import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.database.PersonalPhraseDao
import br.com.teadescomplica.data.mapper.PersonalPhraseMapper
import br.com.teadescomplica.domain.model.PersonalPhrase
import br.com.teadescomplica.domain.repository.PersonalPhraseRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PersonalPhraseRepositoryImpl(
    private val dao: PersonalPhraseDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PersonalPhraseRepository {

    override fun getPersonalPhrasesByScenarioId(scenarioId: Long): Flow<List<PersonalPhrase>> =
        dao.getPersonalPhrasesByScenarioId(scenarioId).map { entities ->
            entities.map { PersonalPhraseMapper.toDomain(it) }
        }

    override fun getPersonalPhraseById(id: Long): Flow<PersonalPhrase?> =
        dao.getPersonalPhraseById(id).map { entity ->
            if (entity == null) null else PersonalPhraseMapper.toDomain(entity)
        }

    override suspend fun insertPersonalPhrase(phrase: PersonalPhrase): Long =
        withContext(ioDispatcher) {
            dao.insertPersonalPhrase(PersonalPhraseMapper.toEntity(phrase))
        }

    override suspend fun updatePersonalPhrase(phrase: PersonalPhrase) =
        withContext(ioDispatcher) {
            dao.updatePersonalPhrase(PersonalPhraseMapper.toEntity(phrase))
        }

    override suspend fun deletePersonalPhrase(id: Long) {
        val entity = withContext(ioDispatcher) { dao.getPersonalPhraseById(id).first() }
        if (entity != null) {
            withContext(ioDispatcher) { dao.deletePersonalPhrase(entity) }
        }
    }

    override suspend fun reorderPersonalPhrases(scenarioId: Long, phrases: List<PersonalPhrase>) =
        withContext(ioDispatcher) {
            dao.reorder(scenarioId, phrases.map { PersonalPhraseMapper.toEntity(it) })
        }

    override fun getAllPersonalPhrases(): Flow<List<PersonalPhrase>> =
        dao.getAllPersonalPhrases().map { entities ->
            entities.map { PersonalPhraseMapper.toDomain(it) }
        }

    companion object {
        fun create(database: AppDatabase): PersonalPhraseRepository =
            PersonalPhraseRepositoryImpl(database.personalPhraseDao())
    }
}
