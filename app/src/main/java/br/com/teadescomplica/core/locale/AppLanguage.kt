package br.com.teadescomplica.core.locale

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import br.com.teadescomplica.R

internal enum class AppLanguage(val tag: String, val label: Int) {
    SYSTEM("", R.string.language_system),
    PORTUGUESE("pt-BR", R.string.language_portuguese),
    ENGLISH("en-US", R.string.language_english),
    SPANISH("es", R.string.language_spanish);

    companion object {
        fun fromTag(tag: String?): AppLanguage =
            entries.firstOrNull { it.tag == tag }
                ?: when (tag?.substringBefore('-')) {
                    "pt" -> PORTUGUESE
                    "en" -> ENGLISH
                    "es" -> SPANISH
                    else -> SYSTEM
                }
    }
}

internal object AppLanguages {
    private fun preferences(context: Context) =
        context.getSharedPreferences("app_language", Context.MODE_PRIVATE)

    fun selected(context: Context): AppLanguage {
        val tag = if (Build.VERSION.SDK_INT >= 33) {
            context.getSystemService(LocaleManager::class.java).applicationLocales[0]?.toLanguageTag()
        } else {
            preferences(context).getString("language", null)
        }
        return AppLanguage.fromTag(tag)
    }

    /** Returns true when the activity must be recreated explicitly. */
    fun select(context: Context, language: AppLanguage): Boolean {
        if (language == selected(context)) return false
        if (Build.VERSION.SDK_INT >= 33) {
            context.getSystemService(LocaleManager::class.java).applicationLocales =
                LocaleList.forLanguageTags(language.tag)
            return false
        }
        preferences(context).edit().putString("language", language.tag).apply()
        return true
    }

    fun activityContext(context: Context): Context {
        if (Build.VERSION.SDK_INT >= 33) return context
        val language = selected(context)
        if (language == AppLanguage.SYSTEM) return context
        val configuration = Configuration(context.resources.configuration)
        configuration.setLocales(LocaleList.forLanguageTags(language.tag))
        return context.createConfigurationContext(configuration)
    }
}
