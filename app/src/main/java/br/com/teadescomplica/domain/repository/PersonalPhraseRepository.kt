package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.PersonalPhrase
import kotlinx.coroutines.flow.Flow

/**
 * Contrato de acesso a frases pessoais.
 *
 * Cada cenário pode ter várias frases pessoais (relação um-para-muitos).
 */
interface PersonalPhraseRepository {

    /** Observa todas as frases pessoais de um cenário, ordenadas por posição. */
    fun getPersonalPhrasesByScenarioId(scenarioId: Long): Flow<List<PersonalPhrase>>

    /** Observa todas as frases pessoais de todos os cenários. */
    fun getAllPersonalPhrases(): Flow<List<PersonalPhrase>>

    /** Observa uma frase pessoal pelo seu identificador. Emite null se não existir. */
    fun getPersonalPhraseById(id: Long): Flow<PersonalPhrase?>

    /** Insere uma frase pessoal e retorna o identificador gerado. */
    suspend fun insertPersonalPhrase(phrase: PersonalPhrase): Long

    /** Atualiza uma frase pessoal existente. */
    suspend fun updatePersonalPhrase(phrase: PersonalPhrase)

    /** Exclui uma frase pessoal pelo seu identificador. */
    suspend fun deletePersonalPhrase(id: Long)

    /** Reordena frases pessoais de um cenário. */
    suspend fun reorderPersonalPhrases(scenarioId: Long, phrases: List<PersonalPhrase>)
}
