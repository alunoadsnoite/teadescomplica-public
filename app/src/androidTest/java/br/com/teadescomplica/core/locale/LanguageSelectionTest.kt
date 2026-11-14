package br.com.teadescomplica.core.locale

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import br.com.teadescomplica.MainActivity
import br.com.teadescomplica.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LanguageSelectionTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()

    @Test
    fun changesLanguageAndRetainsSelectionAfterRecreation() {
        val original = AppLanguages.selected(ui.activity)
        try {
            ui.onAllNodesWithText(ui.activity.getString(R.string.button_settings)).onLast().performClick()
            ui.onNodeWithText("Español").performScrollTo().performClick()
            ui.waitUntil(5_000) {
                ui.onAllNodesWithText("Configuración").fetchSemanticsNodes().isNotEmpty()
            }
            ui.onNodeWithText("Español").assertIsSelected()

            ui.activityRule.scenario.recreate()
            ui.onNodeWithText("Español").assertIsSelected()
            ui.onNodeWithText("English (US)").performScrollTo().performClick()
            ui.waitUntil(5_000) {
                ui.onAllNodesWithText("Language").fetchSemanticsNodes().isNotEmpty()
            }
            ui.onNodeWithText("English (US)").assertIsSelected()
        } finally {
            ui.activityRule.scenario.onActivity {
                if (AppLanguages.select(it, original)) it.recreate()
            }
        }
    }
}
