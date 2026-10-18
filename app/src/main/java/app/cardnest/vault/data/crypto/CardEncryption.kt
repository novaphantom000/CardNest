package app.cardnest.vault.data.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Encrypts and decrypts card data using AES-GCM with Android Keystore.
 *
 * Security properties:
 * - AES-256-GCM authenticated encryption
 * - Keys stored in hardware-backed keystore when available
 * - Unique IV for each encryption operation
 * - 128-bit authentication tag
 */
@Singleton
class CardEncryption @Inject constructor() {

    private val keyStore: KeyStore = KeyStore.getInstance(KEYSTORE_PROVIDER).apply {
        load(null)
    }

    init {
        // Generate key if it doesn't exist
        if (!keyStore.containsAlias(KEY_ALIAS)) {
            generateKey()
        }
    }

    /**
     * Encrypts plaintext card data.
     * @return Base64-encoded string: IV + ciphertext + tag
     */
    fun encrypt(plaintext: String): String {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.ENCRYPT_MODE, getKey())

        val iv = cipher.iv
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))

        // Format: IV (12 bytes) + ciphertext + tag (16 bytes)
        val combined = iv + ciphertext
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts encrypted card data.
     * @param encrypted Base64-encoded string from encrypt()
     * @return Original plaintext
     * @throws DecryptionException if decryption fails
     */
    fun decrypt(encrypted: String): String {
        try {
            val combined = Base64.decode(encrypted, Base64.NO_WRAP)

            // Extract IV and ciphertext
            val iv = combined.copyOfRange(0, IV_SIZE)
            val ciphertext = combined.copyOfRange(IV_SIZE, combined.size)

            val cipher = Cipher.getInstance(TRANSFORMATION)
            val spec = GCMParameterSpec(TAG_SIZE * 8, iv)
            cipher.init(Cipher.DECRYPT_MODE, getKey(), spec)

            val plaintext = cipher.doFinal(ciphertext)
            return String(plaintext, Charsets.UTF_8)
        } catch (e: Exception) {
            throw DecryptionException("Failed to decrypt data", e)
        }
    }

    private fun generateKey() {
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            KEYSTORE_PROVIDER
        )

        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .setUserAuthenticationRequired(false) // Vault-level auth instead
            .setRandomizedEncryptionRequired(true)
            .build()

        keyGenerator.init(spec)
        keyGenerator.generateKey()
    }

    private fun getKey(): SecretKey {
        return keyStore.getKey(KEY_ALIAS, null) as? SecretKey
            ?: throw IllegalStateException("Encryption key not found")
    }

    /**
     * Check if the encryption key is accessible.
     */
    fun isKeyValid(): Boolean {
        return try {
            getKey()
            true
        } catch (e: Exception) {
            false
        }
    }

    companion object {
        private const val KEYSTORE_PROVIDER = "AndroidKeyStore"
        private const val KEY_ALIAS = "cardnest_master_key"
        private const val TRANSFORMATION = "AES/GCM/NoPadding"
        private const val IV_SIZE = 12 // 96 bits recommended for GCM
        private const val TAG_SIZE = 16 // 128 bits
    }
}

class DecryptionException(message: String, cause: Throwable? = null) : Exception(message, cause)
