package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.ExperienceStrategyCrossRef
import br.com.teadescomplica.domain.model.PersonalExperience
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a experiências pessoais.
 *
 * Uma experiência pode existir sem cenário associado (scenarioId nulo). Múltiplas experiências
 * podem estar relacionadas ao mesmo cenário.
 */
interface PersonalExperienceRepository {

    /** Observa todas as experiências, ordenadas por data de ocorrência (mais recente primeiro). */
    fun getAllExperiences(): Flow<List<PersonalExperience>>

    /** Observa uma experiência pelo seu identificador. Emite null se não existir. */
    fun getExperienceById(id: Long): Flow<PersonalExperience?>

    /** Observa todas as experiências de um cenário específico. */
    fun getExperiencesByScenarioId(scenarioId: Long): Flow<List<PersonalExperience>>

    /** Pesquisa experiências por palavras-chave no título, descrição e observações. */
    fun searchExperiences(query: String): Flow<List<PersonalExperience>>

    /** Insere uma experiência e retorna o identificador gerado. */
    suspend fun insertExperience(experience: PersonalExperience): Long

    suspend fun saveExperienceWithStrategies(
        experience: PersonalExperience,
        strategyIds: Set<Long>,
    ): Long

    /** Atualiza uma experiência existente. */
    suspend fun updateExperience(experience: PersonalExperience)

    /** Exclui uma experiência pelo seu identificador. */
    suspend fun deleteExperience(id: Long)

    /** Observa as estratégias associadas a uma experiência. */
    fun getStrategiesForExperience(experienceId: Long): Flow<List<Long>>

    /** Observa todas as associações experiência-estratégia. */
    fun getAllExperienceStrategyAssociations(): Flow<List<ExperienceStrategyCrossRef>>
}
