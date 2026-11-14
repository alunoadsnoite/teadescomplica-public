package br.com.teadescomplica.data.repository

import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.database.ScenarioDao
import br.com.teadescomplica.data.mapper.ScenarioMapper
import br.com.teadescomplica.domain.model.Scenario
import br.com.teadescomplica.domain.repository.ScenarioRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class ScenarioRepositoryImpl(
    private val dao: ScenarioDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : ScenarioRepository {

    override fun getAllScenariosList(): Flow<List<Scenario>> =
        dao.getAllScenarios().map { entities -> entities.map { ScenarioMapper.toDomain(it) } }

    override fun getScenarioById(id: Long): Flow<Scenario?> =
        dao.getScenarioById(id).map { entity ->
            if (entity == null) null else ScenarioMapper.toDomain(entity)
        }

    override suspend fun insertScenario(scenario: Scenario): Long =
        withContext(ioDispatcher) { dao.insertScenario(ScenarioMapper.toEntity(scenario)) }

    override suspend fun updateScenario(scenario: Scenario) =
        withContext(ioDispatcher) { dao.updateScenario(ScenarioMapper.toEntity(scenario)) }

    override suspend fun deleteScenario(id: Long) {
        val entity = withContext(ioDispatcher) { dao.getScenarioById(id).first() }
        if (entity != null) {
            withContext(ioDispatcher) { dao.deleteScenario(entity) }
        }
    }

    companion object {
        fun create(database: AppDatabase): ScenarioRepository =
            ScenarioRepositoryImpl(database.scenarioDao())
    }
}
