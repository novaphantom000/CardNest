package app.cardnest.vault.data.backup

import app.cardnest.vault.data.crypto.BackupDecryptionException
import app.cardnest.vault.data.crypto.BackupEncryption
import app.cardnest.vault.data.repository.CardRepository
import app.cardnest.vault.domain.model.Card
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class BackupFormat(
    val version: Int,
    val timestamp: Long,
    val cards: List<Card>
)

@Singleton
class BackupManager @Inject constructor(
    private val cardRepository: CardRepository,
    private val backupEncryption: BackupEncryption
) {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * Creates an encrypted backup of all cards.
     * @return Encrypted backup string (Base64)
     */
    suspend fun createBackup(passphrase: String): String {
        val cards = cardRepository.getAllCardsForBackup()

        val backup = BackupFormat(
            version = CURRENT_VERSION,
            timestamp = System.currentTimeMillis(),
            cards = cards
        )

        val plainJson = json.encodeToString(backup)
        return backupEncryption.encryptBackup(plainJson, passphrase)
    }

    /**
     * Restores cards from an encrypted backup.
     * @throws BackupDecryptionException if passphrase is wrong
     * @throws BackupFormatException if backup format is invalid
     */
    suspend fun restoreBackup(encryptedBackup: String, passphrase: String) {
        val plainJson = try {
            backupEncryption.decryptBackup(encryptedBackup, passphrase)
        } catch (e: BackupDecryptionException) {
            throw e
        }

        val backup = try {
            json.decodeFromString<BackupFormat>(plainJson)
        } catch (e: Exception) {
            throw BackupFormatException("Invalid backup format", e)
        }

        if (backup.version > CURRENT_VERSION) {
            throw BackupFormatException("Backup version ${backup.version} is not supported")
        }

        cardRepository.restoreCards(backup.cards)
    }

    /**
     * Validates backup format without restoring.
     */
    suspend fun validateBackup(encryptedBackup: String, passphrase: String): BackupInfo {
        val plainJson = backupEncryption.decryptBackup(encryptedBackup, passphrase)

        val backup = try {
            json.decodeFromString<BackupFormat>(plainJson)
        } catch (e: Exception) {
            throw BackupFormatException("Invalid backup format", e)
        }

        return BackupInfo(
            version = backup.version,
            timestamp = backup.timestamp,
            cardCount = backup.cards.size
        )
    }

    companion object {
        private const val CURRENT_VERSION = 1
    }
}

data class BackupInfo(
    val version: Int,
    val timestamp: Long,
    val cardCount: Int
)

class BackupFormatException(message: String, cause: Throwable? = null) : Exception(message, cause)
