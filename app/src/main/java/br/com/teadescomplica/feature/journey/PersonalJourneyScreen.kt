@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.journey

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.designsystem.TeaTopAppBar
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.*

@Composable
fun PersonalJourneyScreen(
    onRecordExperienceClick: () -> Unit,
    onStrategyLibraryClick: () -> Unit,
    onExperienceClick: (PersonalExperience) -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: PersonalJourneyViewModel = viewModel {
        PersonalJourneyViewModel(DiModule.getPersonalExperienceRepository())
    }
    val state by vm.uiState.collectAsState()
    Column(modifier.fillMaxSize()) {
        Text(
            stringResource(R.string.journey_title),
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp),
        )
        if (LocalConfiguration.current.fontScale >= 1.5f) {
            Column(
                Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = onRecordExperienceClick, modifier = Modifier.fillMaxWidth()) {
                    Text(stringResource(R.string.experience_record_title))
                }
                OutlinedButton(
                    onClick = onStrategyLibraryClick,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(stringResource(R.string.ui_strategies))
                }
            }
        } else {
            Row(
                Modifier.padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Button(onClick = onRecordExperienceClick) { Text(stringResource(R.string.experience_record_title)) }
                OutlinedButton(onClick = onStrategyLibraryClick) { Text(stringResource(R.string.ui_strategies)) }
            }
        }
        OutlinedTextField(
            state.searchQuery,
            { vm.searchExperiences(it) },
            label = { Text(stringResource(R.string.journey_search_label)) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.experiences.isEmpty())
                item {
                    Text(
                        if (state.searchQuery.isBlank()) stringResource(R.string.journey_empty_title)
                        else stringResource(R.string.journey_search_empty_title)
                    )
                }
            items(state.experiences, key = { it.id }) { e ->
                Card(Modifier.fillMaxWidth().clickable { onExperienceClick(e) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(e.title, style = MaterialTheme.typography.titleMedium)
                        Text(e.description)
                        if (e.occurredAt != null)
                            Text(br.com.teadescomplica.feature.scenarios.formatDate(e.occurredAt))
                    }
                }
            }
        }
    }
}

@Composable
fun StrategyLibraryScreen(
    onCreateStrategyClick: () -> Unit,
    onStrategyClick: (PersonalStrategy) -> Unit,
    onBackClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val vm: StrategyLibraryViewModel = viewModel {
        StrategyLibraryViewModel(
            DiModule.getPersonalStrategyRepository(),
            DiModule.getPersonalExperienceRepository(),
        )
    }
    val state by vm.uiState.collectAsState()
    Column(modifier.fillMaxSize()) {
        TeaTopAppBar(
            title = { Text(stringResource(R.string.content_description_strategy_library)) },
            navigationIcon = { TextButton(onClick = onBackClick) { Text(stringResource(R.string.content_description_back)) } },
        )
        Button(onClick = onCreateStrategyClick, modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(stringResource(R.string.ui_create_strategy))
        }
        OutlinedTextField(
            state.searchQuery,
            { vm.searchStrategies(it) },
            label = { Text(stringResource(R.string.ui_search_strategies)) },
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        )
        LazyColumn(
            Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            if (state.strategies.isEmpty())
                item {
                    Text(
                        if (state.searchQuery.isBlank()) stringResource(R.string.strategy_library_empty_title)
                        else stringResource(R.string.strategy_search_empty)
                    )
                }
            items(state.strategies, key = { it.id }) { s ->
                Card(Modifier.fillMaxWidth().clickable { onStrategyClick(s) }) {
                    Column(Modifier.padding(16.dp)) {
                        Text(s.title, style = MaterialTheme.typography.titleMedium)
                        Text(s.description)
                    }
                }
            }
        }
    }
}
