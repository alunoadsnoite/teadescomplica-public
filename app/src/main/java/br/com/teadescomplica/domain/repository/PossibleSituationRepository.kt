package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.PossibleSituation
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a situações possíveis.
 *
 * Cada cenário pode ter várias situações possíveis (relação um-para-muitos).
 */
interface PossibleSituationRepository {

    /** Observa todas as situações possíveis de um cenário, ordenadas por criação. */
    fun getPossibleSituationsByScenarioId(scenarioId: Long): Flow<List<PossibleSituation>>

    /** Observa todas as situações possíveis de todos os cenários. */
    fun getAllPossibleSituations(): Flow<List<PossibleSituation>>

    /** Observa uma situação possível pelo seu identificador. Emite null se não existir. */
    fun getPossibleSituationById(id: Long): Flow<PossibleSituation?>

    /** Insere uma situação possível e retorna o identificador gerado. */
    suspend fun insertPossibleSituation(situation: PossibleSituation): Long

    /** Atualiza uma situação possível existente. */
    suspend fun updatePossibleSituation(situation: PossibleSituation)

    /** Exclui uma situação possível. Planos associados devem ter possibleSituationId definido como nulo. */
    suspend fun deletePossibleSituation(id: Long)
}
