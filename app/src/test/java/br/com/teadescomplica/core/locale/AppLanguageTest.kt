package br.com.teadescomplica.core.locale

import android.app.Application
import android.content.Context
import androidx.test.core.app.ApplicationProvider
import br.com.teadescomplica.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28], application = Application::class)
class AppLanguageTest {
    private val context: Context = ApplicationProvider.getApplicationContext()

    @Test
    fun choosesAndRestoresLanguageOnOlderAndroid() {
        assertEquals(AppLanguage.SYSTEM, AppLanguages.selected(context))
        assertTrue(AppLanguages.select(context, AppLanguage.SPANISH))
        val recreated = AppLanguages.activityContext(context)
        assertEquals(AppLanguage.SPANISH, AppLanguages.selected(recreated))
        assertEquals("Mis escenarios", recreated.getString(R.string.scenarios_title))
        assertEquals("Escenario guardado", recreated.getString(R.string.feedback_saved))
        assertFalse(AppLanguages.select(context, AppLanguage.SPANISH))

        assertTrue(AppLanguages.select(context, AppLanguage.ENGLISH))
        assertEquals("My scenarios", AppLanguages.activityContext(context).getString(R.string.scenarios_title))

        assertTrue(AppLanguages.select(context, AppLanguage.PORTUGUESE))
        assertEquals("Meus cenários", AppLanguages.activityContext(context).getString(R.string.scenarios_title))

        assertTrue(AppLanguages.select(context, AppLanguage.SYSTEM))
        assertEquals(AppLanguage.SYSTEM, AppLanguages.selected(context))
    }

    @Test
    fun recognizesRegionalVariantsAndUnsupportedTags() {
        assertEquals(AppLanguage.SPANISH, AppLanguage.fromTag("es-MX"))
        assertEquals(AppLanguage.ENGLISH, AppLanguage.fromTag("en-GB"))
        assertEquals(AppLanguage.PORTUGUESE, AppLanguage.fromTag("pt-PT"))
        assertEquals(AppLanguage.SYSTEM, AppLanguage.fromTag("de"))
    }
}
