package br.com.teadescomplica.data.crypto

import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets

/**
 * Definição do formato de arquivo de backup do TEA Descomplica (.teadbackup).
 *
 * Estrutura do arquivo (todas as inteiros são big-endian):
 *
 * [0..3] Magic number: 0x54454144 ("TEAD") [4..7] Formato versão (Int, atualmente 1) [8..11] KDF
 * versão (Int, atualmente 1) [12..15] Cripto versão (Int, atualmente 1) [16..19] Tamanho do salt em
 * bytes (Int) [20..20+s] Salt (s bytes, aleatório por backup) [20+s..23+s] Tamanho do nonce em
 * bytes (Int, 12) [24+s..24+s+12] Nonce (12 bytes, único por operação) [24+s+12..27+s+12] Tamanho
 * do AAD em bytes (Int) [28+s+12..28+s+12+aadLen] AAD (cabeçalho autenticado, não criptografado)
 * [28+s+12+aadLen..31+s+12+aadLen] Tamanho do criptograma em bytes (Int) [32+s+12+aadLen..]
 * Criptograma (AES-256-GCM ciphertext + tag de 16 bytes)
 *
 * O AAD contém um cabeçalho JSON não sensível com metadados do formato. O criptograma contém o
 * payload JSON do BackupSnapshot criptografado com AES-256-GCM e autenticado com o AAD.
 *
 * Nenhum dado sensível (senha, chave, texto pessoal) é armazenado em metadados não criptografados.
 */
object BackupFormat {

    /** Identificador mágico do formato "TEAD" em bytes. */
    val MAGIC_NUMBER = byteArrayOf(0x54, 0x45, 0x41, 0x44)

    /** Versão atual do formato. */
    const val FORMAT_VERSION = 1

    /** Extensão do arquivo de backup. */
    const val FILE_EXTENSION = ".teadbackup"

    /** Nome sugerido para arquivos de backup. */
    fun suggestedFileName(): String {
        val now = java.util.Calendar.getInstance()
        val year = now.get(java.util.Calendar.YEAR)
        val month = now.get(java.util.Calendar.MONTH) + 1
        val day = now.get(java.util.Calendar.DAY_OF_MONTH)
        return String.format(
            "TEADescomplica_backup_%04d-%02d-%02d%s",
            year,
            month,
            day,
            FILE_EXTENSION,
        )
    }

    /** Tamanho máximo permitido para um arquivo de backup (50 MB). */
    const val MAX_BACKUP_SIZE_BYTES = 50L * 1024 * 1024

    /** Tamanho mínimo esperado para um cabeçalho válido. */
    const val MIN_HEADER_SIZE = 4 + 4 + 4 + 4 + 4 + 4 + 4 + 4

    /**
     * Serializa o cabeçalho não criptografado (AAD) como JSON.
     *
     * O AAD é autenticado (não pode ser alterado sem invalidar a tag GCM) mas não é criptografado.
     * Contém apenas metadados de formato.
     */
    fun serializeAad(formatVersion: Int, kdfVersion: Int, cryptoVersion: Int): ByteArray {
        return """{"formatVersion":$formatVersion,"kdfVersion":$kdfVersion,"cryptoVersion":$cryptoVersion}"""
            .toByteArray(StandardCharsets.UTF_8)
    }

    /** Deserializa o cabeçalho AAD extraindo as versões. */
    data class AadHeader(val formatVersion: Int, val kdfVersion: Int, val cryptoVersion: Int)

    fun deserializeAad(aad: ByteArray): AadHeader? {
        return try {
            val json = String(aad, StandardCharsets.UTF_8)
            // Parse simples sem biblioteca externa
            val formatVersion = extractIntField(json, "formatVersion")
            val kdfVersion = extractIntField(json, "kdfVersion")
            val cryptoVersion = extractIntField(json, "cryptoVersion")
            AadHeader(formatVersion, kdfVersion, cryptoVersion)
        } catch (e: Exception) {
            null
        }
    }

    private fun extractIntField(json: String, field: String): Int {
        val pattern = "\"$field\":(\\d+)".toRegex()
        val match = pattern.find(json)
        return match?.groupValues?.get(1)?.toInt()
            ?: throw IllegalArgumentException("Campo $field não encontrado no AAD")
    }

    /**
     * Constrói o array de bytes completo do arquivo de backup.
     *
     * @param salt salt aleatório (16 bytes).
     * @param nonce nonce de 96 bits (12 bytes).
     * @param aad cabeçalho autenticado (não criptografado).
     * @param ciphertext criptograma + tag GCM.
     * @return array de bytes do arquivo completo.
     */
    fun buildBackupFile(
        salt: ByteArray,
        nonce: ByteArray,
        aad: ByteArray,
        ciphertext: ByteArray,
    ): ByteArray {
        val buffer =
            ByteBuffer.allocate(
                MAGIC_NUMBER.size + // 4
                    Int.SIZE_BYTES + // formatVersion
                    Int.SIZE_BYTES + // kdfVersion
                    Int.SIZE_BYTES + // cryptoVersion
                    Int.SIZE_BYTES + // salt length
                    salt.size +
                    Int.SIZE_BYTES + // nonce length
                    nonce.size +
                    Int.SIZE_BYTES + // aad length
                    aad.size +
                    Int.SIZE_BYTES + // ciphertext length
                    ciphertext.size
            )

        buffer.put(MAGIC_NUMBER)
        buffer.putInt(FORMAT_VERSION)
        buffer.putInt(CryptoManager.KDF_VERSION)
        buffer.putInt(CryptoManager.CRYPTO_VERSION)
        buffer.putInt(salt.size)
        buffer.put(salt)
        buffer.putInt(nonce.size)
        buffer.put(nonce)
        buffer.putInt(aad.size)
        buffer.put(aad)
        buffer.putInt(ciphertext.size)
        buffer.put(ciphertext)

        return buffer.array()
    }

    /**
     * Parseia um array de bytes de arquivo de backup em seus componentes.
     *
     * @param fileBytes bytes completos do arquivo.
     * @return componentes extraídos ou null se o formato for inválido.
     */
    data class ParsedBackup(
        val salt: ByteArray,
        val nonce: ByteArray,
        val aad: ByteArray,
        val ciphertext: ByteArray,
        val aadHeader: AadHeader,
    )

    fun parseBackupFile(fileBytes: ByteArray): ParsedBackup? {
        return try {
            if (fileBytes.size < MIN_HEADER_SIZE || fileBytes.size > MAX_BACKUP_SIZE_BYTES)
                return null
            val buffer = ByteBuffer.wrap(fileBytes)
            val magic = ByteArray(4).also(buffer::get)
            if (!magic.contentEquals(MAGIC_NUMBER)) return null
            val formatVersion = buffer.int
            val kdfVersion = buffer.int
            val cryptoVersion = buffer.int
            if (
                formatVersion != FORMAT_VERSION ||
                    kdfVersion != CryptoManager.KDF_VERSION ||
                    cryptoVersion != CryptoManager.CRYPTO_VERSION
            )
                return null
            fun bytes(min: Int, max: Int): ByteArray {
                val size = buffer.int
                require(size in min..max && size <= buffer.remaining())
                return ByteArray(size).also(buffer::get)
            }
            val salt = bytes(16, 16)
            val nonce = bytes(12, 12)
            val aad = bytes(1, 4096)
            val ciphertext = bytes(16, MAX_BACKUP_SIZE_BYTES.toInt())
            if (buffer.hasRemaining()) return null
            val header = deserializeAad(aad) ?: return null
            if (header != AadHeader(formatVersion, kdfVersion, cryptoVersion)) return null
            ParsedBackup(salt, nonce, aad, ciphertext, header)
        } catch (_: Exception) {
            null
        }
    }
}
