package br.com.teadescomplica.feature.settings

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.teadescomplica.domain.repository.BackupFeedback
import br.com.teadescomplica.domain.repository.BackupRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn

/**
 * ViewModel da tela de Backup e Restauração.
 *
 * Gerencia o fluxo de criação e restauração de backups criptografados, incluindo entrada de senha,
 * seleção de arquivo via SAF e feedback ao usuário.
 */
class BackupViewModel(private val backupRepository: BackupRepository) : ViewModel() {

    private val _isBackupInProgress =
        backupRepository.isBackupInProgress().stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isBackupInProgress: StateFlow<Boolean> = _isBackupInProgress

    private val _isRestoreInProgress =
        backupRepository
            .isRestoreInProgress()
            .stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val isRestoreInProgress: StateFlow<Boolean> = _isRestoreInProgress

    private val _error =
        backupRepository.getLastError().stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val error: StateFlow<BackupFeedback?> = _error

    private val _success =
        backupRepository.getLastSuccess().stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val success: StateFlow<BackupFeedback?> = _success

    /** Estado da senha digitada pelo usuário. */
    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    /** Confirmação da senha. */
    private val _passwordConfirm = MutableStateFlow("")
    val passwordConfirm: StateFlow<String> = _passwordConfirm.asStateFlow()

    /** Se está mostrando o campo de confirmação de senha. */
    private val _isConfirmingPassword = MutableStateFlow(false)
    val isConfirmingPassword: StateFlow<Boolean> = _isConfirmingPassword.asStateFlow()

    /** URI do arquivo selecionado para restauração. */
    private val _selectedRestoreUri = MutableStateFlow<Uri?>(null)
    val selectedRestoreUri: StateFlow<Uri?> = _selectedRestoreUri.asStateFlow()

    init {
        backupRepository.clearFeedback()
    }

    fun setPassword(password: String) {
        _password.value = password
    }

    fun setPasswordConfirm(confirm: String) {
        _passwordConfirm.value = confirm
    }

    fun setConfirmingPassword(confirming: Boolean) {
        _isConfirmingPassword.value = confirming
    }

    fun setSelectedRestoreUri(uri: Uri?) {
        _selectedRestoreUri.value = uri
    }

    /** Valida se a senha atende aos requisitos mínimos. */
    fun isPasswordValid(): Boolean {
        val pwd = _password.value
        return pwd.length >= 8
    }

    /** Valida se senha e confirmação coincidem. */
    fun doPasswordsMatch(): Boolean {
        return _password.value.isNotBlank() && _password.value == _passwordConfirm.value
    }

    /** Limpa o formulário de senha. */
    fun clearPasswordForm() {
        _password.value = ""
        _passwordConfirm.value = ""
        _isConfirmingPassword.value = false
    }

    /** Limpa feedbacks. */
    fun clearFeedback() {
        backupRepository.clearFeedback()
    }

    /** Cria um snapshot e exporta como backup criptografado. */
    suspend fun createAndExportBackup(
        snapshot: br.com.teadescomplica.domain.model.BackupSnapshot,
        outputUri: Uri,
    ): Boolean {
        return backupRepository.exportEncryptedBackup(snapshot, _password.value, outputUri)
    }

    /** Importa e descriptografa um backup de uma URI. */
    suspend fun importBackup(uri: Uri): br.com.teadescomplica.domain.model.BackupSnapshot? {
        return backupRepository.importEncryptedBackup(uri, _password.value)
    }

    /** Aplica um snapshot restaurando todos os dados. */
    suspend fun applyBackup(snapshot: br.com.teadescomplica.domain.model.BackupSnapshot): Boolean {
        return backupRepository.applySnapshot(snapshot)
    }

    /** Cria um snapshot completo dos dados atuais. */
    suspend fun createSnapshot(): br.com.teadescomplica.domain.model.BackupSnapshot {
        return backupRepository.createSnapshot()
    }
}
