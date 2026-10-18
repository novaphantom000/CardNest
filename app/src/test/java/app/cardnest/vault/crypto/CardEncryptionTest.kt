package app.cardnest.vault.crypto

import app.cardnest.vault.data.crypto.CardEncryption
import app.cardnest.vault.data.crypto.DecryptionException
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [28])
class CardEncryptionTest {

    private lateinit var encryption: CardEncryption

    @Before
    fun setup() {
        encryption = CardEncryption()
    }

    @Test
    fun `encrypt and decrypt returns original value`() {
        val plaintext = "4532015112830366"
        val encrypted = encryption.encrypt(plaintext)
        val decrypted = encryption.decrypt(encrypted)

        assertThat(decrypted).isEqualTo(plaintext)
    }

    @Test
    fun `encryption produces different ciphertext for same plaintext`() {
        val plaintext = "4532015112830366"
        val encrypted1 = encryption.encrypt(plaintext)
        val encrypted2 = encryption.encrypt(plaintext)

        // Due to random IV, ciphertexts should differ
        assertThat(encrypted1).isNotEqualTo(encrypted2)

        // But both should decrypt to same plaintext
        assertThat(encryption.decrypt(encrypted1)).isEqualTo(plaintext)
        assertThat(encryption.decrypt(encrypted2)).isEqualTo(plaintext)
    }

    @Test(expected = DecryptionException::class)
    fun `decryption fails with tampered ciphertext`() {
        val plaintext = "4532015112830366"
        val encrypted = encryption.encrypt(plaintext)

        // Tamper with the ciphertext
        val tampered = encrypted.dropLast(5) + "AAAAA"

        encryption.decrypt(tampered)
    }

    @Test
    fun `encrypt handles empty string`() {
        val encrypted = encryption.encrypt("")
        val decrypted = encryption.decrypt(encrypted)

        assertThat(decrypted).isEmpty()
    }

    @Test
    fun `encrypt handles special characters`() {
        val plaintext = "Test123!@#$%^&*()"
        val encrypted = encryption.encrypt(plaintext)
        val decrypted = encryption.decrypt(encrypted)

        assertThat(decrypted).isEqualTo(plaintext)
    }

    @Test
    fun `key is valid after initialization`() {
        assertThat(encryption.isKeyValid()).isTrue()
    }
}
