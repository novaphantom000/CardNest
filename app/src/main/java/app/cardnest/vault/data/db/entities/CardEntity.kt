package app.cardnest.vault.data.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import app.cardnest.vault.domain.model.Card
import app.cardnest.vault.domain.model.CardNetwork
import app.cardnest.vault.domain.model.CardType

@Entity(tableName = "cards")
data class CardEntity(
    @PrimaryKey
    val id: String,
    val nickname: String,
    val cardholderName: String,
    val issuer: String,
    val network: String,
    val type: String,
    val encryptedNumber: String, // Base64 encoded encrypted bytes
    val expiryMonth: Int,
    val expiryYear: Int,
    val billingName: String?,
    val billingAddress: String?,
    val customerServicePhone: String?,
    val notes: String?,
    val isFavorite: Boolean,
    val isArchived: Boolean,
    val tags: String, // JSON array serialized
    val createdAt: Long,
    val lastAccessedAt: Long,
    val modifiedAt: Long
)

fun CardEntity.toDomain(): Card = Card(
    id = id,
    nickname = nickname,
    cardholderName = cardholderName,
    issuer = issuer,
    network = CardNetwork.valueOf(network),
    type = CardType.valueOf(type),
    encryptedNumber = encryptedNumber,
    expiryMonth = expiryMonth,
    expiryYear = expiryYear,
    billingName = billingName,
    billingAddress = billingAddress,
    customerServicePhone = customerServicePhone,
    notes = notes,
    isFavorite = isFavorite,
    isArchived = isArchived,
    tags = if (tags.isBlank()) emptyList() else kotlinx.serialization.json.Json.decodeFromString(tags),
    createdAt = createdAt,
    lastAccessedAt = lastAccessedAt,
    modifiedAt = modifiedAt
)

fun Card.toEntity(): CardEntity = CardEntity(
    id = id,
    nickname = nickname,
    cardholderName = cardholderName,
    issuer = issuer,
    network = network.name,
    type = type.name,
    encryptedNumber = encryptedNumber,
    expiryMonth = expiryMonth,
    expiryYear = expiryYear,
    billingName = billingName,
    billingAddress = billingAddress,
    customerServicePhone = customerServicePhone,
    notes = notes,
    isFavorite = isFavorite,
    isArchived = isArchived,
    tags = if (tags.isEmpty()) "" else kotlinx.serialization.json.Json.encodeToString(kotlinx.serialization.serializer(), tags),
    createdAt = createdAt,
    lastAccessedAt = lastAccessedAt,
    modifiedAt = modifiedAt
)
