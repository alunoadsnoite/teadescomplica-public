@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.core.navigation

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R
import br.com.teadescomplica.core.locale.AppLanguage

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import br.com.teadescomplica.core.datastore.ThemePreference
import br.com.teadescomplica.core.designsystem.TeaAppTheme
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.feature.home.HomeScreen
import br.com.teadescomplica.feature.journey.ExperienceDetailScreen
import br.com.teadescomplica.feature.journey.ExperienceRecordScreen
import br.com.teadescomplica.feature.journey.PersonalJourneyScreen
import br.com.teadescomplica.feature.journey.StrategyDetailScreen
import br.com.teadescomplica.feature.journey.StrategyLibraryScreen
import br.com.teadescomplica.feature.scenarios.PreparationQueryScreen
import br.com.teadescomplica.feature.scenarios.ScenarioDetailScreen
import br.com.teadescomplica.feature.scenarios.ScenarioListScreen
import br.com.teadescomplica.feature.settings.BackupRestoreScreen
import br.com.teadescomplica.feature.settings.SettingsScreen

/**
 * Navegação principal do aplicativo.
 *
 * Quatro destinos na NavigationBar: Início, Meus cenários, Minha Jornada e Configurações. As telas
 * de detalhes, registro e estratégias são abertas como sobreposição via parâmetros de navegação
 * (não são destinos da NavigationBar).
 *
 * @OptIn(ExperimentalMaterial3Api::class) necessário para NavigationBar.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TeaNavigation(language: AppLanguage, onLanguageChange: (AppLanguage) -> Unit) {
    val preferenceRepository = DiModule.getPreferenceRepository()
    var selectedScreen by rememberSaveable { mutableStateOf(Screen.Home) }
    val themePreference by
        preferenceRepository.getThemePreference().collectAsState(initial = ThemePreference.SYSTEM)

    // Estados de navegação para sobreposições (não persistem na barra inferior).
    var detailScenarioId by rememberSaveable { mutableStateOf<Long?>(null) }
    var queryScenarioId by rememberSaveable { mutableStateOf<Long?>(null) }
    var journeyExperienceId by rememberSaveable { mutableStateOf<Long?>(null) }
    var journeyStrategyId by rememberSaveable { mutableStateOf<Long?>(null) }
    var editingExperienceId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showRecordExperience by rememberSaveable { mutableStateOf(false) }
    var showStrategyLibrary by rememberSaveable { mutableStateOf(false) }
    var showBackupRestore by rememberSaveable { mutableStateOf(false) }

    BackHandler(
        enabled =
            showRecordExperience ||
                showStrategyLibrary ||
                showBackupRestore ||
                journeyExperienceId != null ||
                journeyStrategyId != null ||
                detailScenarioId != null ||
                queryScenarioId != null
    ) {
        when {
            showRecordExperience -> showRecordExperience = false
            journeyStrategyId != null -> {
                journeyStrategyId = null
                showStrategyLibrary = true
            }
            showStrategyLibrary -> showStrategyLibrary = false
            journeyExperienceId != null -> journeyExperienceId = null
            detailScenarioId != null -> detailScenarioId = null
            queryScenarioId != null -> {
                detailScenarioId = queryScenarioId
                queryScenarioId = null
            }
            showBackupRestore -> showBackupRestore = false
        }
    }

    TeaAppTheme(themePreference = themePreference) {
        androidx.compose.material3.Scaffold(
            bottomBar = {
                val selectScreen: (Screen) -> Unit = { screen ->
                    selectedScreen = screen
                    detailScenarioId = null
                    queryScenarioId = null
                    journeyExperienceId = null
                    journeyStrategyId = null
                    showRecordExperience = false
                    showStrategyLibrary = false
                    showBackupRestore = false
                }
                if (LocalConfiguration.current.fontScale >= 1.5f) {
                    Surface(color = MaterialTheme.colorScheme.surfaceContainer) {
                        Column(
                            Modifier.fillMaxWidth()
                                .navigationBarsPadding()
                                .padding(horizontal = 8.dp)
                        ) {
                            Screen.entries.chunked(2).forEach { pair ->
                                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                                    pair.forEach { screen ->
                                        TextButton(
                                            onClick = { selectScreen(screen) },
                                            modifier =
                                                Modifier.weight(1f)
                                                    .fillMaxHeight()
                                                    .heightIn(min = 56.dp)
                                                    .semantics {
                                                        selected = selectedScreen == screen
                                                    },
                                        ) {
                                            screen.label()
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    NavigationBar {
                        Screen.entries.forEach { screen ->
                            NavigationBarItem(
                                selected = selectedScreen == screen,
                                onClick = { selectScreen(screen) },
                                icon = { screen.icon() },
                                label = { screen.label() },
                            )
                        }
                    }
                }
            }
        ) { padding ->
            // Sobreposições da Minha Jornada (têm prioridade sobre o conteúdo base).
            if (showRecordExperience) {
                ExperienceRecordScreen(
                    experienceId = editingExperienceId,
                    onSaveComplete = { showRecordExperience = false },
                    onCancelClick = { showRecordExperience = false },
                    modifier = Modifier.padding(padding),
                )
            } else if (showStrategyLibrary) {
                StrategyLibraryScreen(
                    onCreateStrategyClick = {
                        showStrategyLibrary = false
                        journeyStrategyId = 0L
                    },
                    onStrategyClick = { strategy ->
                        showStrategyLibrary = false
                        journeyStrategyId = strategy.id
                    },
                    onBackClick = { showStrategyLibrary = false },
                    modifier = Modifier.padding(padding),
                )
            } else if (journeyExperienceId != null) {
                ExperienceDetailScreen(
                    experienceId = journeyExperienceId!!,
                    onBackClick = { journeyExperienceId = null },
                    onEditClick = {
                        editingExperienceId = journeyExperienceId
                        journeyExperienceId = null
                        showRecordExperience = true
                    },
                    modifier = Modifier.padding(padding),
                )
            } else if (journeyStrategyId != null) {
                StrategyDetailScreen(
                    strategyId = journeyStrategyId!!,
                    onBackClick = {
                        journeyStrategyId = null
                        showStrategyLibrary = true
                    },
                    onEditClick = { journeyStrategyId = null },
                    modifier = Modifier.padding(padding),
                )
            } else if (detailScenarioId != null) {
                ScenarioDetailScreen(
                    scenarioId = detailScenarioId!!,
                    onBackClick = { detailScenarioId = null },
                    onQueryModeClick = {
                        queryScenarioId = detailScenarioId
                        detailScenarioId = null
                    },
                    modifier = Modifier.padding(padding),
                )
            } else if (queryScenarioId != null) {
                PreparationQueryScreen(
                    scenarioId = queryScenarioId!!,
                    onBackClick = { queryScenarioId = null },
                    onEditClick = {
                        detailScenarioId = queryScenarioId
                        queryScenarioId = null
                    },
                    modifier = Modifier.padding(padding),
                )
            } else if (showBackupRestore) {
                BackupRestoreScreen(
                    onBackClick = { showBackupRestore = false },
                    modifier = Modifier.padding(padding),
                )
            } else {
                when (selectedScreen) {
                    Screen.Home ->
                        HomeScreen(
                            onScenariosClick = { selectedScreen = Screen.Scenarios },
                            onSettingsClick = { selectedScreen = Screen.Settings },
                            modifier = Modifier.padding(padding),
                        )
                    Screen.Scenarios ->
                        ScenarioListScreen(
                            onHomeClick = { selectedScreen = Screen.Home },
                            onSettingsClick = { selectedScreen = Screen.Settings },
                            onScenarioClick = { scenarioId -> detailScenarioId = scenarioId },
                            modifier = Modifier.padding(padding),
                        )
                    Screen.Journey ->
                        PersonalJourneyScreen(
                            onRecordExperienceClick = {
                                editingExperienceId = null
                                showRecordExperience = true
                            },
                            onStrategyLibraryClick = { showStrategyLibrary = true },
                            onExperienceClick = { experience ->
                                journeyExperienceId = experience.id
                            },
                            modifier = Modifier.padding(padding),
                        )
                    Screen.Settings ->
                        SettingsScreen(
                            onHomeClick = { selectedScreen = Screen.Home },
                            onScenariosClick = { selectedScreen = Screen.Scenarios },
                            onBackupClick = { showBackupRestore = true },
                            language = language,
                            onLanguageChange = onLanguageChange,
                            modifier = Modifier.padding(padding),
                        )
                }
            }
        }
    }
}

/** Telas disponíveis na NavigationBar principal. */
enum class Screen(val id: String) {
    Home("home"),
    Scenarios("scenarios"),
    Journey("journey"),
    Settings("settings");

    @Composable
    fun icon() =
        when (this) {
            Home -> Icon(imageVector = Icons.Default.Home, contentDescription = null)
            Scenarios -> Icon(imageVector = Icons.Default.List, contentDescription = null)
            Journey -> Icon(imageVector = Icons.Default.History, contentDescription = null)
            Settings -> Icon(imageVector = Icons.Default.Settings, contentDescription = null)
        }

    @Composable
    fun label() =
        when (this) {
            Home -> androidx.compose.material3.Text(text = stringResource(R.string.ui_home))
            Scenarios -> androidx.compose.material3.Text(text = stringResource(R.string.button_my_scenarios))
            Journey -> androidx.compose.material3.Text(text = stringResource(R.string.journey_title))
            Settings -> androidx.compose.material3.Text(text = stringResource(R.string.button_settings))
        }
}
