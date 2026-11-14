package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de uma preparação de cenário.
 *
 * Representa a preparação individualizada de uma experiência cotidiana,
 * associada a um cenário em relação um-para-um.
 *
 * @property scenarioId identificador do cenário associado.
 * @property location local onde ocorrerá a experiência (texto informado pela pessoa).
 * @property scheduledAt data/hora prevista (opcional, em milissegundos).
 * @property whatToExpect o que a pessoa espera encontrar.
 * @property whatToBring o que a pessoa precisa levar.
 * @property importantInformation informações importantes a recordar.
 * @property personalNotes observações pessoais.
 * @property updatedAt timestamp (ms) da última atualização.
 */
data class ScenarioPreparation(
    val scenarioId: Long,
    val location: String = "",
    val scheduledAt: Long? = null,
    val whatToExpect: String = "",
    val whatToBring: String = "",
    val importantInformation: String = "",
    val personalNotes: String = "",
    val updatedAt: Long = 0
) {
    companion object {
        val Empty = ScenarioPreparation(
            scenarioId = 0,
            location = "",
            scheduledAt = null,
            whatToExpect = "",
            whatToBring = "",
            importantInformation = "",
            personalNotes = "",
            updatedAt = 0
        )
    }
}
