package br.com.teadescomplica.data.repository

import androidx.room.withTransaction
import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.database.ExperienceStrategyCrossRefDao
import br.com.teadescomplica.core.database.PersonalExperienceDao
import br.com.teadescomplica.data.mapper.ExperienceStrategyCrossRefMapper
import br.com.teadescomplica.data.mapper.PersonalExperienceMapper
import br.com.teadescomplica.domain.model.ExperienceStrategyCrossRef
import br.com.teadescomplica.domain.model.PersonalExperience
import br.com.teadescomplica.domain.repository.PersonalExperienceRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PersonalExperienceRepositoryImpl(
    private val database: AppDatabase,
    private val dao: PersonalExperienceDao,
    private val crossRefDao: ExperienceStrategyCrossRefDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PersonalExperienceRepository {

    override fun getAllExperiences(): Flow<List<PersonalExperience>> =
        dao.getAllExperiences().map { entities ->
            entities.map { PersonalExperienceMapper.toDomain(it) }
        }

    override fun getExperienceById(id: Long): Flow<PersonalExperience?> =
        dao.getExperienceById(id).map { entity ->
            if (entity == null) null else PersonalExperienceMapper.toDomain(entity)
        }

    override fun getExperiencesByScenarioId(scenarioId: Long): Flow<List<PersonalExperience>> =
        dao.getExperiencesByScenarioId(scenarioId).map { entities ->
            entities.map { PersonalExperienceMapper.toDomain(it) }
        }

    override fun searchExperiences(query: String): Flow<List<PersonalExperience>> =
        dao.searchExperiences("%$query%").map { entities ->
            entities.map { PersonalExperienceMapper.toDomain(it) }
        }

    override suspend fun insertExperience(experience: PersonalExperience): Long =
        withContext(ioDispatcher) {
            dao.insertExperience(PersonalExperienceMapper.toEntity(experience))
        }

    override suspend fun saveExperienceWithStrategies(
        experience: PersonalExperience,
        strategyIds: Set<Long>,
    ): Long =
        withContext(ioDispatcher) {
            require(experience.title.isNotBlank())
            database.withTransaction {
                val id =
                    if (experience.id == 0L)
                        dao.insertExperience(PersonalExperienceMapper.toEntity(experience))
                    else {
                        require(dao.getExperienceById(experience.id).first() != null)
                        dao.updateExperience(PersonalExperienceMapper.toEntity(experience))
                        experience.id
                    }
                crossRefDao.deleteCrossRefsForExperience(id)
                strategyIds.forEach {
                    crossRefDao.insertCrossRef(
                        br.com.teadescomplica.core.database.ExperienceStrategyCrossRefEntity(id, it)
                    )
                }
                id
            }
        }

    override suspend fun updateExperience(experience: PersonalExperience) =
        withContext(ioDispatcher) {
            dao.updateExperience(PersonalExperienceMapper.toEntity(experience))
        }

    override suspend fun deleteExperience(id: Long) =
        withContext(ioDispatcher) { dao.deleteExperienceById(id) }

    override fun getStrategiesForExperience(experienceId: Long): Flow<List<Long>> =
        crossRefDao.getStrategyIdsForExperience(experienceId)

    override fun getAllExperienceStrategyAssociations(): Flow<List<ExperienceStrategyCrossRef>> =
        crossRefDao.getAllCrossRefs().map { entities ->
            entities.map { ExperienceStrategyCrossRefMapper.toDomain(it) }
        }

    companion object {
        fun create(database: AppDatabase): PersonalExperienceRepository =
            PersonalExperienceRepositoryImpl(
                database,
                database.personalExperienceDao(),
                database.experienceStrategyCrossRefDao(),
            )
    }
}
