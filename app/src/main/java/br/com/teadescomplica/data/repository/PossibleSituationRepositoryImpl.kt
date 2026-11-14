package br.com.teadescomplica.data.repository

import br.com.teadescomplica.core.database.AppDatabase
import br.com.teadescomplica.core.database.PossibleSituationDao
import br.com.teadescomplica.data.mapper.PossibleSituationMapper
import br.com.teadescomplica.domain.model.PossibleSituation
import br.com.teadescomplica.domain.repository.PossibleSituationRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class PossibleSituationRepositoryImpl(
    private val dao: PossibleSituationDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO,
) : PossibleSituationRepository {

    override fun getPossibleSituationsByScenarioId(
        scenarioId: Long
    ): Flow<List<PossibleSituation>> =
        dao.getPossibleSituationsByScenarioId(scenarioId).map { entities ->
            entities.map { PossibleSituationMapper.toDomain(it) }
        }

    override fun getPossibleSituationById(id: Long): Flow<PossibleSituation?> =
        dao.getPossibleSituationById(id).map { entity ->
            if (entity == null) null else PossibleSituationMapper.toDomain(entity)
        }

    override suspend fun insertPossibleSituation(situation: PossibleSituation): Long =
        withContext(ioDispatcher) {
            dao.insertPossibleSituation(PossibleSituationMapper.toEntity(situation))
        }

    override suspend fun updatePossibleSituation(situation: PossibleSituation) =
        withContext(ioDispatcher) {
            dao.updatePossibleSituation(PossibleSituationMapper.toEntity(situation))
        }

    override suspend fun deletePossibleSituation(id: Long) {
        // Primeiro, nula as referências em planos alternativos (SET NULL via DAO)
        val situation = withContext(ioDispatcher) { dao.getPossibleSituationById(id).first() }
        if (situation != null) {
            withContext(ioDispatcher) {
                dao.nullifyAlternativePlanReferences(id)
                dao.deletePossibleSituation(situation)
            }
        }
    }

    override fun getAllPossibleSituations(): Flow<List<PossibleSituation>> =
        dao.getAllPossibleSituations().map { entities ->
            entities.map { PossibleSituationMapper.toDomain(it) }
        }

    companion object {
        fun create(database: AppDatabase): PossibleSituationRepository =
            PossibleSituationRepositoryImpl(database.possibleSituationDao())
    }
}
