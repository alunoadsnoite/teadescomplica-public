@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.journey

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import br.com.teadescomplica.core.designsystem.TeaTopAppBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.PersonalExperience
import br.com.teadescomplica.domain.model.PersonalStrategy
import br.com.teadescomplica.domain.repository.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class StrategyDetailViewModel(
    private val repository: PersonalStrategyRepository,
    private val experiences: PersonalExperienceRepository,
) : ViewModel() {
    val strategy = MutableStateFlow<PersonalStrategy?>(null)
    val related = MutableStateFlow<List<PersonalExperience>>(emptyList())
    val error = MutableStateFlow<Int?>(null)
    val saving = MutableStateFlow(false)
    private var loading: Job? = null

    fun load(id: Long) {
        loading?.cancel()
        loading =
            viewModelScope.launch {
                combine(
                        repository.getStrategyById(id),
                        repository.getExperienceIdsForStrategy(id),
                        experiences.getAllExperiences(),
                    ) { s, ids, all ->
                        s to all.filter { it.id in ids }
                    }
                    .collect { (s, all) ->
                        strategy.value = s
                        related.value = all
                    }
            }
    }

    fun save(id: Long, title: String, description: String, onSaved: () -> Unit) {
        if (saving.value || title.isBlank()) return
        saving.value = true
        viewModelScope.launch {
            try {
                val now = System.currentTimeMillis()
                val s =
                    PersonalStrategy(
                        id = id,
                        title = title.trim(),
                        description = description,
                        createdAt = strategy.value?.createdAt ?: now,
                        updatedAt = now,
                    )
                if (id == 0L) repository.insertStrategy(s) else repository.updateStrategy(s)
                onSaved()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                error.value = R.string.strategy_save_failed
            } finally {
                saving.value = false
            }
        }
    }

    fun delete(id: Long, onDeleted: () -> Unit) {
        viewModelScope.launch {
            try {
                repository.deleteStrategy(id)
                onDeleted()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                error.value = R.string.strategy_delete_failed
            }
        }
    }
}

@Composable
fun StrategyDetailScreen(
    strategyId: Long,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit = {},
    modifier: Modifier = Modifier,
) {
    val vm: StrategyDetailViewModel =
        viewModel(key = "strategy-$strategyId") {
            StrategyDetailViewModel(
                DiModule.getPersonalStrategyRepository(),
                DiModule.getPersonalExperienceRepository(),
            )
        }
    val strategy by vm.strategy.collectAsState()
    val related by vm.related.collectAsState()
    val error by vm.error.collectAsState()
    val saving by vm.saving.collectAsState()
    LaunchedEffect(strategyId) { if (strategyId != 0L) vm.load(strategyId) }
    var editing by remember(strategyId) { mutableStateOf(strategyId == 0L) }
    var title by remember(strategy?.title) { mutableStateOf(strategy?.title ?: "") }
    var description by
        remember(strategy?.description) { mutableStateOf(strategy?.description ?: "") }
    var confirm by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = { Text(if (strategyId == 0L) stringResource(R.string.ui_new_strategy) else stringResource(R.string.ui_strategy)) },
            navigationIcon = {
                TextButton(enabled = !saving, onClick = onBackClick) { Text(stringResource(R.string.content_description_back)) }
            },
        )
        Column(
            Modifier.verticalScroll(rememberScrollState()).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (editing) {
                OutlinedTextField(
                    title,
                    { title = it },
                    label = { Text(stringResource(R.string.field_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                OutlinedTextField(
                    description,
                    { description = it },
                    label = { Text(stringResource(R.string.field_description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
                Button(
                    enabled = title.isNotBlank() && !saving,
                    onClick = {
                        vm.save(strategyId, title, description) {
                            if (strategyId == 0L) onBackClick() else editing = false
                        }
                    },
                ) {
                    Text(if (saving) stringResource(R.string.ui_saving) else stringResource(R.string.ui_save_strategy))
                }
            } else {
                Text(
                    strategy?.title ?: stringResource(R.string.ui_loading),
                    style = MaterialTheme.typography.headlineSmall,
                )
                Text(strategy?.description ?: "")
                TextButton(onClick = { editing = true }) { Text(stringResource(R.string.content_description_edit_strategy)) }
                TextButton(onClick = { confirm = true }) { Text(stringResource(R.string.content_description_delete_strategy)) }
                Text(stringResource(R.string.ui_related_experiences), style = MaterialTheme.typography.titleMedium)
                if (related.isEmpty()) Text(stringResource(R.string.ui_no_related_experiences))
                related.forEach { Text(it.title) }
            }
            error?.let { Text(stringResource(it), color = MaterialTheme.colorScheme.error) }
        }
    }
    if (confirm)
        AlertDialog(
            onDismissRequest = { confirm = false },
            title = { Text(stringResource(R.string.ui_delete_strategy)) },
            text = { Text(stringResource(R.string.ui_your_experiences_will_be_kept)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        vm.delete(strategyId, onBackClick)
                        confirm = false
                    }
                ) {
                    Text(stringResource(R.string.button_delete))
                }
            },
            dismissButton = { TextButton(onClick = { confirm = false }) { Text(stringResource(R.string.button_cancel)) } },
        )
}
