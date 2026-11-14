package br.com.teadescomplica.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map

/**
 * Repositório de preferências baseado em Preferences DataStore.
 *
 * Persiste localmente:
 * - preferência de tema (sistema/claro/escuro),
 * - preferência de animações reduzidas,
 * - estado de conclusão da apresentação inicial.
 *
 * Não armazena cenários (esses ficam no Room).
 */
private val Context.teaPreferences by preferencesDataStore(name = "tea_descomplica_preferences")

class PreferenceRepository(private val context: Context) {

    private val dataStore = context.applicationContext.teaPreferences

    private val themeKey = stringPreferencesKey("theme_preference")
    private val reducedMotionKey = booleanPreferencesKey("reduced_motion")
    private val onboardingCompletedKey = booleanPreferencesKey("onboarding_completed")

    /** Observa a preferência de tema. Padrão: sistema. */
    fun getThemePreference(): Flow<ThemePreference> =
        dataStore.data.map { prefs ->
            val key = prefs[themeKey] ?: ThemePreference.SYSTEM.key
            ThemePreference.fromStringKey(key)
        }

    /** Retorna o tema atual (primeira emissão). */
    suspend fun getThemePreferenceOnce(): ThemePreference =
        dataStore.data.firstOrNull()?.let { prefs ->
            ThemePreference.fromStringKey(prefs[themeKey] ?: ThemePreference.SYSTEM.key)
        } ?: ThemePreference.SYSTEM

    /** Salva a preferência de tema. */
    suspend fun setThemePreference(theme: ThemePreference) {
        dataStore.edit { prefs -> prefs[themeKey] = theme.key }
    }

    /** Observa a preferência de animações reduzidas. Padrão: falso. */
    fun getReducedMotion(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[reducedMotionKey] ?: false }

    /** Altera a preferência de animações reduzidas. */
    suspend fun setReducedMotion(reduced: Boolean) {
        dataStore.edit { prefs -> prefs[reducedMotionKey] = reduced }
    }

    /** Observa se a apresentação inicial foi concluída. Padrão: falso. */
    fun getOnboardingCompleted(): Flow<Boolean> =
        dataStore.data.map { prefs -> prefs[onboardingCompletedKey] ?: false }

    /** Marca a apresentação inicial como concluída. */
    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { prefs -> prefs[onboardingCompletedKey] = completed }
    }
}
