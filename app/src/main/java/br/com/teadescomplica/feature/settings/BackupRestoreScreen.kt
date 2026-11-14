package br.com.teadescomplica.feature.settings

import androidx.compose.ui.res.stringResource
import br.com.teadescomplica.R

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import br.com.teadescomplica.core.di.DiModule
import br.com.teadescomplica.domain.model.BackupSnapshot
import br.com.teadescomplica.domain.repository.BackupFeedback
import kotlinx.coroutines.launch

@Composable
fun BackupRestoreScreen(onBackClick: () -> Unit, modifier: Modifier = Modifier) {
    val vm: BackupViewModel = viewModel { BackupViewModel(DiModule.getBackupRepository()) }
    val scope = rememberCoroutineScope()
    val creating by vm.isBackupInProgress.collectAsState()
    val restoring by vm.isRestoreInProgress.collectAsState()
    val error by vm.error.collectAsState()
    val success by vm.success.collectAsState()
    var action by remember { mutableStateOf<String?>(null) }
    var password by remember { mutableStateOf("") }
    var confirm by remember { mutableStateOf("") }
    var pendingSnapshot by remember { mutableStateOf<BackupSnapshot?>(null) }
    val create =
        rememberLauncherForActivityResult(
            ActivityResultContracts.CreateDocument("application/octet-stream")
        ) { uri ->
            if (uri != null)
                scope.launch {
                    try {
                        vm.createAndExportBackup(vm.createSnapshot(), uri)
                    } finally {
                        vm.clearPasswordForm()
                    }
                }
            else vm.clearPasswordForm()
        }
    val open =
        rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) {
                vm.setSelectedRestoreUri(uri)
                password = ""
                action = "restore"
            }
        }
    Column(
        modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(stringResource(R.string.backup_and_restore_title), style = MaterialTheme.typography.headlineMedium)
        Text(
            stringResource(R.string.ui_protect_your_records_in_a_password_encrypted_file_choose_where_to)
        )
        Button(
            onClick = {
                vm.clearFeedback()
                password = ""
                confirm = ""
                action = "create"
            },
            enabled = !creating && !restoring,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (creating) stringResource(R.string.backup_creating) else stringResource(R.string.backup_create_button))
        }
        OutlinedButton(
            onClick = {
                vm.clearFeedback()
                open.launch(arrayOf("*/*"))
            },
            enabled = !creating && !restoring,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (restoring) stringResource(R.string.backup_restoring) else stringResource(R.string.backup_restore_button))
        }
        Text(stringResource(R.string.ui_if_you_forget_the_password_you_cannot_recover_the_backup_contents))
        error?.let { Text(stringResource(it.stringRes), color = MaterialTheme.colorScheme.error) }
        success?.let { Text(stringResource(it.stringRes)) }
        TextButton(onClick = onBackClick) { Text(stringResource(R.string.content_description_back)) }
    }
    if (action != null)
        AlertDialog(
            onDismissRequest = {
                action = null
                password = ""
                confirm = ""
                vm.clearPasswordForm()
            },
            title = {
                Text(if (action == "create") stringResource(R.string.backup_password_title) else stringResource(R.string.restore_password_title))
            },
            text = {
                Column(
                    Modifier.verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedTextField(
                        password,
                        { password = it },
                        label = { Text(stringResource(R.string.backup_password_label)) },
                        singleLine = true,
                        visualTransformation = PasswordVisualTransformation(),
                    )
                    if (action == "create") {
                        OutlinedTextField(
                            confirm,
                            { confirm = it },
                            label = { Text(stringResource(R.string.backup_password_confirm_label)) },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                        )
                        Text(stringResource(R.string.ui_use_at_least_8_characters))
                    }
                }
            },
            confirmButton = {
                TextButton(
                    enabled =
                        if (action == "create") password.length >= 8 && password == confirm
                        else password.isNotBlank(),
                    onClick = {
                        vm.setPassword(password)
                        if (action == "create") {
                            create.launch(
                                "TEADescomplica_backup_${java.time.LocalDate.now()}.teadbackup"
                            )
                        } else {
                            vm.selectedRestoreUri.value?.let { uri ->
                                scope.launch {
                                    try {
                                        pendingSnapshot = vm.importBackup(uri)
                                    } finally {
                                        vm.clearPasswordForm()
                                    }
                                }
                            }
                        }
                        action = null
                        password = ""
                        confirm = ""
                    },
                ) {
                    Text(stringResource(R.string.ui_continue))
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        action = null
                        password = ""
                        confirm = ""
                        vm.clearPasswordForm()
                    }
                ) {
                    Text(stringResource(R.string.button_cancel))
                }
            },
        )
    pendingSnapshot?.let { snapshot ->
        AlertDialog(
            onDismissRequest = { pendingSnapshot = null },
            title = { Text(stringResource(R.string.ui_replace_current_data)) },
            text = { Text(stringResource(R.string.ui_restoring_will_replace_your_current_records_with_the_data_in_this)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        pendingSnapshot = null
                        scope.launch { vm.applyBackup(snapshot) }
                    }
                ) {
                    Text(stringResource(R.string.ui_restore))
                }
            },
            dismissButton = {
                TextButton(onClick = { pendingSnapshot = null }) { Text(stringResource(R.string.button_cancel)) }
            },
        )
    }
}

@get:androidx.annotation.StringRes
private val BackupFeedback.stringRes: Int
    get() = when (this) {
        BackupFeedback.CREATED -> R.string.backup_created
        BackupFeedback.CREATE_FAILED -> R.string.backup_create_failed
        BackupFeedback.READ_FAILED -> R.string.backup_read_failed
        BackupFeedback.RESTORED -> R.string.backup_restored
        BackupFeedback.RESTORE_FAILED -> R.string.backup_restore_failed
    }
