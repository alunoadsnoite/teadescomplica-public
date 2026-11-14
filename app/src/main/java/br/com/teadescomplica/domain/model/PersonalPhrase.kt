package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de uma frase pessoal.
 *
 * Frase criada e controlada pela própria pessoa, associada a um cenário.
 * Pode ser copiada para a área de transferência.
 *
 * @property id identificador persistido (0 quando ainda não foi salvo).
 * @property scenarioId identificador do cenário associado.
 * @property text texto da frase.
 * @property position posição para ordenação manual.
 * @property createdAt timestamp (ms) de criação.
 */
data class PersonalPhrase(
    val id: Long = 0,
    val scenarioId: Long,
    val text: String,
    val position: Int = 0,
    val createdAt: Long = 0
)
