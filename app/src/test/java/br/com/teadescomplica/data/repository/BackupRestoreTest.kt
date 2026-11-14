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
class BackupRestoreTest : RegressionFixture() {

    @Test
    fun validSnapshotPreservesIdsRelationshipsAndPreferences() = runBlocking {
        val sid = scenario()
        val strategy =
            strategyRepo()
                .insertStrategy(PersonalStrategy(title = "Pausa", description = "Respirar"))
        experienceRepo()
            .saveExperienceWithStrategies(
                PersonalExperience(scenarioId = sid, title = "Consulta"),
                setOf(strategy),
            )
        val snap = backup.createSnapshot().copy(themePreference = "dark", reducedMotion = true)
        scenarioRepo().deleteScenario(sid)
        assertTrue(backup.applySnapshot(snap))
        assertEquals(snap.scenarios, backup.createSnapshot().scenarios)
        assertEquals(
            snap.experienceStrategyAssociations,
            backup.createSnapshot().experienceStrategyAssociations,
        )
        assertEquals("dark", prefs.getThemePreferenceOnce().key)
        assertTrue(prefs.getReducedMotion().first())
    }

    @Test
    fun invalidSnapshotNeverDeletesCurrentData() = runBlocking {
        val id = scenario("Preservar")
        val bad =
            BackupSnapshot(
                personalPhrases =
                    listOf(PersonalPhrase(id = 1, scenarioId = 999, text = "Inválida"))
            )
        assertFalse(backup.applySnapshot(bad))
        assertEquals("Preservar", scenarioRepo().getScenarioById(id).first()!!.title)
    }

    @Test
    fun emptyValidSnapshotRestoresEmptyDatabase() = runBlocking {
        scenario()
        assertTrue(backup.applySnapshot(BackupSnapshot()))
        assertTrue(scenarioRepo().getAllScenariosList().first().isEmpty())
    }
}
