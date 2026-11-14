package br.com.teadescomplica.feature.journey

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.model.PersonalExperience
import br.com.teadescomplica.domain.repository.PersonalExperienceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

/**
 * ViewModel da tela Minha Jornada.
 *
 * Orquestra a listagem de experiências, pesquisa por palavras-chave e navegação para registro de
 * novas experiências e biblioteca de estratégias.
 */
class PersonalJourneyViewModel(private val experienceRepository: PersonalExperienceRepository) :
    ViewModel() {

    private val _uiState = MutableStateFlow(PersonalJourneyUiState())
    val uiState: StateFlow<PersonalJourneyUiState> = _uiState

    private val query = MutableStateFlow("")

    init {
        viewModelScope.launch {
            combine(experienceRepository.getAllExperiences(), query) { all, q ->
                    _uiState.value.copy(
                        experiences =
                            all.filter {
                                q.isBlank() ||
                                    it.title.contains(q, true) ||
                                    it.description.contains(q, true) ||
                                    it.whatHelped.contains(q, true) ||
                                    it.whatWasDifficult.contains(q, true) ||
                                    it.nextTimeNotes.contains(q, true)
                            },
                        searchQuery = q,
                    )
                }
                .collect { _uiState.value = it }
        }
    }

    fun loadExperiences() {}

    fun searchExperiences(value: String) {
        query.value = value
    }

    fun dismissFeedback() {
        _uiState.value = _uiState.value.copy(feedbackMessage = null)
    }

    fun setSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }
}

/** Estado da interface da tela Minha Jornada. */
data class PersonalJourneyUiState(
    val experiences: List<PersonalExperience> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val feedbackMessage: String? = null,
)
