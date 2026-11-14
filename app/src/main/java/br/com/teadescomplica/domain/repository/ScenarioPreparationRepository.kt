package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.ScenarioPreparation
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a preparações de cenário.
 *
 * Cada cenário pode ter no máximo uma preparação (relação um-para-um).
 */
interface ScenarioPreparationRepository {

    /** Observa a preparação de um cenário específico. Emite null se não existir. */
    fun getPreparationByScenarioId(scenarioId: Long): Flow<ScenarioPreparation?>

    /** Observa todas as preparações de cenário. */
    fun getAllPreparations(): Flow<List<ScenarioPreparation>>

    /** Insere ou atualiza uma preparação. Retorna o identificador. */
    suspend fun savePreparation(preparation: ScenarioPreparation): Long

    /** Exclui a preparação de um cenário. */
    suspend fun deletePreparation(scenarioId: Long)
}
