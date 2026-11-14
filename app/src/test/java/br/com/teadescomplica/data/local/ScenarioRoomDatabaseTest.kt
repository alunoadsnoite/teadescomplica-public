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
class ScenarioRoomDatabaseTest : RegressionFixture() {

    @Test
    fun scenarioCrudAndOrdering() = runBlocking {
        val id = scenario("Primeiro")
        val second = scenario("Segundo")
        assertEquals(second, scenarioRepo().getAllScenariosList().first().first().id)
        val original = scenarioRepo().getScenarioById(id).first()!!
        scenarioRepo().updateScenario(original.copy(title = "Editado", updatedAt = 10))
        assertEquals("Editado", scenarioRepo().getAllScenariosList().first().first().title)
        scenarioRepo().deleteScenario(id)
        assertNull(scenarioRepo().getScenarioById(id).first())
    }

    @Test
    fun deletingScenarioCascadesChildren() = runBlocking {
        val id = scenario()
        preparationRepo().savePreparation(ScenarioPreparation(scenarioId = id, location = "Local"))
        phraseRepo().insertPersonalPhrase(PersonalPhrase(scenarioId = id, text = "Ajuda"))
        scenarioRepo().deleteScenario(id)
        assertTrue(db.scenarioPreparationDao().getAllPreparations().first().isEmpty())
        assertTrue(db.personalPhraseDao().getAllPersonalPhrases().first().isEmpty())
    }
}
