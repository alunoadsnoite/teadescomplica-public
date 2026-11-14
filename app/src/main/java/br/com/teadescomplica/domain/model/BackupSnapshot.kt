package br.com.teadescomplica.domain.model

/**
 * Snapshot consistente de todos os dados do aplicativo para backup/restauração.
 *
 * Este modelo é independente do Android e contém todos os dados persistentes
 * necessários para reconstruir o estado do aplicativo a partir de um backup.
 *
 * @property formatVersion versão do formato de backup (atualmente 1).
 * @property exportedAt timestamp (ms) de quando o backup foi criado.
 * @property scenarios lista de cenários.
 * @property scenarioPreparations lista de preparações de cenário.
 * @property possibleSituations lista de situações possíveis.
 * @property alternativePlans lista de planos alternativos.
 * @property personalPhrases lista de frases pessoais.
 * @property personalExperiences lista de experiências vividas.
 * @property personalStrategies lista de estratégias pessoais.
 * @property experienceStrategyAssociations lista de associações muitos-para-muitos.
 * @property themePreference preferência de tema (sistema/claro/escuro).
 * @property reducedMotion preferência de animações reduzidas.
 */
data class BackupSnapshot(
    val formatVersion: Int = 1,
    val exportedAt: Long = System.currentTimeMillis(),
    val scenarios: List<Scenario> = emptyList(),
    val scenarioPreparations: List<ScenarioPreparation> = emptyList(),
    val possibleSituations: List<PossibleSituation> = emptyList(),
    val alternativePlans: List<AlternativePlan> = emptyList(),
    val personalPhrases: List<PersonalPhrase> = emptyList(),
    val personalExperiences: List<PersonalExperience> = emptyList(),
    val personalStrategies: List<PersonalStrategy> = emptyList(),
    val experienceStrategyAssociations: List<ExperienceStrategyCrossRef> = emptyList(),
    val themePreference: String = "system",
    val reducedMotion: Boolean = false
)
