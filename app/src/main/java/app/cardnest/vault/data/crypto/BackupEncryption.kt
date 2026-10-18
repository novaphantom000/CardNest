package app.cardnest.vault.data.crypto

import android.util.Base64
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.PBEKeySpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

/**
 * Handles backup encryption using user-provided passphrase.
 * Uses PBKDF2 for key derivation and AES-GCM for encryption.
 */
class BackupEncryption @Inject constructor() {

    /**
     * Encrypts backup data with a passphrase.
     * @return Base64-encoded: salt + IV + ciphertext + tag
     */
    fun encryptBackup(plaintext: String, passphrase: String): String {
        val salt = ByteArray(SALT_SIZE)
        SecureRandom().nextBytes(salt)

        val key = deriveKey(passphrase, salt)

        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, key)

        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // Format: salt (32) + IV (12) + ciphertext + tag (16)
        val combined = salt + iv + ciphertext
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts backup data with a passphrase.
     * @throws BackupDecryptionException if passphrase is wrong or data corrupted
     */
    fun decryptBackup(encrypted: String, passphrase: String): String {
        try {
            val combined = Base64.decode(encrypted, Base64.NO_WRAP)

            if (combined.size < SALT_SIZE + IV_SIZE + TAG_SIZE) {
                throw BackupDecryptionException("Invalid backup format")
            }

            val salt = combined.copyOfRange(0, SALT_SIZE)
            val iv = combined.copyOfRange(SALT_SIZE, SALT_SIZE + IV_SIZE)
            val ciphertext = combined.copyOfRange(SALT_SIZE + IV_SIZE, combined.size)

            val key = deriveKey(passphrase, salt)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_SIZE * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, key, spec)

            val plaintext = cipher.doFinal(ciphertext)
            return String(plaintext, Charsets.UTF_8)
        } catch (e: javax.crypto.AEADBadTagException) {
            throw BackupDecryptionException("Incorrect passphrase or corrupted backup", e)
        } catch (e: Exception) {
            throw BackupDecryptionException("Failed to decrypt backup", e)
        }
    }

    private fun deriveKey(passphrase: String, salt: ByteArray): SecretKeySpec {
        val factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
        val spec = PBEKeySpec(passphrase.toCharArray(), salt, PBKDF2_ITERATIONS, KEY_SIZE_BITS)
        val tmp = factory.generateSecret(spec)
        return SecretKeySpec(tmp.encoded, "AES")
    }

    companion object {
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val SALT_SIZE = 32
        private const val IV_SIZE = 12
        private const val TAG_SIZE = 16
        private const val KEY_SIZE_BITS = 256
        private const val PBKDF2_ITERATIONS = 310_000 // OWASP 2023 recommendation
    }
}

class BackupDecryptionException(message: String, cause: Throwable? = null) : Exception(message, cause)
