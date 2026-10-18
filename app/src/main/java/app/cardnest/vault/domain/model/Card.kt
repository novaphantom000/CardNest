package app.cardnest.vault.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Card(
    val id: String,
    val nickname: String,
    val cardholderName: String,
    val issuer: String,
    val network: CardNetwork,
    val type: CardType,
    val encryptedNumber: String, // AES-GCM encrypted
    val expiryMonth: Int,
    val expiryYear: Int,
    val billingName: String? = null,
    val billingAddress: String? = null,
    val customerServicePhone: String? = null,
    val notes: String? = null,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val tags: List<String> = emptyList(),
    val createdAt: Long,
    val lastAccessedAt: Long,
    val modifiedAt: Long
) {
    val lastFourDigits: String?
        get() = null // Calculated after decryption when needed

    fun isExpiringSoon(daysThreshold: Int = 30): Boolean {
        val now = java.util.Calendar.getInstance()
        val expiryDate = java.util.Calendar.getInstance().apply {
            set(java.util.Calendar.YEAR, expiryYear)
            set(java.util.Calendar.MONTH, expiryMonth - 1)
            set(java.util.Calendar.DAY_OF_MONTH, getActualMaximum(java.util.Calendar.DAY_OF_MONTH))
        }
        val diffInMillis = expiryDate.timeInMillis - now.timeInMillis
        val diffInDays = diffInMillis / (1000 * 60 * 60 * 24)
        return diffInDays in 1..daysThreshold
    }

    fun isExpired(): Boolean {
        val now = java.util.Calendar.getInstance()
        val currentYear = now.get(java.util.Calendar.YEAR)
        val currentMonth = now.get(java.util.Calendar.MONTH) + 1
        return expiryYear < currentYear || (expiryYear == currentYear && expiryMonth < currentMonth)
    }
}

enum class CardNetwork {
    VISA,
    MASTERCARD,
    AMEX,
    DISCOVER,
    DINERS,
    JCB,
    UNIONPAY,
    OTHER;

    companion object {
        fun fromNumber(number: String): CardNetwork {
            val cleaned = number.filter { it.isDigit() }
            return when {
                cleaned.startsWith("4") -> VISA
                cleaned.matches(Regex("^5[1-5].*")) || cleaned.matches(Regex("^2[2-7].*")) -> MASTERCARD
                cleaned.matches(Regex("^3[47].*")) -> AMEX
                cleaned.matches(Regex("^6(?:011|5).*")) -> DISCOVER
                cleaned.matches(Regex("^3(?:0[0-5]|[68]).*")) -> DINERS
                cleaned.matches(Regex("^35.*")) -> JCB
                cleaned.matches(Regex("^62.*")) -> UNIONPAY
                else -> OTHER
            }
        }
    }
}

enum class CardType {
    CREDIT,
    DEBIT,
    PREPAID,
    CHARGE,
    OTHER
}
