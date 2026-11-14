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
class BackupRepositoryTest : RegressionFixture() {

    @Test
    fun snapshotContainsAllDataAndPreferences() = runBlocking {
        val id = scenario()
        preparationRepo()
            .savePreparation(ScenarioPreparation(scenarioId = id, location = "Hospital"))
        val s =
            strategyRepo()
                .insertStrategy(PersonalStrategy(title = "Pausa", description = "Respirar"))
        experienceRepo()
            .saveExperienceWithStrategies(
                PersonalExperience(scenarioId = id, title = "Consulta"),
                setOf(s),
            )
        prefs.setThemePreference(br.com.teadescomplica.core.datastore.ThemePreference.DARK)
        prefs.setReducedMotion(true)
        val snap = backup.createSnapshot()
        assertEquals(1, snap.scenarios.size)
        assertEquals(1, snap.scenarioPreparations.size)
        assertEquals(1, snap.experienceStrategyAssociations.size)
        assertEquals("dark", snap.themePreference)
        assertTrue(snap.reducedMotion)
        assertEquals(
            snap,
            BackupSnapshotSerializer.deserialize(BackupSnapshotSerializer.serialize(snap)),
        )
    }

    @Test
    fun exportUnavailableDestinationDoesNotClaimSuccess() = runBlocking {
        val ok =
            backup.exportEncryptedBackup(
                backup.createSnapshot(),
                "senha1234",
                android.net.Uri.parse("content://missing.provider/backup"),
            )
        assertFalse(ok)
        assertNotNull(backup.getLastError().value)
        assertFalse(backup.isBackupInProgress().value)
    }

    @Test
    fun missingImportClearsBusyFlag() = runBlocking {
        assertNull(
            backup.importEncryptedBackup(
                android.net.Uri.parse("content://missing.provider/backup"),
                "senha1234",
            )
        )
        assertFalse(backup.isRestoreInProgress().value)
    }
}
