package br.com.teadescomplica.feature.scenarios

import br.com.teadescomplica.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.model.AlternativePlan
import br.com.teadescomplica.domain.model.PersonalPhrase
import br.com.teadescomplica.domain.model.PossibleSituation
import br.com.teadescomplica.domain.model.Scenario
import br.com.teadescomplica.domain.model.ScenarioPreparation
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
 * ViewModel do modo de consulta ("Minha preparação").
 *
 * Apresenta apenas as informações essenciais do cenário de forma simplificada, sem modo de edição.
 */
class PreparationQueryViewModel(
    private val scenarioRepository: ScenarioRepository,
    private val situationRepository: PossibleSituationRepository,
    private val planRepository: AlternativePlanRepository,
    private val preparationRepository: ScenarioPreparationRepository,
    private val phraseRepository: PersonalPhraseRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(PreparationQueryUiState())
    val uiState: StateFlow<PreparationQueryUiState> = _uiState

    private var currentScenarioId: Long = 0
    private var loading: Job? = null

    fun loadScenario(scenarioId: Long) {
        currentScenarioId = scenarioId
        _uiState.value =
            _uiState.value.copy(
                scenario = null,
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
                            PreparationQueryUiState(
                                scenario = scenario,
                                preparation = preparation,
                                situations = situations,
                                plans = plans,
                                phrases = phrases,
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
                            error = R.string.preparation_load_failed,
                        )
                }
            }
    }
}

/** Estado da interface do modo de consulta. */
data class PreparationQueryUiState(
    val preparation: ScenarioPreparation? = null,
    val scenario: Scenario? = null,
    val situations: List<PossibleSituation> = emptyList(),
    val plans: List<AlternativePlan> = emptyList(),
    val phrases: List<PersonalPhrase> = emptyList(),
    val isLoading: Boolean = false,
    @androidx.annotation.StringRes val error: Int? = null,
)
