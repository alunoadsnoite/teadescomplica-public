package br.com.teadescomplica.feature.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.core.datastore.PreferenceRepository
import br.com.teadescomplica.core.datastore.ThemePreference
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel da tela Configurações.
 *
 * Gerencia tema, animações reduzidas e leitura da versão do aplicativo.
 */
class SettingsViewModel(private val preferenceRepository: PreferenceRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState

    init {
        loadPreferences()
    }

    private fun loadPreferences() {
        viewModelScope.launch {
            val theme = preferenceRepository.getThemePreferenceOnce()
            _uiState.value = _uiState.value.copy(themePreference = theme)

            // Observa mudanças futuras do tema.
            viewModelScope.launch {
                preferenceRepository.getThemePreference().collect { theme ->
                    _uiState.value = _uiState.value.copy(themePreference = theme)
                }
            }
            viewModelScope.launch {
                preferenceRepository.getReducedMotion().collect { reduced ->
                    _uiState.value = _uiState.value.copy(reducedMotion = reduced)
                }
            }
        }
    }

    fun setTheme(theme: ThemePreference) {
        viewModelScope.launch { preferenceRepository.setThemePreference(theme) }
    }

    fun setReducedMotion(reduced: Boolean) {
        viewModelScope.launch { preferenceRepository.setReducedMotion(reduced) }
    }

    fun getVersionName(): String = "1.0.0"
}

/** Estado da interface da tela Configurações. */
data class SettingsUiState(
    val themePreference: ThemePreference = ThemePreference.SYSTEM,
    val reducedMotion: Boolean = false,
    val versionName: String = br.com.teadescomplica.BuildConfig.VERSION_NAME,
)
