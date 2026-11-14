package br.com.teadescomplica.data.crypto

import java.nio.ByteBuffer
import org.junit.Assert.*
import org.junit.Test

class BackupFormatTest {
    private fun file(): ByteArray =
        BackupFormat.buildBackupFile(
            CryptoManager.generateSalt(),
            CryptoManager.generateNonce(),
            BackupFormat.serializeAad(1, 1, 1),
            ByteArray(32),
        )

    @Test
    fun ownFileRoundTrip() {
        val f = file()
        val parsed = BackupFormat.parseBackupFile(f)
        assertNotNull(parsed)
        assertEquals(16, parsed!!.salt.size)
        assertEquals(12, parsed.nonce.size)
        assertArrayEquals(ByteArray(32), parsed.ciphertext)
    }

    @Test
    fun everyTruncatedPrefixIsRejected() {
        val f = file()
        for (n in 0 until f.size) assertNull("size=$n", BackupFormat.parseBackupFile(f.copyOf(n)))
    }

    @Test
    fun wrongMagicAndVersionsRejected() {
        val f = file()
        f[0] = 0
        assertNull(BackupFormat.parseBackupFile(f))
        val v = file()
        ByteBuffer.wrap(v).putInt(4, 99)
        assertNull(BackupFormat.parseBackupFile(v))
    }

    @Test
    fun negativeOrExcessiveLengthRejected() {
        for (n in listOf(-1, 0, Int.MAX_VALUE)) {
            val f = file()
            ByteBuffer.wrap(f).putInt(16, n)
            assertNull(BackupFormat.parseBackupFile(f))
        }
    }

    @Test
    fun mismatchedHeaderAndTrailingBytesRejected() {
        val f =
            BackupFormat.buildBackupFile(
                ByteArray(16),
                ByteArray(12),
                BackupFormat.serializeAad(99, 1, 1),
                ByteArray(32),
            )
        assertNull(BackupFormat.parseBackupFile(f))
        assertNull(BackupFormat.parseBackupFile(file() + byteArrayOf(1)))
    }

    @Test
    fun nameAndAad() {
        assertTrue(BackupFormat.suggestedFileName().endsWith(".teadbackup"))
        assertEquals(
            BackupFormat.AadHeader(1, 1, 1),
            BackupFormat.deserializeAad(BackupFormat.serializeAad(1, 1, 1)),
        )
    }
}
