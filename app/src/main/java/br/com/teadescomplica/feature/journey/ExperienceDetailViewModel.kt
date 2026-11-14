package br.com.teadescomplica.feature.journey

import br.com.teadescomplica.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.model.PersonalExperience
import br.com.teadescomplica.domain.model.PersonalStrategy
import br.com.teadescomplica.domain.repository.PersonalExperienceRepository
import br.com.teadescomplica.domain.repository.PersonalStrategyRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de detalhes da experiência.
 *
 * Orquestra a exibição de uma experiência, suas estratégias associadas, o cenário relacionado
 * (quando existente) e as ações de editar/excluir.
 */
class ExperienceDetailViewModel(
    private val experienceRepository: PersonalExperienceRepository,
    private val strategyRepository: PersonalStrategyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExperienceDetailUiState())
    val uiState: StateFlow<ExperienceDetailUiState> = _uiState

    private var currentExperienceId: Long = 0
    private var loading: Job? = null

    fun loadExperience(id: Long) {
        currentExperienceId = id
        _uiState.value =
            _uiState.value.copy(
                experience = null,
                strategies = emptyList(),
                error = null,
                isLoading = true,
            )
        loadData()
    }

    private fun loadData() {
        loading?.cancel()
        loading =
            viewModelScope.launch {
                try {
                    combine(
                            experienceRepository.getExperienceById(currentExperienceId),
                            experienceRepository.getStrategiesForExperience(currentExperienceId),
                            strategyRepository.getAllStrategies(),
                        ) { experience, ids, strategies ->
                            _uiState.value.copy(
                                experience = experience,
                                strategies = strategies.filter { it.id in ids },
                                isLoading = false,
                                error =
                                    if (experience == null) R.string.experience_missing else null,
                            )
                        }
                        .collect { _uiState.value = it }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = R.string.experience_load_failed,
                        )
                }
            }
    }

    fun deleteExperience(onDeleted: () -> Unit = {}) {
        viewModelScope.launch {
            try {
                experienceRepository.deleteExperience(currentExperienceId)
                _uiState.value =
                    _uiState.value.copy(error = null, feedbackMessage = R.string.experience_deleted)
                onDeleted()
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.experience_delete_failed
                    )
            }
        }
    }

    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}

/** Estado da interface da tela de detalhes da experiência. */
data class ExperienceDetailUiState(
    val experience: PersonalExperience? = null,
    val strategies: List<PersonalStrategy> = emptyList(),
    val isLoading: Boolean = false,
    @androidx.annotation.StringRes val error: Int? = null,
    @androidx.annotation.StringRes val feedbackMessage: Int? = null,
)
