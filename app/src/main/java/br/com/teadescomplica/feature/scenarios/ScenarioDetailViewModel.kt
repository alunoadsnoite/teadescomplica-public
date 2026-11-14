package br.com.teadescomplica.feature.scenarios

import br.com.teadescomplica.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.model.AlternativePlan
import br.com.teadescomplica.domain.model.PersonalPhrase
import br.com.teadescomplica.domain.model.PossibleSituation
import br.com.teadescomplica.domain.model.Scenario
import br.com.teadescomplica.domain.model.ScenarioPreparation
import br.com.teadescomplica.domain.model.SituationLikelihood
import br.com.teadescomplica.domain.repository.AlternativePlanRepository
import br.com.teadescomplica.domain.repository.PersonalPhraseRepository
import br.com.teadescomplica.domain.repository.PossibleSituationRepository
import br.com.teadescomplica.domain.repository.ScenarioPreparationRepository
import br.com.teadescomplica.domain.repository.ScenarioRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel da tela de detalhes do cenário.
 *
 * Orquestra preparação, situações possíveis, planos alternativos e frases pessoais de um único
 * cenário. Nunca acessa DAO diretamente.
 */
class ScenarioDetailViewModel(
    private val scenarioRepository: ScenarioRepository,
    private val preparationRepository: ScenarioPreparationRepository,
    private val situationRepository: PossibleSituationRepository,
    private val planRepository: AlternativePlanRepository,
    private val phraseRepository: PersonalPhraseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ScenarioDetailUiState())
    val uiState: StateFlow<ScenarioDetailUiState> = _uiState

    private var currentScenarioId: Long = 0
    private var loading: Job? = null

    fun loadScenario(scenarioId: Long) {
        currentScenarioId = scenarioId
        _uiState.value =
            _uiState.value.copy(
                scenario = null,
                preparation = null,
                situations = emptyList(),
                plans = emptyList(),
                phrases = emptyList(),
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
                            scenarioRepository.getScenarioById(currentScenarioId),
                            preparationRepository.getPreparationByScenarioId(currentScenarioId),
                            situationRepository.getPossibleSituationsByScenarioId(
                                currentScenarioId
                            ),
                            planRepository.getAlternativePlansByScenarioId(currentScenarioId),
                            phraseRepository.getPersonalPhrasesByScenarioId(currentScenarioId),
                        ) { scenario, preparation, situations, plans, phrases ->
                            _uiState.value.copy(
                                scenario = scenario,
                                preparation = preparation,
                                situations = situations,
                                plans = plans,
                                phrases = phrases,
                                isLoading = false,
                                error = if (scenario == null) R.string.scenario_missing else null,
                            )
                        }
                        .collect { _uiState.value = it }
                } catch (e: CancellationException) {
                    throw e
                } catch (_: Exception) {
                    _uiState.value =
                        _uiState.value.copy(
                            isLoading = false,
                            error = R.string.scenario_load_failed,
                        )
                }
            }
    }

    fun updateSituation(situation: PossibleSituation) {
        viewModelScope.launch {
            try {
                situationRepository.updatePossibleSituation(situation)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _uiState.value =
                    _uiState.value.copy(error = R.string.situation_update_failed)
            }
        }
    }

    /** Salva a preparação do cenário. */
    fun savePreparation(preparation: ScenarioPreparation) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val toSave = preparation.copy(scenarioId = currentScenarioId, updatedAt = now)
                preparationRepository.savePreparation(toSave)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.preparation_saved,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.preparation_save_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Adiciona uma situação possível. */
    fun addSituation(description: String, likelihood: SituationLikelihood?) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val situation =
                    PossibleSituation(
                        scenarioId = currentScenarioId,
                        description = description,
                        likelihood = likelihood,
                        createdAt = now,
                    )
                situationRepository.insertPossibleSituation(situation)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.situation_added,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.situation_add_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /**
     * Exclui uma situação possível. Planos associados têm possibleSituationId definido como nulo.
     */
    fun deleteSituation(id: Long) {
        viewModelScope.launch {
            try {
                situationRepository.deletePossibleSituation(id)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.situation_deleted,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.situation_delete_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Adiciona um plano alternativo. */
    fun addPlan(title: String, description: String, possibleSituationId: Long?) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val currentPlans = _uiState.value.plans
                val nextPosition =
                    if (currentPlans.isEmpty()) 0 else currentPlans.maxOf { it.position } + 1
                val plan =
                    AlternativePlan(
                        scenarioId = currentScenarioId,
                        possibleSituationId = possibleSituationId,
                        title = title,
                        description = description,
                        position = nextPosition,
                        createdAt = now,
                        updatedAt = now,
                    )
                planRepository.insertAlternativePlan(plan)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.plan_added,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.plan_add_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Atualiza um plano alternativo. */
    fun updatePlan(plan: AlternativePlan) {
        viewModelScope.launch {
            try {
                planRepository.updateAlternativePlan(
                    plan.copy(updatedAt = System.currentTimeMillis())
                )
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.plan_updated,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.plan_update_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Exclui um plano alternativo. */
    fun deletePlan(id: Long) {
        viewModelScope.launch {
            try {
                planRepository.deleteAlternativePlan(id)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.plan_deleted,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.plan_delete_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Adiciona uma frase pessoal. */
    fun addPhrase(text: String) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val currentPhrases = _uiState.value.phrases
                val nextPosition =
                    if (currentPhrases.isEmpty()) 0 else currentPhrases.maxOf { it.position } + 1
                val phrase =
                    PersonalPhrase(
                        scenarioId = currentScenarioId,
                        text = text,
                        position = nextPosition,
                        createdAt = now,
                    )
                phraseRepository.insertPersonalPhrase(phrase)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.phrase_added,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.phrase_add_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Atualiza uma frase pessoal. */
    fun updatePhrase(phrase: PersonalPhrase) {
        viewModelScope.launch {
            try {
                phraseRepository.updatePersonalPhrase(phrase)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.phrase_updated,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.phrase_update_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Exclui uma frase pessoal. */
    fun deletePhrase(id: Long) {
        viewModelScope.launch {
            try {
                phraseRepository.deletePersonalPhrase(id)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.phrase_deleted,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.phrase_delete_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Reordena frases pessoais. */
    fun reorderPhrases(phrases: List<PersonalPhrase>) {
        viewModelScope.launch {
            try {
                phraseRepository.reorderPersonalPhrases(currentScenarioId, phrases)
                _uiState.value = _uiState.value.copy(phrases = phrases)
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.phrase_reorder_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Reordena planos alternativos. */
    fun reorderPlans(plans: List<AlternativePlan>) {
        viewModelScope.launch {
            try {
                planRepository.reorderAlternativePlans(currentScenarioId, plans)
                _uiState.value = _uiState.value.copy(plans = plans)
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.plan_reorder_failed,
                        feedbackType = FeedbackType.Error,
                    )
            }
        }
    }

    /** Dismissa o feedback. */
    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }
}

/** Estado da interface da tela de detalhes do cenário. */
data class ScenarioDetailUiState(
    val scenario: Scenario? = null,
    val preparation: ScenarioPreparation? = null,
    val situations: List<PossibleSituation> = emptyList(),
    val plans: List<AlternativePlan> = emptyList(),
    val phrases: List<PersonalPhrase> = emptyList(),
    val isLoading: Boolean = false,
    @androidx.annotation.StringRes val error: Int? = null,
    @androidx.annotation.StringRes val feedbackMessage: Int? = null,
    val feedbackType: FeedbackType = FeedbackType.Success,
)
