package br.com.teadescomplica.data.crypto

import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec

/**
 * Gerenciador de criptografia para backup do TEA Descomplica.
 *
 * Implementa criptografia autenticada AES-256-GCM com derivação de chave via PBKDF2-HMAC-SHA256 a
 * partir de uma senha fornecida pelo usuário.
 *
 * Parâmetros:
 * - Chave AES: 256 bits
 * - Nonce (IV): 96 bits (12 bytes), único por operação
 * - Tag de autenticação GCM: 128 bits (16 bytes)
 * - Salt: 128 bits (16 bytes), aleatório por backup
 * - KDF: PBKDF2-HMAC-SHA256 com 600.000 iterações
 *
 * Nunca armazena senhas ou chaves em memória além do necessário. Nunca utiliza AES/ECB, chaves
 * fixas, nonces constantes ou SHA-1/MD5.
 */
object CryptoManager {

    /** Versão dos parâmetros do KDF. */
    const val KDF_VERSION = 1

    /** Versão do algoritmo de criptografia. */
    const val CRYPTO_VERSION = 1

    /** Tamanho da chave AES em bits. */
    private const val KEY_SIZE_BITS = 256

    /** Tamanho do nonce GCM em bits (96 bits = 12 bytes). */
    const val NONCE_SIZE_BITS = 96

    /** Tamanho da tag de autenticação GCM em bits (128 bits = 16 bytes). */
    private const val TAG_SIZE_BITS = 128

    /** Tamanho do salt em bytes (128 bits). */
    private const val SALT_SIZE_BYTES = 16

    /** Número de iterações do PBKDF2. */
    private const val PBKDF2_ITERATIONS = 600_000

    /** Algoritmo do KDF. */
    private const val PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"

    /** Algoritmo de cifragem. */
    private const val CIPHER_ALGORITHM = "AES/GCM/NoPadding"

    private val random = SecureRandom()

    /** Gera um salt aleatório criptograficamente seguro. */
    fun generateSalt(): ByteArray {
        val salt = ByteArray(SALT_SIZE_BYTES)
        random.nextBytes(salt)
        return salt
    }

    /** Gera um nonce (IV) aleatório criptograficamente seguro para GCM. */
    fun generateNonce(): ByteArray {
        val nonce = ByteArray(NONCE_SIZE_BITS / 8)
        random.nextBytes(nonce)
        return nonce
    }

    /**
     * Deriva uma chave AES-256 a partir de uma senha usando PBKDF2-HMAC-SHA256.
     *
     * @param password senha fornecida pelo usuário (não é armazenada).
     * @param salt salt aleatório exclusivo por backup.
     * @return chave secreta de 256 bits.
     */
    fun deriveKey(password: String, salt: ByteArray): SecretKey {
        val spec = PBEKeySpec(password.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_SIZE_BITS)
        val keyGenerator = javax.crypto.SecretKeyFactory.getInstance(PBKDF2_ALGORITHM)
        val keyBytes = keyGenerator.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Criptografa dados usando AES-256-GCM com autenticação.
     *
     * @param plaintext dados em texto puro a serem criptografados.
     * @param secretKey chave secreta derivada da senha.
     * @param nonce nonce de 96 bits único por operação.
     * @param aad dados adicionais autenticados (opcional, cabeçalho).
     * @return criptograma + tag de autenticação concatenados.
     */
    fun encrypt(
        plaintext: ByteArray,
        secretKey: SecretKey,
        nonce: ByteArray,
        aad: ByteArray? = null,
    ): ByteArray {
        val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
        val spec = GCMParameterSpec(TAG_SIZE_BITS, nonce)
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec)

        if (aad != null) {
            cipher.updateAAD(aad)
        }

        return cipher.doFinal(plaintext)
    }

    /**
     * Descriptografa dados usando AES-256-GCM e valida a autenticação.
     *
     * @param ciphertext criptograma + tag de autenticação.
     * @param secretKey chave secreta derivada da senha.
     * @param nonce nonce de 96 bits usado na criptografia.
     * @param aad dados adicionais autenticados (deve ser o mesmo usado na criptografia).
     * @return plaintext descriptografado, ou null se a autenticação falhar.
     */
    fun decrypt(
        ciphertext: ByteArray,
        secretKey: SecretKey,
        nonce: ByteArray,
        aad: ByteArray? = null,
    ): ByteArray? {
        return try {
            val cipher = Cipher.getInstance(CIPHER_ALGORITHM)
            val spec = GCMParameterSpec(TAG_SIZE_BITS, nonce)
            cipher.init(Cipher.DECRYPT_MODE, secretKey, spec)

            if (aad != null) {
                cipher.updateAAD(aad)
            }

            cipher.doFinal(ciphertext)
        } catch (e: java.security.GeneralSecurityException) {
            null
        }
    }

    /**
     * Gera uma chave simétrica aleatória (não derivada de senha) para uso em operações internas, se
     * necessário.
     */
    fun generateRandomKey(): SecretKey {
        val keyGenerator = KeyGenerator.getInstance("AES")
        keyGenerator.init(KEY_SIZE_BITS)
        return keyGenerator.generateKey()
    }

    /** Converte uma string em bytes UTF-8. */
    fun stringToBytes(text: String): ByteArray = text.toByteArray(Charsets.UTF_8)

    /** Converte bytes em string UTF-8. */
    fun bytesToString(bytes: ByteArray): String = String(bytes, Charsets.UTF_8)

    /**
     * Converte bytes para representação hexadecimal (apenas para logs de depuração, nunca para
     * dados sensíveis como senhas ou chaves).
     */
    fun bytesToHex(bytes: ByteArray): String = bytes.joinToString("") { "%02x".format(it) }

    @JvmStatic fun instance() = CryptoManager
}
