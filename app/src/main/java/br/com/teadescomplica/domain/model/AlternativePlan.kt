package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de um plano alternativo.
 *
 * Permite que a pessoa prepare o que fazer se uma situação possível ocorrer.
 * Pode estar associada a uma situação possível ou ser independente.
 *
 * @property id identificador persistido (0 quando ainda não foi salvo).
 * @property scenarioId identificador do cenário associado.
 * @property possibleSituationId identificador da situação possível associada (opcional).
 * @property title título do plano alternativo.
 * @property description descrição detalhada do plano.
 * @property position posição para ordenação manual.
 * @property createdAt timestamp (ms) de criação.
 * @property updatedAt timestamp (ms) da última atualização.
 */
data class AlternativePlan(
    val id: Long = 0,
    val scenarioId: Long,
    val possibleSituationId: Long? = null,
    val title: String,
    val description: String,
    val position: Int = 0,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
)
