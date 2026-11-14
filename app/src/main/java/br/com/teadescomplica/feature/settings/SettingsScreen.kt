@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package br.com.teadescomplica.feature.settings

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import android.content.Context
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.Role
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.datastore.ThemePreference
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.core.locale.AppLanguage

@Composable
internal fun SettingsScreen(
    onHomeClick: () -> Unit,
    onScenariosClick: () -> Unit,
    onBackupClick: () -> Unit,
    language: AppLanguage,
    onLanguageChange: (AppLanguage) -> Unit,
    modifier: Modifier = Modifier,
) {
    val viewModel: SettingsViewModel = viewModel {
        SettingsViewModel(DiModule.getPreferenceRepository())
    }
    val uiState = viewModel.uiState.collectAsState().value
    val context: Context = LocalContext.current
    var copyFeedback by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = stringResource(id = R.string.settings_title),
            style = MaterialTheme.typography.displayMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        Text(stringResource(R.string.settings_language), style = MaterialTheme.typography.titleMedium)
        Column(Modifier.selectableGroup()) {
            AppLanguage.entries.forEach { option ->
                Row(
                    modifier = Modifier.fillMaxWidth()
                        .selectable(
                            selected = language == option,
                            role = Role.RadioButton,
                            onClick = { onLanguageChange(option) },
                        )
                        .padding(vertical = 8.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                ) {
                    RadioButton(selected = language == option, onClick = null)
                    Text(stringResource(option.label), modifier = Modifier.padding(start = 12.dp))
                }
            }
        }

        Text(
            text = stringResource(id = R.string.settings_theme),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = themeLabel(uiState.themePreference),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )

        ThemePreference.entries.forEach { option ->
            FilterChip(
                selected = uiState.themePreference == option,
                onClick = { viewModel.setTheme(option) },
                label = { Text(themeLabel(option)) },
            )
        }

        var reducedMotion by
            remember(uiState.reducedMotion) { mutableStateOf(uiState.reducedMotion) }
        Checkbox(
            checked = reducedMotion,
            onCheckedChange = { checked ->
                reducedMotion = checked
                viewModel.setReducedMotion(checked)
            },
        )
        Text(
            text = stringResource(id = R.string.settings_reduced_motion),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(id = R.string.settings_reduced_motion_summary),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Divider(modifier = Modifier.fillMaxWidth())

        Text(
            text = stringResource(id = R.string.settings_privacy),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(id = R.string.privacy_text),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(id = R.string.local_data_text),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Divider(modifier = Modifier.fillMaxWidth())

        Text(
            text = stringResource(id = R.string.settings_version),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = uiState.versionName,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Divider(modifier = Modifier.fillMaxWidth())

        // Seção Backup e Restauração
        Text(
            text = stringResource(id = R.string.backup_and_restore_title),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Button(onClick = onBackupClick, modifier = Modifier.fillMaxWidth()) {
            Text(text = stringResource(id = R.string.backup_and_restore_button))
        }
        Text(
            text = stringResource(id = R.string.backup_and_restore_summary),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        Divider(modifier = Modifier.fillMaxWidth())

        Text(
            text = stringResource(id = R.string.settings_developer),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            text = stringResource(id = R.string.settings_developer_name),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
        )
        val email = stringResource(id = R.string.settings_developer_email)
        val noEmailApp = stringResource(R.string.ui_no_email_app_is_available)
        TextButton(
            onClick = {
                val emailIntent =
                    android.content.Intent(
                        android.content.Intent.ACTION_SENDTO,
                        android.net.Uri.fromParts("mailto", email, null),
                    )
                try {
                    context.startActivity(emailIntent)
                } catch (_: android.content.ActivityNotFoundException) {
                    android.widget.Toast.makeText(
                            context,
                            noEmailApp,
                            android.widget.Toast.LENGTH_LONG,
                        )
                        .show()
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(email)
        }
    }
}

@Composable
private fun themeLabel(theme: ThemePreference): String =
    when (theme) {
        ThemePreference.SYSTEM -> stringResource(R.string.theme_system)
        ThemePreference.LIGHT -> stringResource(R.string.theme_light)
        ThemePreference.DARK -> stringResource(R.string.theme_dark)
    }
