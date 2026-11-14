package br.com.teadescomplica.data.mapper

import br.com.teadescomplica.domain.model.*
import com.google.gson.JsonParser
import org.junit.Assert.*
import org.junit.Test

class BackupSnapshotSerializerTest {
    private fun snapshot() =
        BackupSnapshot(
            exportedAt = 100,
            scenarios =
                listOf(
                    Scenario(
                        id = 1,
                        title = "Consulta \"médica\" \n ç 😀",
                        description = "Texto com \\ e [ ] { }",
                        objective = "Preparar",
                        createdAt = 1,
                        updatedAt = 2,
                    )
                ),
            scenarioPreparations =
                listOf(
                    ScenarioPreparation(scenarioId = 1, location = "Hospital", scheduledAt = 1234)
                ),
            possibleSituations =
                listOf(
                    PossibleSituation(
                        id = 2,
                        scenarioId = 1,
                        description = "Atraso",
                        likelihood = SituationLikelihood.POSSIBLE,
                    )
                ),
            alternativePlans =
                listOf(
                    AlternativePlan(
                        id = 3,
                        scenarioId = 1,
                        possibleSituationId = 2,
                        title = "Pausa",
                        description = "Água",
                    )
                ),
            personalPhrases = listOf(PersonalPhrase(id = 4, scenarioId = 1, text = "Ajuda")),
            personalExperiences =
                listOf(
                    PersonalExperience(
                        id = 5,
                        scenarioId = 1,
                        title = "Experiência",
                        occurredAt = 6789,
                    )
                ),
            personalStrategies =
                listOf(PersonalStrategy(id = 6, title = "Respirar", description = "Calma")),
            experienceStrategyAssociations = listOf(ExperienceStrategyCrossRef(5, 6)),
            themePreference = "dark",
            reducedMotion = true,
        )

    @Test
    fun completeRoundTripPreservesEveryField() {
        val s = snapshot()
        val json = BackupSnapshotSerializer.serialize(s)
        assertNotNull(JsonParser.parseString(json))
        assertEquals(s, BackupSnapshotSerializer.deserialize(json))
    }

    @Test
    fun emptySnapshotRoundTrip() {
        val s = BackupSnapshot(exportedAt = 10)
        assertEquals(s, BackupSnapshotSerializer.deserialize(BackupSnapshotSerializer.serialize(s)))
    }

    @Test
    fun malformedAndMissingFieldsRejected() {
        for (s in
            listOf(
                "invalid",
                "{}",
                "null",
                "[]",
                "{\"formatVersion\":1}",
                BackupSnapshotSerializer.serialize(snapshot()) + " garbage",
            )) assertNull(s, BackupSnapshotSerializer.deserialize(s))
    }

    @Test
    fun wrongTypesAndVersionsRejected() {
        val json = BackupSnapshotSerializer.serialize(snapshot())
        assertNull(
            BackupSnapshotSerializer.deserialize(
                json.replace("\"formatVersion\": 1", "\"formatVersion\": 99")
            )
        )
        assertNull(
            BackupSnapshotSerializer.deserialize(
                json.replace("\"reducedMotion\": true", "\"reducedMotion\": \"true\"")
            )
        )
    }

    @Test
    fun invalidRelationsAndDuplicatesRejected() {
        val s = snapshot()
        assertFalse(
            BackupSnapshotSerializer.isValid(
                s.copy(
                    personalPhrases =
                        listOf(PersonalPhrase(id = 4, scenarioId = 999, text = "Ajuda"))
                )
            )
        )
        assertFalse(BackupSnapshotSerializer.isValid(s.copy(scenarios = s.scenarios + s.scenarios)))
        assertFalse(
            BackupSnapshotSerializer.isValid(
                s.copy(experienceStrategyAssociations = listOf(ExperienceStrategyCrossRef(999, 6)))
            )
        )
    }

    @Test
    fun nullOptionalFieldsAndLargeIdsSurvive() {
        val s =
            snapshot()
                .copy(
                    scenarios =
                        listOf(
                            Scenario(
                                id = 9999999999L,
                                title = "Título",
                                description = "",
                                objective = "",
                                createdAt = 0,
                                updatedAt = 0,
                            )
                        ),
                    scenarioPreparations = emptyList(),
                    possibleSituations = emptyList(),
                    alternativePlans = emptyList(),
                    personalPhrases = emptyList(),
                    personalExperiences =
                        listOf(
                            PersonalExperience(
                                id = 1,
                                title = "Livre",
                                scenarioId = null,
                                occurredAt = null,
                            )
                        ),
                    experienceStrategyAssociations = emptyList(),
                )
        assertEquals(s, BackupSnapshotSerializer.deserialize(BackupSnapshotSerializer.serialize(s)))
    }
}
