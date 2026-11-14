package br.com.teadescomplica.feature

import android.app.Activity
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import br.com.teadescomplica.MainActivity
import java.io.File
import org.junit.*
import org.junit.Assert.*
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LargeFontAccessibilityTest {
    @get:Rule val ui = createAndroidComposeRule<MainActivity>()
    private var originalScale = 1f

    private fun changeScale(value: Float) {
        val descriptor =
            InstrumentationRegistry.getInstrumentation()
                .uiAutomation
                .executeShellCommand("settings put system font_scale $value")
        android.os.ParcelFileDescriptor.AutoCloseInputStream(descriptor).use { it.readBytes() }
    }

    @Before
    fun enlargeFont() {
        originalScale =
            android.provider.Settings.System.getFloat(ui.activity.contentResolver, "font_scale", 1f)
        if (originalScale != 2f) {
            changeScale(2f)
            ui.waitUntil(5000) { ui.activity.resources.configuration.fontScale == 2f }
        }
    }

    @After
    fun restoreFont() {
        if (originalScale != 2f) changeScale(originalScale)
    }

    private fun screenshot(name: String) {
        val device = InstrumentationRegistry.getInstrumentation().uiAutomation
        val bitmap = device.takeScreenshot()
        val directory =
            File(ui.activity.getExternalFilesDir(null), "accessibility-200").apply { mkdirs() }
        File(directory, "$name.png").outputStream().use {
            bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
        bitmap.recycle()
    }

    @Test
    fun homeNavigationAndSettingsRemainReachableAt200Percent() {
        assertEquals(2f, ui.activity.resources.configuration.fontScale, 0.01f)
        ui.onAllNodesWithText("Configurações").onLast().assertIsDisplayed()
        screenshot("01-inicio")
        ui.onNodeWithText("Tudo fica armazenado", substring = true)
            .performScrollTo()
            .assertIsDisplayed()
        ui.onAllNodesWithText("Configurações").onLast().performClick()
        ui.onNodeWithText("Gerenciar backup").performScrollTo().assertIsDisplayed()
        screenshot("02-configuracoes")
        ui.onNodeWithText("Gerenciar backup").performClick()
        ui.onNodeWithText("Criar backup").performScrollTo().performClick()
        ui.onNodeWithText("Confirmar senha").performScrollTo().assertIsDisplayed()
        ui.onNodeWithText("Cancelar").assertIsDisplayed()
        screenshot("03-dialogo-backup")
        ui.onNodeWithText("Cancelar").performClick()
        ui.onNodeWithText("Minha jornada").performClick()
        ui.onNodeWithText("Registrar experiência").assertIsDisplayed()
        ui.onNodeWithText("Estratégias").assertIsDisplayed()
        screenshot("04-jornada")
        ui.onNodeWithText("Registrar experiência").performClick()
        ui.onNode(hasScrollToNodeAction()).performScrollToNode(hasText("Salvar experiência"))
        ui.onNodeWithText("Salvar experiência").assertIsDisplayed()
        screenshot("05-formulario")
    }

    @Test
    fun emailOpensMailComposerWithoutSending() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val filter = IntentFilter(Intent.ACTION_SENDTO).apply { addDataScheme("mailto") }
        val monitor =
            instrumentation.addMonitor(
                filter,
                android.app.Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null),
                true,
            )
        try {
            ui.onAllNodesWithText("Configurações").onLast().performClick()
            ui.onNodeWithText("valdenor.sa@proton.me").performScrollTo().performClick()
            ui.waitForIdle()
            assertEquals(1, monitor.hits)
        } finally {
            instrumentation.removeMonitor(monitor)
        }
    }
}
