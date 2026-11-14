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
class PreparationQueryViewModelTest : RegressionFixture() {

    @Test
    fun queryLoadsPreparationAndPhrases() = runBlocking {
        val id = scenario()
        preparationRepo()
            .savePreparation(
                ScenarioPreparation(scenarioId = id, importantInformation = "Levar documentos")
            )
        phraseRepo()
            .insertPersonalPhrase(PersonalPhrase(scenarioId = id, text = "Preciso de ajuda"))
        val vm =
            keep(
                PreparationQueryViewModel(
                    scenarioRepo(),
                    situationRepo(),
                    planRepo(),
                    preparationRepo(),
                    phraseRepo(),
                )
            )
        vm.loadScenario(id)
        awaitCondition { vm.uiState.value.phrases.size == 1 }
        assertEquals("Levar documentos", vm.uiState.value.preparation!!.importantInformation)
    }
}
