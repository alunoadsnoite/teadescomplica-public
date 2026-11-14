package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de uma experiência vivida pela pessoa.
 *
 * Representa um registro individual de algo que aconteceu, permitindo que a
 * pessoa construa uma biblioteca privada de experiências para consulta futura.
 *
 * @property id identificador persistido (0 quando ainda não foi salvo).
 * @property scenarioId identificador opcional do cenário relacionado.
 * @property title título da experiência (obrigatório).
 * @property occurredAt data/hora em que a experiência ocorreu (opcional).
 * @property description descrição do que aconteceu.
 * @property whatHelped o que ajudou durante a experiência.
 * @property whatWasDifficult o que foi difícil durante a experiência.
 * @property nextTimeNotes observações para da próxima vez.
 * @property createdAt timestamp (ms) de criação.
 * @property updatedAt timestamp (ms) da última atualização.
 */
data class PersonalExperience(
    val id: Long = 0,
    val scenarioId: Long? = null,
    val title: String,
    val occurredAt: Long? = null,
    val description: String = "",
    val whatHelped: String = "",
    val whatWasDifficult: String = "",
    val nextTimeNotes: String = "",
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    companion object {
        val Empty = PersonalExperience(
            id = 0,
            scenarioId = null,
            title = "",
            occurredAt = null,
            description = "",
            whatHelped = "",
            whatWasDifficult = "",
            nextTimeNotes = "",
            createdAt = 0,
            updatedAt = 0
        )
    }
}
