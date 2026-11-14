package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de uma estratégia pessoal criada pela própria pessoa.
 *
 * Representa algo que a pessoa considera útil e pode ser reutilizado em
 * diferentes experiências.
 *
 * @property id identificador persistido (0 quando ainda não foi salvo).
 * @property title título da estratégia.
 * @property description descrição da estratégia.
 * @property createdAt timestamp (ms) de criação.
 * @property updatedAt timestamp (ms) da última atualização.
 */
data class PersonalStrategy(
    val id: Long = 0,
    val title: String,
    val description: String,
    val createdAt: Long = 0,
    val updatedAt: Long = 0
) {
    companion object {
        val Empty = PersonalStrategy(
            id = 0,
            title = "",
            description = "",
            createdAt = 0,
            updatedAt = 0
        )
    }
}
