package app.cardnest.vault.backup

import app.cardnest.vault.data.backup.BackupManager
import app.cardnest.vault.data.crypto.BackupDecryptionException
import app.cardnest.vault.data.crypto.BackupEncryption
import app.cardnest.vault.data.repository.CardRepository
import app.cardnest.vault.domain.model.Card
import app.cardnest.vault.domain.model.CardNetwork
import app.cardnest.vault.domain.model.CardType
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class BackupManagerTest {

    private lateinit var backupManager: BackupManager
    private lateinit var cardRepository: CardRepository
    private lateinit var backupEncryption: BackupEncryption

    private val testCard = Card(
        id = "test-id",
        nickname = "Test Card",
        cardholderName = "John Doe",
        issuer = "Test Bank",
        network = CardNetwork.VISA,
        type = CardType.CREDIT,
        encryptedNumber = "encrypted123",
        expiryMonth = 12,
        expiryYear = 2027,
        createdAt = System.currentTimeMillis(),
        lastAccessedAt = System.currentTimeMillis(),
        modifiedAt = System.currentTimeMillis()
    )

    @Before
    fun setup() {
        cardRepository = mockk()
        backupEncryption = BackupEncryption()
        backupManager = BackupManager(cardRepository, backupEncryption)
    }

    @Test
    fun `createBackup returns encrypted string`() = runTest {
        coEvery { cardRepository.getAllCardsForBackup() } returns listOf(testCard)

        val passphrase = "strong-passphrase-123"
        val backup = backupManager.createBackup(passphrase)

        assertThat(backup).isNotEmpty()
        coVerify { cardRepository.getAllCardsForBackup() }
    }

    @Test
    fun `restoreBackup decrypts and restores cards`() = runTest {
        coEvery { cardRepository.getAllCardsForBackup() } returns listOf(testCard)
        coEvery { cardRepository.restoreCards(any()) } returns Unit

        val passphrase = "strong-passphrase-123"
        val backup = backupManager.createBackup(passphrase)

        backupManager.restoreBackup(backup, passphrase)

        coVerify { cardRepository.restoreCards(any()) }
    }

    @Test(expected = BackupDecryptionException::class)
    fun `restoreBackup fails with wrong passphrase`() = runTest {
        coEvery { cardRepository.getAllCardsForBackup() } returns listOf(testCard)

        val backup = backupManager.createBackup("correct-passphrase")
        backupManager.restoreBackup(backup, "wrong-passphrase")
    }

    @Test
    fun `validateBackup returns correct info`() = runTest {
        coEvery { cardRepository.getAllCardsForBackup() } returns listOf(testCard, testCard)

        val passphrase = "test-passphrase"
        val backup = backupManager.createBackup(passphrase)
        val info = backupManager.validateBackup(backup, passphrase)

        assertThat(info.cardCount).isEqualTo(2)
        assertThat(info.version).isEqualTo(1)
    }
}
