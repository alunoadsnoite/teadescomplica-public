package br.com.teadescomplica.feature.journey

import br.com.teadescomplica.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.model.PersonalStrategy
import br.com.teadescomplica.domain.repository.PersonalExperienceRepository
import br.com.teadescomplica.domain.repository.PersonalStrategyRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel da tela Minhas Estratégias (biblioteca de estratégias).
 *
 * Orquestra listagem, criação, edição, exclusão e pesquisa de estratégias, bem como a consulta de
 * experiências relacionadas a cada estratégia.
 */
class StrategyLibraryViewModel(
    private val strategyRepository: PersonalStrategyRepository,
    private val experienceRepository: PersonalExperienceRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(StrategyLibraryUiState())
    val uiState: StateFlow<StrategyLibraryUiState> = _uiState

    private val query = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(strategyRepository.getAllStrategies(), query) { all, q ->
                    _uiState.value.copy(
                        strategies =
                            all.filter {
                                q.isBlank() ||
                                    it.title.contains(q, true) ||
                                    it.description.contains(q, true)
                            },
                        searchQuery = q,
                    )
                }
                .collect { _uiState.value = it }
        }
    }

    fun loadStrategies() {}

    fun searchStrategies(value: String) {
        query.value = value
    }

    fun deleteStrategy(id: Long) {
        viewModelScope.launch {
            try {
                strategyRepository.deleteStrategy(id)
                _uiState.value =
                    _uiState.value.copy(error = null, feedbackMessage = R.string.strategy_deleted)
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.strategy_delete_retry
                    )
            }
        }
    }

    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}

/** Estado da interface da tela Minhas Estratégias. */
data class StrategyLibraryUiState(
    val strategies: List<PersonalStrategy> = emptyList(),
    val searchQuery: String = "",
    @androidx.annotation.StringRes val error: Int? = null,
    @androidx.annotation.StringRes val feedbackMessage: Int? = null,
)
