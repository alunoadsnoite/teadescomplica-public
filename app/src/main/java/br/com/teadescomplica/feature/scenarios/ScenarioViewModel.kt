package br.com.teadescomplica.feature.scenarios

import br.com.teadescomplica.R
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.model.Scenario
import br.com.teadescomplica.domain.repository.ScenarioRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel da tela Meus cenários.
 *
 * Expõe um único estado de interface imutável ({@link ScenarioUiState}) e orquestra operações de
 * persistência via repositório. Nunca acessa DAO diretamente.
 *
 * Como o DataSource expõe suspend functions (não Flow reativo), o ViewModel recarrega a lista
 * explicitamente após cada operação de escrita. Isso garante que a interface reflita inserções,
 * atualizações e exclusões.
 */
class ScenarioViewModel(private val repository: ScenarioRepository) : ViewModel() {

    private val _uiState = MutableStateFlow(ScenarioUiState())
    val uiState: StateFlow<ScenarioUiState> = _uiState

    private var loading: Job? = null

    init {
        loadScenarios()
    }

    /** Carrega a lista de cenários do repositório. */
    fun loadScenarios() {
        loading?.cancel()
        loading =
            viewModelScope.launch {
                repository.getAllScenariosList().collect { scenarios ->
                    _uiState.value = _uiState.value.copy(scenarios = scenarios, error = null)
                }
            }
    }

    /** Salva (cria ou atualiza) um cenário e recarrega a lista. */
    fun saveScenario(scenario: Scenario) {
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val toSave =
                    if (scenario.id == 0L) {
                        scenario.copy(createdAt = now, updatedAt = now)
                    } else {
                        scenario.copy(updatedAt = now)
                    }
                if (toSave.id == 0L) {
                    repository.insertScenario(toSave)
                } else {
                    repository.updateScenario(toSave)
                }
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        showForm = false,
                        editingId = null,
                        formScenario = Scenario.Empty,
                        feedbackMessage =
                            if (scenario.id == 0L) R.string.feedback_saved else R.string.feedback_updated,
                        feedbackType = FeedbackType.Success,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.error_save
                    )
            }
        }
    }

    /** Exclui um cenário pelo identificador e recarrega a lista. */
    fun deleteScenario(id: Long) {
        viewModelScope.launch {
            try {
                repository.deleteScenario(id)
                _uiState.value =
                    _uiState.value.copy(
                        error = null,
                        feedbackMessage = R.string.feedback_deleted,
                        feedbackType = FeedbackType.Success,
                        deletingId = null,
                    )
            } catch (e: Exception) {
                _uiState.value =
                    _uiState.value.copy(
                        error = R.string.error_delete,
                        deletingId = null,
                    )
            }
        }
    }

    /** Inicia fluxo de criação de novo cenário. */
    fun startNewScenario() {
        _uiState.value =
            _uiState.value.copy(
                showForm = true,
                editingId = null,
                formScenario = Scenario.Empty,
                error = null,
            )
    }

    /** Inicia fluxo de edição de um cenário existente. */
    fun startEditScenario(scenario: Scenario) {
        _uiState.value =
            _uiState.value.copy(
                showForm = true,
                editingId = scenario.id,
                formScenario = scenario,
                error = null,
            )
    }

    /** Cancela o formulário. */
    fun cancelForm() {
        _uiState.value =
            _uiState.value.copy(
                showForm = false,
                editingId = null,
                formScenario = Scenario.Empty,
                error = null,
            )
    }

    /** Atualiza o rascunho do formulário. */
    fun updateFormScenario(scenario: Scenario) {
        _uiState.value = _uiState.value.copy(formScenario = scenario, error = null)
    }

    /** Solicita confirmação de exclusão. */
    fun requestDelete(id: Long) {
        _uiState.value = _uiState.value.copy(deletingId = id)
    }

    /** Cancela a confirmação de exclusão. */
    fun cancelDelete() {
        _uiState.value = _uiState.value.copy(deletingId = null)
    }

    /** Dismissa a mensagem de feedback. */
    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    /** Valida o título do rascunho atual. */
    fun isFormValid(): Boolean {
        return _uiState.value.formScenario.title.isNotBlank()
    }
}

/** Tipo de feedback exibido ao usuário. */
enum class FeedbackType {
    Success,
    Error,
}

/** Estado da interface da tela Meus cenários. */
data class ScenarioUiState(
    val scenarios: List<Scenario> = emptyList(),
    @androidx.annotation.StringRes val error: Int? = null,
    val showForm: Boolean = false,
    val editingId: Long? = null,
    val formScenario: Scenario = Scenario.Empty,
    val deletingId: Long? = null,
    @androidx.annotation.StringRes val feedbackMessage: Int? = null,
    val feedbackType: FeedbackType = FeedbackType.Success,
)
