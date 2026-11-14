@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.journey

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import br.com.teadescomplica.core.designsystem.TeaTopAppBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.feature.scenarios.formatDate
import br.com.teadescomplica.feature.scenarios.parseDate

@Composable
fun ExperienceRecordScreen(
    experienceId: Long?,
    onSaveComplete: () -> Unit,
    onCancelClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: ExperienceRecordViewModel =
        viewModel(key = "experience-form-${experienceId ?: 0}") {
            ExperienceRecordViewModel(
                DiModule.getPersonalExperienceRepository(),
                DiModule.getPersonalStrategyRepository(),
            )
        }
    val state by vm.uiState.collectAsState()
    val scenarios by
        DiModule.getScenarioRepository().getAllScenariosList().collectAsState(initial = emptyList())
    LaunchedEffect(experienceId) {
        if (experienceId == null) vm.startNewExperience() else vm.loadExperience(experienceId)
    }
    var date by
        remember(state.draft.occurredAt) { mutableStateOf(formatDate(state.draft.occurredAt)) }
    var invalidDate by remember { mutableStateOf(false) }
    var menu by remember { mutableStateOf(false) }
    Column(modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = {
                Text(if (experienceId == null) stringResource(R.string.experience_record_title) else stringResource(R.string.experience_edit_title))
            },
            navigationIcon = {
                TextButton(enabled = !state.isSaving, onClick = onCancelClick) { Text(stringResource(R.string.button_cancel)) }
            },
        )
        LazyColumn(
            Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                OutlinedTextField(
                    state.draft.title,
                    { vm.updateDraftField(ExperienceField.Title, it) },
                    label = { Text(stringResource(R.string.field_title_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    date,
                    {
                        date = it
                        invalidDate = false
                    },
                    label = { Text(stringResource(R.string.ui_date_and_time_optional)) },
                    placeholder = { Text(stringResource(R.string.ui_dd_mm_yyyy_hh_mm)) },
                    isError = invalidDate,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (invalidDate)
                    Text(stringResource(R.string.ui_invalid_date_dd_mm_yyyy_hh_mm), color = MaterialTheme.colorScheme.error)
            }
            item {
                OutlinedTextField(
                    state.draft.description,
                    { vm.updateDraftField(ExperienceField.Description, it) },
                    label = { Text(stringResource(R.string.field_experience_description_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    state.draft.whatHelped,
                    { vm.updateDraftField(ExperienceField.WhatHelped, it) },
                    label = { Text(stringResource(R.string.field_experience_what_helped_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    state.draft.whatWasDifficult,
                    { vm.updateDraftField(ExperienceField.WhatWasDifficult, it) },
                    label = { Text(stringResource(R.string.field_experience_what_was_difficult_label)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                OutlinedTextField(
                    state.draft.nextTimeNotes,
                    { vm.updateDraftField(ExperienceField.NextTimeNotes, it) },
                    label = { Text(stringResource(R.string.ui_what_should_i_remember_next_time)) },
                    modifier = Modifier.fillMaxWidth(),
                )
            }
            item {
                Text(stringResource(R.string.ui_related_scenario_optional))
                Box {
                    OutlinedButton(onClick = { menu = true }) {
                        Text(
                            scenarios.find { it.id == state.draft.scenarioId }?.title
                                ?: stringResource(R.string.ui_no_related_scenario)
                        )
                    }
                    DropdownMenu(menu, { menu = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.ui_no_related_scenario)) },
                            onClick = {
                                vm.updateScenarioId(null)
                                menu = false
                            },
                        )
                        scenarios.forEach { scenario ->
                            DropdownMenuItem(
                                text = { Text(scenario.title) },
                                onClick = {
                                    vm.updateScenarioId(scenario.id)
                                    menu = false
                                },
                            )
                        }
                    }
                }
                Text(stringResource(R.string.experience_strategies_label), style = MaterialTheme.typography.titleMedium)
                if (state.availableStrategies.isEmpty())
                    Text(stringResource(R.string.ui_you_can_add_strategies_to_your_library))
            }
            state.availableStrategies.forEach { strategy ->
                item(key = strategy.id) {
                    Row(
                        Modifier.fillMaxWidth()
                            .toggleable(
                                value = strategy.id in state.selectedStrategyIds,
                                role = Role.Checkbox,
                                onValueChange = { vm.toggleStrategy(strategy.id) },
                            )
                    ) {
                        Checkbox(strategy.id in state.selectedStrategyIds, null)
                        Text(strategy.title, Modifier.padding(top = 12.dp))
                    }
                }
            }
            state.error?.let { item { Text(stringResource(it), color = MaterialTheme.colorScheme.error) } }
            item {
                Button(
                    enabled = state.draft.title.isNotBlank() && !state.isSaving,
                    onClick = {
                        try {
                            vm.updateOccurredAt(parseDate(date))
                            vm.saveExperience(onSaveComplete)
                        } catch (_: Exception) {
                            invalidDate = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(if (state.isSaving) stringResource(R.string.ui_saving) else stringResource(R.string.ui_save_experience))
                }
            }
        }
    }
}
