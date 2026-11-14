package br.com.teadescomplica.domain.repository

import br.com.teadescomplica.domain.model.BackupSnapshot
import kotlinx.coroutines.flow.Flow

/**
 * Contrato para backup e restauração de dados do aplicativo.
 *
 * Todas as operações são suspensas e devem ser executadas fora da thread
 * principal. Nenhuma operação acessa a rede diretamente.
 *
 * O backup é criptografado com AES-256-GCM usando uma chave derivada
 * de uma senha fornecida pelo usuário via PBKDF2-HMAC-SHA256.
 */
interface BackupRepository {

    /** Observa se existe um backup em andamento. */
    fun isBackupInProgress(): Flow<Boolean>

    /** Observa se existe uma restauração em andamento. */
    fun isRestoreInProgress(): Flow<Boolean>

    /** Observa a última mensagem de erro, se houver. */
    fun getLastError(): Flow<BackupFeedback?>

    /** Observa a última mensagem de sucesso, se houver. */
    fun getLastSuccess(): Flow<BackupFeedback?>

    /**
     * Cria um snapshot consistente de todos os dados do aplicativo.
     *
     * Não acessa DAOs diretamente; usa os repositórios de domínio.
     */
    suspend fun createSnapshot(): BackupSnapshot

    /**
     * Criptografa um snapshot e grava no destino selecionado pelo usuário
     * via Android Storage Access Framework.
     *
     * @param snapshot dados a serem salvos.
     * @param password senha fornecida pelo usuário (não é armazenada).
     * @param outputUri URI do destino retornada pelo seletor de documentos.
     * @return true se a gravação foi concluída com sucesso.
     */
    suspend fun exportEncryptedBackup(
        snapshot: BackupSnapshot,
        password: String,
        outputUri: android.net.Uri
    ): Boolean

    /**
     * Importa e descriptografa um backup de uma URI selecionada pelo usuário.
     *
     * @param inputUri URI da origem retornada pelo seletor de documentos.
     * @param password senha fornecida pelo usuário.
     * @return o snapshot descriptografado e validado, ou null se falhar.
     */
    suspend fun importEncryptedBackup(
        inputUri: android.net.Uri,
        password: String
    ): BackupSnapshot?

    /**
     * Aplica um snapshot restaurando todos os dados do aplicativo.
     *
     * Usa transações Room para substituir os dados de forma atômica.
     * As preferências do DataStore são restauradas separadamente.
     *
     * @param snapshot dados validados a serem restaurados.
     * @return true se a restauração foi concluída com sucesso.
     */
    suspend fun applySnapshot(snapshot: BackupSnapshot): Boolean

    /** Limpa mensagens de feedback. */
    fun clearFeedback()
}

/** Result of a backup operation, independent of the interface language. */
enum class BackupFeedback {
    CREATED,
    CREATE_FAILED,
    READ_FAILED,
    RESTORED,
    RESTORE_FAILED,
}
