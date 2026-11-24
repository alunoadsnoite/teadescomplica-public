@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.scenarios

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import br.com.teadescomplica.core.designsystem.TeaTopAppBar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.Scenario
import java.text.DateFormat
import androidx.compose.ui.platform.LocalConfiguration

@Composable
fun ScenarioListScreen(
    onHomeClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onScenarioClick: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ScenarioViewModel = viewModel {
        ScenarioViewModel(DiModule.getScenarioRepository())
    }
    val uiState = viewModel.uiState.collectAsState().value
    val snackbarHostState = remember { SnackbarHostState() }

    var showRightsGuides by rememberSaveable { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var deleteTargetId by remember { mutableStateOf(0L) }

    val feedback = uiState.feedbackMessage?.let { stringResource(it) }
    LaunchedEffect(feedback) {
        if (feedback != null) {
            snackbarHostState.showSnackbar(
                message = feedback,
                duration = SnackbarDuration.Long,
            )
            viewModel.dismissFeedback()
        }
    }

    Column(modifier = modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = { Text(text = stringResource(id = R.string.scenarios_title)) },
            colors =
                TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
        )

        Button(
            onClick = { viewModel.startNewScenario() },
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(stringResource(R.string.button_new_scenario))
        }

        TextButton(
            onClick = { showRightsGuides = true },
            modifier = Modifier.padding(horizontal = 16.dp),
        ) {
            Text(stringResource(R.string.rights_guides))
        }
        if (showRightsGuides) RightsGuides(onDismiss = { showRightsGuides = false })

        Box(modifier = Modifier.weight(1f)) {
            SnackbarHost(hostState = snackbarHostState)

            if (uiState.scenarios.isEmpty() && uiState.error == null) {
                EmptyState(
                    title = stringResource(id = R.string.scenarios_empty_title),
                    subtitle = stringResource(id = R.string.scenarios_empty_subtitle),
                )
            } else {
                ScenarioList(
                    scenarios = uiState.scenarios,
                    onEditClick = { scenario -> viewModel.startEditScenario(scenario) },
                    onDeleteClick = { id ->
                        deleteTargetId = id
                        showDeleteDialog = true
                    },
                    onItemClick = { scenario -> onScenarioClick(scenario.id) },
                )
            }

            uiState.error?.let { error ->
                ErrorBanner(message = stringResource(error), onDismiss = { viewModel.dismissFeedback() })
            }
        }

        if (uiState.showForm) {
            ScenarioForm(
                scenario = uiState.formScenario,
                isEdit = uiState.editingId != null,
                onSave = { draft ->
                    if (!viewModel.isFormValid()) return@ScenarioForm
                    viewModel.saveScenario(draft)
                },
                onCancel = { viewModel.cancelForm() },
                onTitleChange = { title ->
                    viewModel.updateFormScenario(uiState.formScenario.copy(title = title))
                },
                onDescriptionChange = { desc ->
                    viewModel.updateFormScenario(uiState.formScenario.copy(description = desc))
                },
                onObjectiveChange = { obj ->
                    viewModel.updateFormScenario(uiState.formScenario.copy(objective = obj))
                },
            )
        }

        if (showDeleteDialog && deleteTargetId != 0L) {
            ConfirmationDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.deleteScenario(deleteTargetId)
                    deleteTargetId = 0L
                },
            )
        }
    }
}

@Composable
private fun ScenarioList(
    scenarios: List<Scenario>,
    onEditClick: (Scenario) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onItemClick: (Scenario) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        items(items = scenarios, key = { it.id }) { scenario ->
            ScenarioItem(
                scenario = scenario,
                onEditClick = onEditClick,
                onDeleteClick = onDeleteClick,
                onItemClick = onItemClick,
            )
        }
    }
}

@Composable
private fun ScenarioItem(
    scenario: Scenario,
    onEditClick: (Scenario) -> Unit,
    onDeleteClick: (Long) -> Unit,
    onItemClick: (Scenario) -> Unit,
) {
    val updated =
        DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, LocalConfiguration.current.locales[0])
            .format(scenario.updatedAt)

    val updatedLabel = stringResource(R.string.scenario_updated_at, updated)
    val deleteLabel = stringResource(R.string.content_description_delete_scenario, scenario.title)
    Card(
        modifier =
            Modifier.fillMaxWidth()
                .clickable { onItemClick(scenario) }
                .semantics {
                    contentDescription =
                        "${scenario.title}. ${scenario.description}. $updatedLabel"
                },
        colors =
            CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = scenario.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1F),
                )
                Text(
                    text = updated,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = scenario.description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (scenario.objective.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = scenario.objective,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                Button(
                    onClick = { onEditClick(scenario) },
                    modifier = Modifier.padding(end = 8.dp),
                ) {
                    Text(text = stringResource(id = R.string.button_edit))
                }
                Button(
                    onClick = { onDeleteClick(scenario.id) },
                    colors =
                        ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError,
                        ),
                ) {
                    Text(
                        text = stringResource(id = R.string.button_delete),
                        modifier =
                            Modifier.semantics {
                                contentDescription = deleteLabel
                            },
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyState(title: String, subtitle: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Icon(
            imageVector = Icons.Default.Inbox,
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = subtitle,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp),
        )
    }
}

@Composable
private fun ErrorBanner(message: String, onDismiss: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, top = 8.dp)) {
        Surface(color = MaterialTheme.colorScheme.error, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.Error,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onError,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onError,
                )
                Spacer(modifier = Modifier.weight(1F))
                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onError,
                    )
                }
            }
        }
    }
}

@Composable
private fun ScenarioForm(
    scenario: Scenario,
    isEdit: Boolean,
    onSave: (Scenario) -> Unit,
    onCancel: () -> Unit,
    onTitleChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onObjectiveChange: (String) -> Unit,
) {
    var title by remember(scenario) { mutableStateOf(scenario.title) }
    var description by remember(scenario) { mutableStateOf(scenario.description) }
    var objective by remember(scenario) { mutableStateOf(scenario.objective) }

    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text =
                    if (isEdit) stringResource(id = R.string.form_edit_title)
                    else stringResource(id = R.string.form_new_title)
            )
        },
        text = {
            Column(modifier = Modifier.width(320.dp)) {
                TextField(
                    value = title,
                    onValueChange = { newTitle ->
                        title = newTitle
                        onTitleChange(newTitle)
                    },
                    label = { Text(text = stringResource(id = R.string.field_title_label)) },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                if (title.isBlank()) {
                    Text(
                        text = stringResource(id = R.string.field_title_required),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = description,
                    onValueChange = { newDesc ->
                        description = newDesc
                        onDescriptionChange(newDesc)
                    },
                    label = { Text(text = stringResource(id = R.string.field_description_label)) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.field_description_hint))
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(12.dp))
                TextField(
                    value = objective,
                    onValueChange = { newObj ->
                        objective = newObj
                        onObjectiveChange(newObj)
                    },
                    label = { Text(text = stringResource(id = R.string.field_objective_label)) },
                    placeholder = {
                        Text(text = stringResource(id = R.string.field_objective_hint))
                    },
                    maxLines = 3,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(
                        scenario.copy(
                            title = title,
                            description = description,
                            objective = objective,
                        )
                    )
                },
                enabled = title.isNotBlank(),
            ) {
                Text(
                    text = stringResource(id = R.string.button_save),
                    
                )
            }
        },
        dismissButton = {
            Button(onClick = onCancel) { Text(text = stringResource(id = R.string.button_cancel)) }
        },
    )
}

@Composable
private fun ConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.dialog_delete_title)) },
        text = {
            Text(
                text = stringResource(id = R.string.dialog_delete_message),
                style = MaterialTheme.typography.bodyMedium,
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors =
                    ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error,
                        contentColor = MaterialTheme.colorScheme.onError,
                    ),
            ) {
                Text(text = stringResource(id = R.string.dialog_confirm_delete))
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) { Text(text = stringResource(id = R.string.dialog_keep)) }
        },
    )
}
