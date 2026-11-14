package br.com.teadescomplica.data.repository

import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.database.ScenarioPreparationDao
import br.com.teadescomplica.data.mapper.ScenarioPreparationMapper
import br.com.teadescomplica.domain.model.ScenarioPreparation
import br.com.teadescomplica.domain.repository.ScenarioPreparationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ScenarioPreparationRepositoryImpl(
    private val dao: ScenarioPreparationDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ScenarioPreparationRepository {

    override fun getPreparationByScenarioId(scenarioId: Long): Flow<ScenarioPreparation?> =
        dao.getPreparationByScenarioId(scenarioId).map { entity ->
            if (entity == null) null else ScenarioPreparationMapper.toDomain(entity)
        }

    override suspend fun savePreparation(preparation: ScenarioPreparation): Long =
        withContext(ioDispatcher) {
            dao.savePreparation(ScenarioPreparationMapper.toEntity(preparation))
        }

    override suspend fun deletePreparation(scenarioId: Long) =
        withContext(ioDispatcher) { dao.deletePreparationByScenarioId(scenarioId) }

    override fun getAllPreparations(): Flow<List<ScenarioPreparation>> =
        dao.getAllPreparations().map { entities ->
            entities.map { ScenarioPreparationMapper.toDomain(it) }
        }

    companion object {
        fun create(database: AppDatabase): ScenarioPreparationRepository =
            ScenarioPreparationRepositoryImpl(database.scenarioPreparationDao())
    }
}
