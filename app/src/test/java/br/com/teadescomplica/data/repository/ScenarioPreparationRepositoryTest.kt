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
class ScenarioPreparationRepositoryTest : RegressionFixture() {

    @Test
    fun repeatedSaveUpdatesOneRow() = runBlocking {
        val id = scenario()
        val p = ScenarioPreparation(scenarioId = id, location = "Original", updatedAt = 1)
        val first = preparationRepo().savePreparation(p)
        val second = preparationRepo().savePreparation(p.copy(location = "Alterado", updatedAt = 2))
        assertEquals(first, second)
        assertEquals(1, db.scenarioPreparationDao().getAllPreparations().first().size)
        assertEquals(
            "Alterado",
            preparationRepo().getPreparationByScenarioId(id).first()!!.location,
        )
    }

    @Test
    fun missingPreparationIsNull() = runBlocking {
        assertNull(preparationRepo().getPreparationByScenarioId(scenario()).first())
    }

    @Test
    fun existingDuplicatesArePreservedAndLatestUpdated() = runBlocking {
        val id = scenario()
        val dao = db.scenarioPreparationDao()
        dao.insertPreparation(
            ScenarioPreparationEntity.fromDomain(
                ScenarioPreparation(scenarioId = id, location = "Antiga", updatedAt = 1)
            )
        )
        dao.insertPreparation(
            ScenarioPreparationEntity.fromDomain(
                ScenarioPreparation(scenarioId = id, location = "Recente", updatedAt = 2)
            )
        )
        preparationRepo()
            .savePreparation(
                ScenarioPreparation(scenarioId = id, location = "Atualizada", updatedAt = 3)
            )
        assertEquals(2, dao.getAllPreparations().first().size)
        assertEquals("Atualizada", dao.getPreparationByScenarioId(id).first()!!.location)
    }
}
