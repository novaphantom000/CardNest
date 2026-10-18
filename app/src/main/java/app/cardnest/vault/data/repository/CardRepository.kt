package app.cardnest.vault.data.repository

import app.cardnest.vault.data.crypto.CardEncryption
import app.cardnest.vault.data.db.dao.CardDao
import app.cardnest.vault.data.db.entities.toDomain
import app.cardnest.vault.data.db.entities.toEntity
import app.cardnest.vault.domain.model.Card
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CardRepository @Inject constructor(
    private val cardDao: CardDao,
    private val encryption: CardEncryption
) {

    fun getAllCards(): Flow<List<Card>> {
        return cardDao.getAllCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getArchivedCards(): Flow<List<Card>> {
        return cardDao.getArchivedCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    suspend fun getCardById(id: String): Card? {
        return cardDao.getCardById(id)?.toDomain()
    }

    fun searchCards(query: String): Flow<List<Card>> {
        return cardDao.searchCards(query).map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getFavoriteCards(): Flow<List<Card>> {
        return cardDao.getFavoriteCards().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    fun getCardCount(): Flow<Int> {
        return cardDao.getCardCount()
    }

    suspend fun insertCard(card: Card) {
        cardDao.insertCard(card.toEntity())
    }

    suspend fun updateCard(card: Card) {
        cardDao.updateCard(card.toEntity())
    }

    suspend fun deleteCard(id: String) {
        cardDao.deleteCard(id)
    }

    suspend fun updateLastAccessed(id: String) {
        cardDao.updateLastAccessed(id, System.currentTimeMillis())
    }

    /**
     * Encrypts card number and creates a new card.
     */
    suspend fun createCard(
        nickname: String,
        cardholderName: String,
        issuer: String,
        network: app.cardnest.vault.domain.model.CardNetwork,
        type: app.cardnest.vault.domain.model.CardType,
        cardNumber: String,
        expiryMonth: Int,
        expiryYear: Int,
        billingName: String? = null,
        billingAddress: String? = null,
        customerServicePhone: String? = null,
        notes: String? = null,
        tags: List<String> = emptyList()
    ): Card {
        val encryptedNumber = encryption.encrypt(cardNumber)
        val now = System.currentTimeMillis()

        val card = Card(
            id = UUID.randomUUID().toString(),
            nickname = nickname,
            cardholderName = cardholderName,
            issuer = issuer,
            network = network,
            type = type,
            encryptedNumber = encryptedNumber,
            expiryMonth = expiryMonth,
            expiryYear = expiryYear,
            billingName = billingName,
            billingAddress = billingAddress,
            customerServicePhone = customerServicePhone,
            notes = notes,
            isFavorite = false,
            isArchived = false,
            tags = tags,
            createdAt = now,
            lastAccessedAt = now,
            modifiedAt = now
        )

        insertCard(card)
        return card
    }

    /**
     * Decrypts card number for display/copy.
     */
    fun decryptCardNumber(encryptedNumber: String): String {
        return encryption.decrypt(encryptedNumber)
    }

    /**
     * Check for duplicate card numbers.
     */
    suspend fun isDuplicateCard(cardNumber: String, excludeId: String? = null): Boolean {
        val allCards = cardDao.getAllCardsOneShot()
        return allCards.any { entity ->
            entity.id != excludeId && try {
                encryption.decrypt(entity.encryptedNumber) == cardNumber
            } catch (e: Exception) {
                false
            }
        }
    }

    suspend fun getAllCardsForBackup(): List<Card> {
        return cardDao.getAllCardsOneShot().map { it.toDomain() }
    }

    suspend fun restoreCards(cards: List<Card>) {
        cardDao.deleteAllCards()
        cards.forEach { card ->
            cardDao.insertCard(card.toEntity())
        }
    }
}
