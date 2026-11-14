package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.PersonalStrategy
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a estratégias pessoais.
 *
 * Uma estratégia pode ser associada a múltiplas experiências e vice-versa.
 */
interface PersonalStrategyRepository {

    /** Observa todas as estratégias, ordenadas por título. */
    fun getAllStrategies(): Flow<List<PersonalStrategy>>

    /** Observa uma estratégia pelo seu identificador. Emite null se não existir. */
    fun getStrategyById(id: Long): Flow<PersonalStrategy?>

    /** Insere uma estratégia e retorna o identificador gerado. */
    suspend fun insertStrategy(strategy: PersonalStrategy): Long

    /** Atualiza uma estratégia existente. */
    suspend fun updateStrategy(strategy: PersonalStrategy)

    /** Exclui uma estratégia pelo seu identificador. */
    suspend fun deleteStrategy(id: Long)

    /** Observa os identificadores das experiências associadas a uma estratégia. */
    fun getExperienceIdsForStrategy(strategyId: Long): Flow<List<Long>>

    /** Associa uma estratégia a uma experiência. Evita duplicatas. */
    suspend fun associateStrategyToExperience(strategyId: Long, experienceId: Long)

    /** Remove a associação entre uma estratégia e uma experiência. */
    suspend fun disassociateStrategyFromExperience(strategyId: Long, experienceId: Long)
}
