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
 * ViewModel da tela de registro de experiência.
 *
 * Orquestra o formulário de criação/edição de experiência, incluindo seleção opcional de cenário e
 * estratégias associadas.
 */
class ExperienceRecordViewModel(
    private val experienceRepository: PersonalExperienceRepository,
    private val strategyRepository: PersonalStrategyRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExperienceRecordUiState())
    val uiState: StateFlow<ExperienceRecordUiState> = _uiState

    private var editingId: Long? = null
    private var loading: Job? = null

    init {
        loadStrategies()
    }

    private fun loadStrategies() {
        viewModelScope.launch {
            strategyRepository.getAllStrategies().collect { strategies ->
                _uiState.value = _uiState.value.copy(availableStrategies = strategies)
            }
        }
    }

    fun loadExperience(id: Long) {
        editingId = id
        loading?.cancel()
        loading =
            viewModelScope.launch {
                combine(
                        experienceRepository.getExperienceById(id),
                        experienceRepository.getStrategiesForExperience(id),
                    ) { experience, ids ->
                        _uiState.value.copy(
                            draft = experience ?: PersonalExperience.Empty,
                            selectedStrategyIds = ids.toSet(),
                            error = if (experience == null) R.string.experience_missing else null,
                        )
                    }
                    .collect { _uiState.value = it }
            }
    }

    fun startNewExperience() {
        loading?.cancel()
        editingId = null
        _uiState.value =
            _uiState.value.copy(
                draft = PersonalExperience.Empty,
                selectedStrategyIds = emptySet(),
                error = null,
                feedbackMessage = null,
            )
    }

    fun updateDraftField(field: ExperienceField, value: String) {
        _uiState.value =
            _uiState.value.copy(
                draft =
                    when (field) {
                        ExperienceField.Title -> _uiState.value.draft.copy(title = value)
                        ExperienceField.Description ->
                            _uiState.value.draft.copy(description = value)
                        ExperienceField.WhatHelped -> _uiState.value.draft.copy(whatHelped = value)
                        ExperienceField.WhatWasDifficult ->
                            _uiState.value.draft.copy(whatWasDifficult = value)
                        ExperienceField.NextTimeNotes ->
                            _uiState.value.draft.copy(nextTimeNotes = value)
                    },
                error = null,
            )
    }

    fun updateOccurredAt(value: Long?) {
        _uiState.value = _uiState.value.copy(draft = _uiState.value.draft.copy(occurredAt = value))
    }

    fun updateScenarioId(scenarioId: Long?) {
        _uiState.value =
            _uiState.value.copy(
                draft = _uiState.value.draft.copy(scenarioId = scenarioId),
                error = null,
            )
    }

    fun toggleStrategy(strategyId: Long) {
        _uiState.value =
            _uiState.value.copy(
                selectedStrategyIds =
                    if (_uiState.value.selectedStrategyIds.contains(strategyId)) {
                        _uiState.value.selectedStrategyIds - strategyId
                    } else {
                        _uiState.value.selectedStrategyIds + strategyId
                    }
            )
    }

    fun isFormValid(): Boolean {
        return _uiState.value.draft.title.isNotBlank()
    }

    fun saveExperience(onSaved: () -> Unit = {}) {
        if (_uiState.value.isSaving) return
        if (!isFormValid()) {
            _uiState.value = _uiState.value.copy(error = R.string.field_title_required)
            return
        }
        _uiState.value = _uiState.value.copy(isSaving = true, error = null)
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val draft =
                    _uiState.value.draft.copy(
                        id = editingId ?: 0L,
                        updatedAt = now,
                        createdAt = if (editingId == null) now else _uiState.value.draft.createdAt,
                    )
                val id =
                    experienceRepository.saveExperienceWithStrategies(
                        draft,
                        _uiState.value.selectedStrategyIds,
                    )
                editingId = id
                _uiState.value =
                    _uiState.value.copy(
                        draft = draft.copy(id = id),
                        isSaving = false,
                        feedbackMessage = R.string.experience_saved,
                    )
                onSaved()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        isSaving = false,
                        error = R.string.experience_save_failed,
                    )
            }
        }
    }

    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}

enum class ExperienceField {
    Title,
    Description,
    WhatHelped,
    WhatWasDifficult,
    NextTimeNotes,
}

/** Estado da interface da tela de registro de experiência. */
data class ExperienceRecordUiState(
    val isSaving: Boolean = false,
    val draft: PersonalExperience = PersonalExperience.Empty,
    val availableStrategies: List<PersonalStrategy> = emptyList(),
    val selectedStrategyIds: Set<Long> = emptySet(),
    @androidx.annotation.StringRes val error: Int? = null,
    @androidx.annotation.StringRes val feedbackMessage: Int? = null,
)
