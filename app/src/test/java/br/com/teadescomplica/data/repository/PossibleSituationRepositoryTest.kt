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
class PossibleSituationRepositoryTest : RegressionFixture() {

    @Test
    fun situationsCanBeEdited() = runBlocking {
        val id = scenario()
        val repo = situationRepo()
        val s =
            repo.insertPossibleSituation(
                PossibleSituation(
                    scenarioId = id,
                    description = "Espera",
                    likelihood = SituationLikelihood.POSSIBLE,
                )
            )
        val original = repo.getPossibleSituationsByScenarioId(id).first().single()
        repo.updatePossibleSituation(original.copy(description = "Atraso", likelihood = null))
        assertEquals(
            "Atraso",
            repo.getPossibleSituationsByScenarioId(id).first().single().description,
        )
        repo.deletePossibleSituation(s)
        assertTrue(repo.getPossibleSituationsByScenarioId(id).first().isEmpty())
    }
}
