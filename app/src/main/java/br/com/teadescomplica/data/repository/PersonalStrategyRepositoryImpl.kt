package br.com.teadescomplica.data.repository

import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.database.ExperienceStrategyCrossRefDao
import br.com.teadescomplica.core.database.PersonalStrategyDao
import br.com.teadescomplica.data.mapper.PersonalStrategyMapper
import br.com.teadescomplica.domain.model.PersonalStrategy
import br.com.teadescomplica.domain.repository.PersonalStrategyRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PersonalStrategyRepositoryImpl(
    private val dao: PersonalStrategyDao,
    private val crossRefDao: ExperienceStrategyCrossRefDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PersonalStrategyRepository {

    override fun getAllStrategies(): Flow<List<PersonalStrategy>> =
        dao.getAllStrategies().map { entities ->
            entities.map { PersonalStrategyMapper.toDomain(it) }
        }

    override fun getStrategyById(id: Long): Flow<PersonalStrategy?> =
        dao.getStrategyById(id).map { entity ->
            if (entity == null) null else PersonalStrategyMapper.toDomain(entity)
        }

    override suspend fun insertStrategy(strategy: PersonalStrategy): Long =
        withContext(ioDispatcher) { dao.insertStrategy(PersonalStrategyMapper.toEntity(strategy)) }

    override suspend fun updateStrategy(strategy: PersonalStrategy) =
        withContext(ioDispatcher) { dao.updateStrategy(PersonalStrategyMapper.toEntity(strategy)) }

    override suspend fun deleteStrategy(id: Long) =
        withContext(ioDispatcher) { dao.deleteStrategyById(id) }

    override fun getExperienceIdsForStrategy(strategyId: Long): Flow<List<Long>> =
        crossRefDao.getExperienceIdsForStrategy(strategyId)

    override suspend fun associateStrategyToExperience(strategyId: Long, experienceId: Long) =
        withContext(ioDispatcher) {
            crossRefDao.insertCrossRef(
                br.com.teadescomplica.core.database.ExperienceStrategyCrossRefEntity(
                    experienceId = experienceId,
                    strategyId = strategyId,
                )
            )
        }

    override suspend fun disassociateStrategyFromExperience(strategyId: Long, experienceId: Long) =
        withContext(ioDispatcher) {
            val entity =
                br.com.teadescomplica.core.database.ExperienceStrategyCrossRefEntity(
                    experienceId = experienceId,
                    strategyId = strategyId,
                )
            crossRefDao.deleteCrossRef(entity)
        }

    companion object {
        fun create(database: AppDatabase): PersonalStrategyRepository =
            PersonalStrategyRepositoryImpl(
                database.personalStrategyDao(),
                database.experienceStrategyCrossRefDao(),
            )
    }
}
