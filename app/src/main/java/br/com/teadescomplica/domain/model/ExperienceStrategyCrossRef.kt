package br.com.teadescomplica.domain.model

/**
 * Modelo de domínio do relacionamento muitos-para-muitos entre experiências e estratégias.
 *
 * @property experienceId identificador da experiência associada.
 * @property strategyId identificador da estratégia associada.
 */
data class ExperienceStrategyCrossRef(
    val experienceId: Long,
    val strategyId: Long
)
