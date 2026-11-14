package br.com.teadescomplica.data.repository

import android.app.Application
import br.com.teadescomplica.RegressionFixture
import br.com.teadescomplica.core.database.*
import br.com.teadescomplica.data.crypto.*
import br.com.teadescomplica.data.mapper.*
import br.com.teadescomplica.data.repository.*
import br.com.teadescomplica.domain.model.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class AlternativePlanRepositoryTest : RegressionFixture() {

    @Test
    fun deletingSituationPreservesPlan() = runBlocking {
        val id = scenario()
        val s =
            situationRepo()
                .insertPossibleSituation(PossibleSituation(scenarioId = id, description = "Atraso"))
        planRepo()
            .insertAlternativePlan(
                AlternativePlan(
                    scenarioId = id,
                    possibleSituationId = s,
                    title = "Pedir ajuda",
                    description = "Falar com recepção",
                )
            )
        situationRepo().deletePossibleSituation(s)
        val p = planRepo().getAlternativePlansByScenarioId(id).first().single()
        assertNull(p.possibleSituationId)
        assertEquals("Pedir ajuda", p.title)
    }

    @Test
    fun reorderPersists() = runBlocking {
        val id = scenario()
        val repo = planRepo()
        repo.insertAlternativePlan(
            AlternativePlan(scenarioId = id, title = "A", description = "", position = 0)
        )
        repo.insertAlternativePlan(
            AlternativePlan(scenarioId = id, title = "B", description = "", position = 1)
        )
        repo.reorderAlternativePlans(
            id,
            repo.getAlternativePlansByScenarioId(id).first().reversed(),
        )
        assertEquals(
            listOf("B", "A"),
            repo.getAlternativePlansByScenarioId(id).first().map { it.title },
        )
    }
}
