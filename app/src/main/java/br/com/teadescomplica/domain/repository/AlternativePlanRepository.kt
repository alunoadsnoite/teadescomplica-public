package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.AlternativePlan
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a planos alternativos.
 *
 * Cada cenário pode ter vários planos alternativos (relação um-para-muitos).
 * Um plano pode estar opcionalmente associado a uma situação possível.
 */
interface AlternativePlanRepository {

    /** Observa todos os planos alternativos de um cenário, ordenados por posição. */
    fun getAlternativePlansByScenarioId(scenarioId: Long): Flow<List<AlternativePlan>>

    /** Observa todos os planos alternativos de todos os cenários. */
    fun getAllAlternativePlans(): Flow<List<AlternativePlan>>

    /** Observa um plano alternativo pelo seu identificador. Emite null se não existir. */
    fun getAlternativePlanById(id: Long): Flow<AlternativePlan?>

    /** Insere um plano alternativo e retorna o identificador gerado. */
    suspend fun insertAlternativePlan(plan: AlternativePlan): Long

    /** Atualiza um plano alternativo existente. */
    suspend fun updateAlternativePlan(plan: AlternativePlan)

    /** Exclui um plano alternativo pelo seu identificador. */
    suspend fun deleteAlternativePlan(id: Long)

    /** Reordena planos alternativos de um cenário. */
    suspend fun reorderAlternativePlans(scenarioId: Long, plans: List<AlternativePlan>)
}
