package br.com.teadescomplica.feature.scenarios

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.teadescomplica.MainActivity
import br.com.teadescomplica.core.di.DiModule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ScenarioAccessibilityTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private val prefix = "Teste automático ${System.nanoTime()}"

    @After
    fun cleanupOnlyTestRecords() = runBlocking {
        val db = DiModule.getDatabase()
        db.personalExperienceDao()
            .getAllExperiences()
            .first()
            .filter { it.title.startsWith(prefix) }
            .forEach { db.personalExperienceDao().deleteExperience(it) }
        db.personalStrategyDao()
            .getAllStrategies()
            .first()
            .filter { it.title.startsWith(prefix) }
            .forEach { db.personalStrategyDao().deleteStrategy(it) }
        db.scenarioDao()
            .getAllScenarios()
            .first()
            .filter { it.title.startsWith(prefix) }
            .forEach { db.scenarioDao().deleteScenario(it) }
    }

    @Test
    fun scenarioPreparationCanBeCreatedEditedAndQueried() {
        ui.onAllNodesWithText("Meus cenários").onLast().performClick()
        ui.onNodeWithText("Criar cenário").performClick()
        ui.onNodeWithText("Título").performTextInput("$prefix cenário")
        ui.onNodeWithText("Salvar").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Novo cenário").fetchSemanticsNodes().isEmpty() &&
                ui.onAllNodesWithText("$prefix cenário").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("$prefix cenário").performClick()
        ui.onNodeWithText("Adicionar preparação").performClick()
        ui.onNodeWithText("Local").performTextInput("Hospital A")
        ui.onNodeWithText("Salvar").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Local: Hospital A").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("Editar preparação").performClick()
        ui.onNodeWithText("Local").performTextReplacement("Hospital B")
        ui.onNodeWithText("Salvar").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Local: Hospital B").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("Consultar").performClick()
        ui.onNodeWithText("Hospital B", substring = true).assertIsDisplayed()
    }

    @Test
    fun strategyAndExperienceCanBeSavedAndEdited() {
        ui.onNodeWithText("Minha jornada").performClick()
        ui.onNodeWithText("Estratégias").performClick()
        ui.onNodeWithText("Criar estratégia").performClick()
        ui.onNodeWithText("Título").performTextInput("$prefix estratégia")
        ui.onNodeWithText("Descrição").performTextInput("Pedir uma pausa")
        ui.onNodeWithText("Salvar estratégia").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Criar estratégia").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("$prefix estratégia").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Editar estratégia").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("Pedir uma pausa").assertIsDisplayed()
        ui.onNodeWithText("Editar estratégia").performClick()
        ui.onNodeWithText("Descrição").performTextReplacement("Pedir água")
        ui.onNodeWithText("Salvar estratégia").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Editar estratégia").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("Pedir água").assertIsDisplayed()
        ui.onNodeWithText("Minha jornada").performClick()
        ui.onNodeWithText("Registrar experiência").performClick()
        ui.onNodeWithText("Título").performTextInput("$prefix experiência")
        ui.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("$prefix estratégia"))
        ui.onNodeWithText("$prefix estratégia").performClick()
        ui.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Salvar experiência"))
        ui.onNodeWithText("Salvar experiência").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Pesquisar experiências").fetchSemanticsNodes().isNotEmpty() &&
                ui.onAllNodesWithText("$prefix experiência").fetchSemanticsNodes().isNotEmpty()
        }
        ui.onNodeWithText("$prefix experiência").performClick()
        ui.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("$prefix estratégia"))
        ui.onNodeWithText("$prefix estratégia").assertExists()
        ui.onNodeWithContentDescription("Editar experiência").performClick()
        ui.onNodeWithText("Título").performTextReplacement("$prefix experiência editada")
        ui.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Salvar experiência"))
        ui.onNodeWithText("Salvar experiência").performClick()
        ui.waitUntil(5000) {
            ui.onAllNodesWithText("Pesquisar experiências").fetchSemanticsNodes().isNotEmpty() &&
                ui.onAllNodesWithText("$prefix experiência editada")
                    .fetchSemanticsNodes()
                    .isNotEmpty()
        }
    }
}
