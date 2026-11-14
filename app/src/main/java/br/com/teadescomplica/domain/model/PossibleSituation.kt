package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio de uma situação possível (imprevisto antecipado).
 *
 * Representa algo que pode acontecer durante a experiência, permitindo que a
 * pessoa se antecipe sem impor classificações clínicas ou probabilísticas.
 *
 * @property id identificador persistido (0 quando ainda não foi salvo).
 * @property scenarioId identificador do cenário associado.
 * @property description descrição da situação possível.
 * @property likelihood probabilidade subjetiva (opcional).
 * @property createdAt timestamp (ms) de criação.
 */
data class PossibleSituation(
    val id: Long = 0,
    val scenarioId: Long,
    val description: String,
    val likelihood: SituationLikelihood? = null,
    val createdAt: Long = 0
)

/**
 * Nível de possibilidade subjetivo informado pela pessoa.
 *
 * Não utiliza classificações clínicas nem cálculos probabilísticos.
 */
enum class SituationLikelihood {
    POSSIBLE,
    UNCERTAIN
}
