package br.com.teadescomplica.data.repository

import br.com.teadescomplica.core.database.AlternativePlanDao
import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.data.mapper.AlternativePlanMapper
import br.com.teadescomplica.domain.model.AlternativePlan
import br.com.teadescomplica.domain.repository.AlternativePlanRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class AlternativePlanRepositoryImpl(
    private val dao: AlternativePlanDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : AlternativePlanRepository {

    override fun getAlternativePlansByScenarioId(scenarioId: Long): Flow<List<AlternativePlan>> =
        dao.getAlternativePlansByScenarioId(scenarioId).map { entities ->
            entities.map { AlternativePlanMapper.toDomain(it) }
        }

    override fun getAlternativePlanById(id: Long): Flow<AlternativePlan?> =
        dao.getAlternativePlanById(id).map { entity ->
            if (entity == null) null else AlternativePlanMapper.toDomain(entity)
        }

    override suspend fun insertAlternativePlan(plan: AlternativePlan): Long =
        withContext(ioDispatcher) {
            dao.insertAlternativePlan(AlternativePlanMapper.toEntity(plan))
        }

    override suspend fun updateAlternativePlan(plan: AlternativePlan) =
        withContext(ioDispatcher) {
            dao.updateAlternativePlan(AlternativePlanMapper.toEntity(plan))
        }

    override suspend fun deleteAlternativePlan(id: Long) {
        val entity = withContext(ioDispatcher) { dao.getAlternativePlanById(id).first() }
        if (entity != null) {
            withContext(ioDispatcher) { dao.deleteAlternativePlan(entity) }
        }
    }

    override suspend fun reorderAlternativePlans(scenarioId: Long, plans: List<AlternativePlan>) =
        withContext(ioDispatcher) {
            dao.reorder(scenarioId, plans.map { AlternativePlanMapper.toEntity(it) })
        }

    override fun getAllAlternativePlans(): Flow<List<AlternativePlan>> =
        dao.getAllAlternativePlans().map { entities ->
            entities.map { AlternativePlanMapper.toDomain(it) }
        }

    companion object {
        fun create(database: AppDatabase): AlternativePlanRepository =
            AlternativePlanRepositoryImpl(database.alternativePlanDao())
    }
}
