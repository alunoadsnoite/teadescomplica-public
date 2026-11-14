package br.com.teadescomplica.data.crypto

import org.junit.Assert.*
import org.junit.Test

class CryptoManagerTest {
    @Test
    fun randomValuesHaveExpectedSizesAndDiffer() {
        assertEquals(16, CryptoManager.generateSalt().size)
        assertEquals(12, CryptoManager.generateNonce().size)
        assertFalse(CryptoManager.generateNonce().contentEquals(CryptoManager.generateNonce()))
    }

    @Test
    fun passwordRoundTripAndWrongPassword() {
        val salt = CryptoManager.generateSalt()
        val nonce = CryptoManager.generateNonce()
        val key = CryptoManager.deriveKey("senhaCorreta123", salt)
        val plain = "Dados sensíveis ç 😀".toByteArray()
        val cipher = CryptoManager.encrypt(plain, key, nonce)
        assertArrayEquals(plain, CryptoManager.decrypt(cipher, key, nonce))
        assertNull(
            CryptoManager.decrypt(cipher, CryptoManager.deriveKey("senhaErrada123", salt), nonce)
        )
    }

    @Test
    fun tamperingCiphertextAadAndTruncationAreRejected() {
        val key = CryptoManager.generateRandomKey()
        val nonce = CryptoManager.generateNonce()
        val aad = "header".toByteArray()
        val cipher = CryptoManager.encrypt("Texto".toByteArray(), key, nonce, aad)
        assertNull(CryptoManager.decrypt(cipher.copyOf(cipher.size - 1), key, nonce, aad))
        val tampered = cipher.copyOf()
        tampered[0] = (tampered[0].toInt() xor 1).toByte()
        assertNull(CryptoManager.decrypt(tampered, key, nonce, aad))
        assertNull(CryptoManager.decrypt(cipher, key, nonce, "other".toByteArray()))
    }

    @Test
    fun differentSaltsProduceDifferentKeys() {
        assertFalse(
            CryptoManager.deriveKey("mesmasenha", CryptoManager.generateSalt())
                .encoded
                .contentEquals(
                    CryptoManager.deriveKey("mesmasenha", CryptoManager.generateSalt()).encoded
                )
        )
    }
}
