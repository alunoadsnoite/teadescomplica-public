package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.domain.model.*
import com.google.gson.*
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import java.io.StringReader

/** Format 1 JSON. Reject malformed data before any destructive restore. */
object BackupSnapshotSerializer {
    private val gson =
        GsonBuilder().setPrettyPrinting().disableHtmlEscaping().serializeNulls().create()

    fun escapeJson(value: String): String = gson.toJson(value)

    fun serialize(snapshot: BackupSnapshot): String {
        require(isValid(snapshot)) { "Snapshot inválido" }
        return gson.toJson(snapshot)
    }

    fun deserialize(json: String): BackupSnapshot? =
        try {
            val reader = JsonReader(StringReader(json)).apply { strictness = Strictness.STRICT }
            val root = JsonParser.parseReader(reader).asJsonObject
            require(reader.peek() == JsonToken.END_DOCUMENT)
            fun objectFields(
                o: JsonObject,
                strings: List<String>,
                longs: List<String>,
                nullableLongs: List<String> = emptyList(),
            ) {
                strings.forEach {
                    require(o[it]?.isJsonPrimitive == true && o[it].asJsonPrimitive.isString)
                }
                longs.forEach { number(o[it], false) }
                nullableLongs.forEach { number(o[it], true) }
            }
            objectFields(root, listOf("themePreference"), listOf("formatVersion", "exportedAt"))
            require(
                root["reducedMotion"]?.isJsonPrimitive == true &&
                    root["reducedMotion"].asJsonPrimitive.isBoolean
            )
            fun array(
                name: String,
                strings: List<String>,
                longs: List<String>,
                nullable: List<String> = emptyList(),
            ): List<JsonObject> {
                require(root[name]?.isJsonArray == true)
                return root[name].asJsonArray.map { element ->
                    val o = element.asJsonObject
                    objectFields(o, strings, longs, nullable)
                    o
                }
            }
            array(
                "scenarios",
                listOf("title", "description", "objective"),
                listOf("id", "createdAt", "updatedAt"),
            )
            array(
                "scenarioPreparations",
                listOf(
                    "location",
                    "whatToExpect",
                    "whatToBring",
                    "importantInformation",
                    "personalNotes",
                ),
                listOf("scenarioId", "updatedAt"),
                listOf("scheduledAt"),
            )
            array(
                    "possibleSituations",
                    listOf("description"),
                    listOf("id", "scenarioId", "createdAt"),
                )
                .forEach {
                    val likelihood = it["likelihood"] ?: error("Possibilidade ausente")
                    require(
                        likelihood.isJsonNull ||
                            (likelihood.isJsonPrimitive &&
                                likelihood.asJsonPrimitive.isString &&
                                likelihood.asString in setOf("POSSIBLE", "UNCERTAIN"))
                    )
                }
            array(
                "alternativePlans",
                listOf("title", "description"),
                listOf("id", "scenarioId", "position", "createdAt", "updatedAt"),
                listOf("possibleSituationId"),
            )
            array(
                "personalPhrases",
                listOf("text"),
                listOf("id", "scenarioId", "position", "createdAt"),
            )
            array(
                "personalExperiences",
                listOf("title", "description", "whatHelped", "whatWasDifficult", "nextTimeNotes"),
                listOf("id", "createdAt", "updatedAt"),
                listOf("scenarioId", "occurredAt"),
            )
            array(
                "personalStrategies",
                listOf("title", "description"),
                listOf("id", "createdAt", "updatedAt"),
            )
            array(
                "experienceStrategyAssociations",
                emptyList(),
                listOf("experienceId", "strategyId"),
            )
            gson.fromJson(root, BackupSnapshot::class.java).takeIf(::isValid)
        } catch (_: Exception) {
            null
        }

    private fun number(element: JsonElement?, nullable: Boolean) {
        require(element != null)
        if (nullable && element.isJsonNull) return
        require(element.isJsonPrimitive && element.asJsonPrimitive.isNumber)
        element.asBigDecimal.longValueExact()
    }

    fun isValid(s: BackupSnapshot): Boolean {
        if (
            s.formatVersion != 1 ||
                s.exportedAt < 0 ||
                s.themePreference !in setOf("system", "light", "dark")
        )
            return false
        fun ids(values: List<Long>) = values.all { it > 0 } && values.distinct().size == values.size
        if (
            !ids(s.scenarios.map { it.id }) ||
                !ids(s.possibleSituations.map { it.id }) ||
                !ids(s.alternativePlans.map { it.id }) ||
                !ids(s.personalPhrases.map { it.id }) ||
                !ids(s.personalExperiences.map { it.id }) ||
                !ids(s.personalStrategies.map { it.id })
        )
            return false
        val scenarios = s.scenarios.map { it.id }.toSet()
        val situations = s.possibleSituations.associateBy { it.id }
        val experiences = s.personalExperiences.map { it.id }.toSet()
        val strategies = s.personalStrategies.map { it.id }.toSet()
        if (
            s.scenarios.any { it.title.isBlank() } ||
                s.personalExperiences.any { it.title.isBlank() } ||
                s.personalStrategies.any { it.title.isBlank() }
        )
            return false
        if (
            s.scenarioPreparations.any { it.scenarioId !in scenarios } ||
                s.scenarioPreparations.map { it.scenarioId }.distinct().size !=
                    s.scenarioPreparations.size
        )
            return false
        if (s.possibleSituations.any { it.scenarioId !in scenarios || it.description.isBlank() })
            return false
        if (
            s.alternativePlans.any {
                it.scenarioId !in scenarios ||
                    it.title.isBlank() ||
                    it.position < 0 ||
                    (it.possibleSituationId != null &&
                        situations[it.possibleSituationId]?.scenarioId != it.scenarioId)
            }
        )
            return false
        if (
            s.personalPhrases.any {
                it.scenarioId !in scenarios || it.text.isBlank() || it.position < 0
            }
        )
            return false
        if (s.personalExperiences.any { it.scenarioId != null && it.scenarioId !in scenarios })
            return false
        if (
            s.experienceStrategyAssociations.any {
                it.experienceId !in experiences || it.strategyId !in strategies
            }
        )
            return false
        return s.experienceStrategyAssociations.distinct().size ==
            s.experienceStrategyAssociations.size
    }
}
