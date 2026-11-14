package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.Scenario
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a cenários.
 *
 * Implementado pela camada de dados (Room + DataStore). A camada de domínio
 * e a de apresentação dependem apenas desta interface.
 */
interface ScenarioRepository {
    /** Observa todos os cenários, ordenados do mais recente para o mais antigo. */
    fun getAllScenariosList(): Flow<List<Scenario>>

    /** Observa um cenário pelo seu identificador. Emite null se não existir. */
    fun getScenarioById(id: Long): Flow<Scenario?>

    /** Insere um cenário e retorna o identificador gerado. */
    suspend fun insertScenario(scenario: Scenario): Long

    /** Atualiza um cenário existente. */
    suspend fun updateScenario(scenario: Scenario)

    /** Exclui um cenário pelo seu identificador. */
    suspend fun deleteScenario(id: Long)
}
