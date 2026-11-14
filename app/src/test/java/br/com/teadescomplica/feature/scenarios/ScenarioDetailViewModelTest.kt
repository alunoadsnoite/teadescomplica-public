package br.com.teadescomplica.feature.scenarios

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
class ScenarioDetailViewModelTest : RegressionFixture() {

    @Test
    fun loadsEverySectionAndStopsLoading() = runBlocking {
        val id = scenario()
        preparationRepo().savePreparation(ScenarioPreparation(scenarioId = id, location = "Local"))
        phraseRepo().insertPersonalPhrase(PersonalPhrase(scenarioId = id, text = "Ajuda"))
        val vm =
            keep(
                ScenarioDetailViewModel(
                    scenarioRepo(),
                    preparationRepo(),
                    situationRepo(),
                    planRepo(),
                    phraseRepo(),
                )
            )
        vm.loadScenario(id)
        awaitCondition { vm.uiState.value.phrases.size == 1 && !vm.uiState.value.isLoading }
        assertEquals("Local", vm.uiState.value.preparation!!.location)
    }

    @Test
    fun missingScenarioShowsError() = runBlocking {
        val vm =
            keep(
                ScenarioDetailViewModel(
                    scenarioRepo(),
                    preparationRepo(),
                    situationRepo(),
                    planRepo(),
                    phraseRepo(),
                )
            )
        vm.loadScenario(999)
        awaitCondition { vm.uiState.value.error != null }
        assertFalse(vm.uiState.value.isLoading)
    }
}
