@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.journey

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import br.com.teadescomplica.core.designsystem.TeaTopAppBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.PersonalExperience
import br.com.teadescomplica.domain.model.PersonalStrategy
import java.text.DateFormat
import androidx.compose.ui.platform.LocalConfiguration

/**
 * Tela de detalhes da experiência.
 *
 * Apresenta título, data, descrição, o que ajudou, dificuldades, observações, estratégias
 * associadas e cenário relacionado. Permite editar e excluir.
 *
 * @param experienceId identificador da experiência.
 * @param onBackClick navega de volta para Minha Jornada.
 * @param onEditClick navega para o formulário de edição.
 */
@Composable
fun ExperienceDetailScreen(
    experienceId: Long,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: ExperienceDetailViewModel = viewModel {
        ExperienceDetailViewModel(
            experienceRepository = DiModule.getPersonalExperienceRepository(),
            strategyRepository = DiModule.getPersonalStrategyRepository(),
        )
    }
    val uiState = viewModel.uiState.collectAsState().value
    LaunchedEffect(experienceId) { viewModel.loadExperience(experienceId) }
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = {
                Text(
                    text =
                        uiState.experience?.title
                            ?: stringResource(id = R.string.experience_detail_title),
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = stringResource(id = R.string.content_description_back),
                    )
                }
            },
            actions = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    IconButton(onClick = onEditClick) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription =
                                stringResource(id = R.string.content_description_edit_experience),
                            tint = MaterialTheme.colorScheme.primary,
                        )
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription =
                                stringResource(id = R.string.content_description_delete_experience),
                            tint = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            },
            colors =
                TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                ),
        )

        uiState.error?.let { error ->
            ErrorBanner(message = stringResource(error), onDismiss = { viewModel.dismissFeedback() })
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { ExperienceDetailCard(experience = uiState.experience) }

            item {
                uiState.experience?.let { experience ->
                    if (experience.scenarioId != null) {
                        RelatedScenarioCard(scenarioId = experience.scenarioId!!)
                    }
                }
            }

            item { StrategiesAssociatedCard(strategies = uiState.strategies) }

            uiState.feedbackMessage?.let { message ->
                item {
                    Snackbar(
                        action = {
                            TextButton(onClick = { viewModel.dismissFeedback() }) { Text(stringResource(R.string.ui_close)) }
                        }
                    ) {
                        Text(stringResource(message))
                    }
                }
            }
        }

        if (showDeleteDialog) {
            ConfirmationDialog(
                onDismiss = { showDeleteDialog = false },
                onConfirm = {
                    showDeleteDialog = false
                    viewModel.deleteExperience(onBackClick)
                },
            )
        }
    }
}

/** Cartão com detalhes da experiência. */
@Composable
private fun ExperienceDetailCard(experience: PersonalExperience?) {
    if (experience == null) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = experience.title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.height(8.dp))

            experience.occurredAt?.let { occurredAt ->
                val formatted =
                    DateFormat.getDateTimeInstance(
                            DateFormat.SHORT,
                            DateFormat.SHORT,
                            LocalConfiguration.current.locales[0],
                        )
                        .format(occurredAt)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(
                        imageVector = Icons.Default.Schedule,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp),
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = formatted,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (experience.description.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.experience_detail_description_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = experience.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (experience.whatHelped.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.experience_detail_what_helped_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = experience.whatHelped,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (experience.whatWasDifficult.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.experience_detail_what_was_difficult_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = experience.whatWasDifficult,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            if (experience.nextTimeNotes.isNotEmpty()) {
                Text(
                    text = stringResource(id = R.string.experience_detail_next_time_notes_label),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = experience.nextTimeNotes,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

/** Cartão de cenário relacionado. */
@Composable
private fun RelatedScenarioCard(scenarioId: Long) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.experience_detail_scenario_related_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(id = R.string.experience_detail_scenario_related_value),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
            )
        }
    }
}

/** Cartão de estratégias associadas. */
@Composable
private fun StrategiesAssociatedCard(strategies: List<PersonalStrategy>) {
    if (strategies.isEmpty()) return

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(id = R.string.experience_detail_strategies_label),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(modifier = Modifier.height(8.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                strategies.forEach { strategy ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            imageVector = Icons.Default.Lightbulb,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp),
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = strategy.title,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                }
            }
        }
    }
}

/** Banner de erro. */
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

/** Dialog de confirmação de exclusão. */
@Composable
private fun ConfirmationDialog(onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(id = R.string.dialog_delete_experience_title)) },
        text = {
            Text(
                text = stringResource(id = R.string.dialog_delete_experience_message),
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
