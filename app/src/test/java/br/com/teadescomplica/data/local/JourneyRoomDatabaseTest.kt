package br.com.teadescomplica.data.local

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
class JourneyRoomDatabaseTest : RegressionFixture() {

    @Test
    fun deletingScenarioPreservesExperience() = runBlocking {
        val id = scenario()
        val e =
            experienceRepo()
                .insertExperience(PersonalExperience(scenarioId = id, title = "Experiência"))
        scenarioRepo().deleteScenario(id)
        assertNull(experienceRepo().getExperienceById(e).first()!!.scenarioId)
    }

    @Test
    fun deletingStrategyPreservesExperienceAndRemovesLink() = runBlocking {
        val s =
            strategyRepo()
                .insertStrategy(PersonalStrategy(title = "Respirar", description = "Pausa"))
        val e =
            experienceRepo()
                .saveExperienceWithStrategies(PersonalExperience(title = "Consulta"), setOf(s))
        strategyRepo().deleteStrategy(s)
        assertNotNull(experienceRepo().getExperienceById(e).first())
        assertTrue(experienceRepo().getStrategiesForExperience(e).first().isEmpty())
    }
}
