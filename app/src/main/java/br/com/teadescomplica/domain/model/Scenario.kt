package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de um cenário.
 *
 * Independente do Android: não referencia classes do framework. É o contrato
 * usado entre a camada de domínio e a camada de apresentação.
 *
 * @property id identificador persistido (0 quando ainda não foi salvo).
 * @property title título do cenário (obrigatório).
 * @property description descrição da situação.
 * @property objective o que se deseja conseguir nessa situação.
 * @property createdAt timestamp (ms) de criação.
 * @property updatedAt timestamp (ms) da última atualização.
 */
data class Scenario(
    val id: Long = 0,
    val title: String,
    val description: String,
    val objective: String,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    /** Cenário vazio para pré-preencher formulários. */
    companion object {
        val Empty = Scenario(
            id = 0,
            title = "",
            description = "",
            objective = "",
            createdAt = 0,
            updatedAt = 0
        )
    }
}
