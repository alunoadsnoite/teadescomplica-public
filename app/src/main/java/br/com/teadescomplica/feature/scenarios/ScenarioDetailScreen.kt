@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.scenarios

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.designsystem.TeaTopAppBar
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.*
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.ResolverStyle

private val dateFormat =
    DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm").withResolverStyle(ResolverStyle.STRICT)

fun formatDate(value: Long?): String =
    value?.let { Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).format(dateFormat) } ?: ""

fun parseDate(value: String): Long? =
    if (value.isBlank()) null
    else
        LocalDateTime.parse(value.trim(), dateFormat)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

@Composable
fun ScenarioDetailScreen(
    scenarioId: Long,
    onBackClick: () -> Unit,
    onQueryModeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: ScenarioDetailViewModel =
        viewModel(key = "scenario-$scenarioId") {
            ScenarioDetailViewModel(
                DiModule.getScenarioRepository(),
                DiModule.getScenarioPreparationRepository(),
                DiModule.getPossibleSituationRepository(),
                DiModule.getAlternativePlanRepository(),
                DiModule.getPersonalPhraseRepository(),
            )
        }
    val state by vm.uiState.collectAsState()
    LaunchedEffect(scenarioId) { vm.loadScenario(scenarioId) }
    var preparation by remember { mutableStateOf(false) }
    var situation by remember { mutableStateOf<PossibleSituation?>(null) }
    var plan by remember { mutableStateOf<AlternativePlan?>(null) }
    var phrase by remember { mutableStateOf<PersonalPhrase?>(null) }
    var delete by remember { mutableStateOf<(() -> Unit)?>(null) }
    val clipboard = androidx.compose.ui.platform.LocalClipboardManager.current
    Column(modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = { Text(state.scenario?.title ?: stringResource(R.string.scenario_detail_title)) },
            navigationIcon = { TextButton(onClick = onBackClick) { Text(stringResource(R.string.content_description_back)) } },
            actions = { TextButton(onClick = onQueryModeClick) { Text(stringResource(R.string.ui_quick_view)) } },
        )
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            if (state.isLoading) item { CircularProgressIndicator() }
            state.error?.let { item { Text(stringResource(it), color = MaterialTheme.colorScheme.error) } }
            item {
                Section(stringResource(R.string.preparation_title)) {
                    val p = state.preparation
                    if (p == null) Text(stringResource(R.string.ui_add_information_that_helps_you_prepare))
                    else {
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
                    TextButton(onClick = { preparation = true }) {
                        Text(if (p == null) stringResource(R.string.button_add_preparation) else stringResource(R.string.ui_edit_preparation))
                    }
                }
            }
            item {
                Text(stringResource(R.string.possible_situations_title), style = MaterialTheme.typography.titleLarge)
                TextButton(
                    onClick = {
                        situation = PossibleSituation(scenarioId = scenarioId, description = "")
                    }
                ) {
                    Text(stringResource(R.string.ui_add_situation))
                }
            }
            state.situations.forEach { s ->
                item(key = "s-${s.id}") {
                    Section(s.description) {
                        s.likelihood?.let {
                            Text(if (it == SituationLikelihood.POSSIBLE) stringResource(R.string.likelihood_possible) else stringResource(R.string.likelihood_uncertain))
                        }
                        Row {
                            TextButton(onClick = { situation = s }) { Text(stringResource(R.string.ui_edit_situation)) }
                            TextButton(onClick = { delete = { vm.deleteSituation(s.id) } }) {
                                Text(stringResource(R.string.ui_delete_situation))
                            }
                        }
                    }
                }
            }
            item {
                Text(stringResource(R.string.alternative_plans_title), style = MaterialTheme.typography.titleLarge)
                TextButton(
                    onClick = {
                        plan =
                            AlternativePlan(scenarioId = scenarioId, title = "", description = "")
                    }
                ) {
                    Text(stringResource(R.string.ui_add_alternative))
                }
            }
            state.plans.forEachIndexed { index, p ->
                item(key = "p-${p.id}") {
                    Section(p.title) {
                        Text(p.description)
                        p.possibleSituationId?.let { id ->
                            state.situations
                                .find { it.id == id }
                                ?.let { Text(stringResource(R.string.ui_alternative_situation, it.description)) }
                        }
                        Row {
                            TextButton(onClick = { plan = p }) { Text(stringResource(R.string.ui_edit_alternative)) }
                            TextButton(onClick = { delete = { vm.deletePlan(p.id) } }) {
                                Text(stringResource(R.string.ui_delete_alternative))
                            }
                        }
                        Row {
                            TextButton(
                                enabled = index > 0,
                                onClick = {
                                    vm.reorderPlans(
                                        state.plans.toMutableList().apply {
                                            add(index - 1, removeAt(index))
                                        }
                                    )
                                },
                            ) {
                                Text(stringResource(R.string.ui_move_alternative_up))
                            }
                            TextButton(
                                enabled = index < state.plans.lastIndex,
                                onClick = {
                                    vm.reorderPlans(
                                        state.plans.toMutableList().apply {
                                            add(index + 1, removeAt(index))
                                        }
                                    )
                                },
                            ) {
                                Text(stringResource(R.string.ui_move_alternative_down))
                            }
                        }
                    }
                }
            }
            item {
                Text(stringResource(R.string.personal_phrases_title), style = MaterialTheme.typography.titleLarge)
                TextButton(
                    onClick = { phrase = PersonalPhrase(scenarioId = scenarioId, text = "") }
                ) {
                    Text(stringResource(R.string.ui_add_phrase))
                }
            }
            state.phrases.forEachIndexed { index, p ->
                item(key = "f-${p.id}") {
                    Section(p.text) {
                        Row {
                            TextButton(onClick = { phrase = p }) { Text(stringResource(R.string.ui_edit_phrase)) }
                            TextButton(onClick = { delete = { vm.deletePhrase(p.id) } }) {
                                Text(stringResource(R.string.ui_delete_phrase))
                            }
                            TextButton(
                                onClick = {
                                    clipboard.setText(
                                        androidx.compose.ui.text.AnnotatedString(p.text)
                                    )
                                }
                            ) {
                                Text(stringResource(R.string.ui_copy))
                            }
                        }
                        Row {
                            TextButton(
                                enabled = index > 0,
                                onClick = {
                                    vm.reorderPhrases(
                                        state.phrases.toMutableList().apply {
                                            add(index - 1, removeAt(index))
                                        }
                                    )
                                },
                            ) {
                                Text(stringResource(R.string.ui_move_phrase_up))
                            }
                            TextButton(
                                enabled = index < state.phrases.lastIndex,
                                onClick = {
                                    vm.reorderPhrases(
                                        state.phrases.toMutableList().apply {
                                            add(index + 1, removeAt(index))
                                        }
                                    )
                                },
                            ) {
                                Text(stringResource(R.string.ui_move_phrase_down))
                            }
                        }
                    }
                }
            }
            state.feedbackMessage?.let { item { Text(stringResource(it)) } }
            item { Spacer(Modifier.height(16.dp)) }
        }
    }
    if (preparation)
        PreparationDialog(state.preparation, { preparation = false }) {
            vm.savePreparation(it)
            preparation = false
        }
    situation?.let { s ->
        SituationDialog(s, { situation = null }) {
            if (s.id == 0L) vm.addSituation(it.description, it.likelihood)
            else vm.updateSituation(it)
            situation = null
        }
    }
    plan?.let { p ->
        PlanDialog(p, state.situations, { plan = null }) {
            if (p.id == 0L) vm.addPlan(it.title, it.description, it.possibleSituationId)
            else vm.updatePlan(it)
            plan = null
        }
    }
    phrase?.let { p ->
        TextEditDialog(stringResource(R.string.ui_personal_phrase), p.text, "", false, { phrase = null }) { text, _ ->
            if (p.id == 0L) vm.addPhrase(text) else vm.updatePhrase(p.copy(text = text))
            phrase = null
        }
    }
    delete?.let { action ->
        AlertDialog(
            onDismissRequest = { delete = null },
            title = { Text(stringResource(R.string.ui_delete_record)) },
            text = { Text(stringResource(R.string.ui_this_action_cannot_be_undone)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        action()
                        delete = null
                    }
                ) {
                    Text(stringResource(R.string.button_delete))
                }
            },
            dismissButton = { TextButton(onClick = { delete = null }) { Text(stringResource(R.string.button_cancel)) } },
        )
    }
}

@Composable
private fun Section(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium)
            content()
        }
    }
}

@Composable
fun TextEditDialog(
    title: String,
    initial: String,
    initialDescription: String,
    hasDescription: Boolean = true,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
) {
    var text by remember { mutableStateOf(initial) }
    var description by remember { mutableStateOf(initialDescription) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    text,
                    { text = it },
                    label = { Text(if (hasDescription) stringResource(R.string.field_title_label) else stringResource(R.string.ui_text)) },
                )
                if (hasDescription)
                    OutlinedTextField(
                        description,
                        { description = it },
                        label = { Text(stringResource(R.string.field_description_label)) },
                    )
            }
        },
        confirmButton = {
            TextButton(
                enabled = text.isNotBlank(),
                onClick = { onSave(text.trim(), description) },
            ) {
                Text(stringResource(R.string.button_save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } },
    )
}

@Composable
private fun PreparationDialog(
    original: ScenarioPreparation?,
    onDismiss: () -> Unit,
    onSave: (ScenarioPreparation) -> Unit,
) {
    var value by remember { mutableStateOf(original ?: ScenarioPreparation(scenarioId = 0)) }
    var date by remember { mutableStateOf(formatDate(original?.scheduledAt)) }
    var invalid by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.preparation_title)) },
        text = {
            androidx.compose.foundation.rememberScrollState().let { scroll ->
                Column(
                    Modifier.heightIn(max = 450.dp).then(Modifier.verticalScroll(scroll)),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    OutlinedTextField(
                        value.location,
                        { value = value.copy(location = it) },
                        label = { Text(stringResource(R.string.ui_location)) },
                    )
                    OutlinedTextField(
                        date,
                        {
                            date = it
                            invalid = false
                        },
                        label = { Text(stringResource(R.string.ui_date_and_time_optional)) },
                        placeholder = { Text(stringResource(R.string.ui_dd_mm_yyyy_hh_mm)) },
                        isError = invalid,
                    )
                    if (invalid)
                        Text(
                            stringResource(R.string.ui_enter_a_valid_date_dd_mm_yyyy_hh_mm),
                            color = MaterialTheme.colorScheme.error,
                        )
                    OutlinedTextField(
                        value.whatToExpect,
                        { value = value.copy(whatToExpect = it) },
                        label = { Text(stringResource(R.string.ui_what_to_expect)) },
                    )
                    OutlinedTextField(
                        value.whatToBring,
                        { value = value.copy(whatToBring = it) },
                        label = { Text(stringResource(R.string.ui_what_to_bring)) },
                    )
                    OutlinedTextField(
                        value.importantInformation,
                        { value = value.copy(importantInformation = it) },
                        label = { Text(stringResource(R.string.preparation_important_info_label)) },
                    )
                    OutlinedTextField(
                        value.personalNotes,
                        { value = value.copy(personalNotes = it) },
                        label = { Text(stringResource(R.string.ui_notes)) },
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    try {
                        onSave(value.copy(scheduledAt = parseDate(date)))
                    } catch (_: Exception) {
                        invalid = true
                    }
                }
            ) {
                Text(stringResource(R.string.button_save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } },
    )
}

@Composable
private fun SituationDialog(
    original: PossibleSituation,
    onDismiss: () -> Unit,
    onSave: (PossibleSituation) -> Unit,
) {
    var value by remember { mutableStateOf(original) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ui_possible_situation)) },
        text = {
            Column {
                OutlinedTextField(
                    value.description,
                    { value = value.copy(description = it) },
                    label = { Text(stringResource(R.string.field_description_label)) },
                )
                listOf<SituationLikelihood?>(
                        null,
                        SituationLikelihood.POSSIBLE,
                        SituationLikelihood.UNCERTAIN,
                    )
                    .forEach { option ->
                        FilterChip(
                            selected = value.likelihood == option,
                            onClick = { value = value.copy(likelihood = option) },
                            label = {
                                Text(
                                    when (option) {
                                        null -> stringResource(R.string.ui_not_specified)
                                        SituationLikelihood.POSSIBLE -> stringResource(R.string.likelihood_possible)
                                        else -> stringResource(R.string.likelihood_uncertain)
                                    }
                                )
                            },
                        )
                    }
            }
        },
        confirmButton = {
            TextButton(enabled = value.description.isNotBlank(), onClick = { onSave(value) }) {
                Text(stringResource(R.string.button_save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } },
    )
}

@Composable
private fun PlanDialog(
    original: AlternativePlan,
    situations: List<PossibleSituation>,
    onDismiss: () -> Unit,
    onSave: (AlternativePlan) -> Unit,
) {
    var value by remember { mutableStateOf(original) }
    var expanded by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.ui_alternative_plan)) },
        text = {
            Column {
                OutlinedTextField(
                    value.title,
                    { value = value.copy(title = it) },
                    label = { Text(stringResource(R.string.field_title_label)) },
                )
                OutlinedTextField(
                    value.description,
                    { value = value.copy(description = it) },
                    label = { Text(stringResource(R.string.field_description_label)) },
                )
                Box {
                    TextButton(onClick = { expanded = true }) {
                        Text(
                            situations.find { it.id == value.possibleSituationId }?.description
                                ?: stringResource(R.string.ui_no_related_situation)
                        )
                    }
                    DropdownMenu(expanded, { expanded = false }) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.ui_no_related_situation)) },
                            onClick = {
                                value = value.copy(possibleSituationId = null)
                                expanded = false
                            },
                        )
                        situations.forEach { s ->
                            DropdownMenuItem(
                                text = { Text(s.description) },
                                onClick = {
                                    value = value.copy(possibleSituationId = s.id)
                                    expanded = false
                                },
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(enabled = value.title.isNotBlank(), onClick = { onSave(value) }) {
                Text(stringResource(R.string.button_save))
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text(stringResource(R.string.button_cancel)) } },
    )
}
