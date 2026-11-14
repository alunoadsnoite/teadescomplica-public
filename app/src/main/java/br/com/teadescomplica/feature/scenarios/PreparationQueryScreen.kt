@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.scenarios

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import br.com.teadescomplica.core.designsystem.TeaTopAppBar

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.di.DiModule

@Composable
fun PreparationQueryScreen(
    scenarioId: Long,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: PreparationQueryViewModel =
        viewModel(key = "query-$scenarioId") {
            PreparationQueryViewModel(
                DiModule.getScenarioRepository(),
                DiModule.getPossibleSituationRepository(),
                DiModule.getAlternativePlanRepository(),
                DiModule.getScenarioPreparationRepository(),
                DiModule.getPersonalPhraseRepository(),
            )
        }
    val state by vm.uiState.collectAsState()
    LaunchedEffect(scenarioId) { vm.loadScenario(scenarioId) }
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    Column(modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = { Text(state.scenario?.title ?: stringResource(R.string.preparation_title)) },
            navigationIcon = { TextButton(onClick = onBackClick) { Text(stringResource(R.string.content_description_back)) } },
            actions = { TextButton(onClick = onEditClick) { Text(stringResource(R.string.button_edit)) } },
        )
        LazyColumn(
            Modifier.weight(1f).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            if (state.isLoading) item { CircularProgressIndicator() }
            state.error?.let { item { Text(stringResource(it), color = MaterialTheme.colorScheme.error) } }
            state.scenario?.let { s ->
                item { Text(s.objective, style = MaterialTheme.typography.titleMedium) }
            }
            state.preparation?.let { p ->
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(
                            Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(stringResource(R.string.preparation_title), style = MaterialTheme.typography.titleLarge)
                            listOf(
                                    stringResource(R.string.ui_location) to p.location,
                                    stringResource(R.string.ui_date) to formatDate(p.scheduledAt),
                                    stringResource(R.string.ui_what_to_expect) to p.whatToExpect,
                                    stringResource(R.string.ui_what_to_bring) to p.whatToBring,
                                    stringResource(R.string.preparation_important_info_label) to p.importantInformation,
                                    stringResource(R.string.ui_notes) to p.personalNotes,
                                )
                                .filter { it.second.isNotBlank() }
                                .forEach { (label, value) -> Text("$label: $value") }
                        }
                    }
                }
            }
            item {
                Text(stringResource(R.string.possible_situations_title), style = MaterialTheme.typography.titleLarge)
                if (state.situations.isEmpty()) Text(stringResource(R.string.ui_no_situations_saved))
            }
            state.situations.forEach { s -> item { Text(s.description) } }
            item {
                Text(stringResource(R.string.alternative_plans_title), style = MaterialTheme.typography.titleLarge)
                if (state.plans.isEmpty()) Text(stringResource(R.string.ui_no_alternatives_saved))
            }
            state.plans.forEach { p ->
                item {
                    Text(p.title, style = MaterialTheme.typography.titleMedium)
                    Text(p.description)
                }
            }
            item {
                Text(stringResource(R.string.personal_phrases_title), style = MaterialTheme.typography.titleLarge)
                if (state.phrases.isEmpty()) Text(stringResource(R.string.ui_no_phrases_saved))
            }
            state.phrases.forEach { p ->
                item {
                    Card(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(p.text)
                            TextButton(
                                onClick = {
                                    clipboard.setText(
                                        androidx.compose.ui.text.AnnotatedString(p.text)
                                    )
                                }
                            ) {
                                Text(stringResource(R.string.content_description_copy_phrase))
                            }
                        }
                    }
                }
            }
        }
    }
}
