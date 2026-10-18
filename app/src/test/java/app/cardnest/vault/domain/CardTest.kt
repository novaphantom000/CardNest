package app.cardnest.vault.domain

import app.cardnest.vault.domain.model.Card
import app.cardnest.vault.domain.model.CardNetwork
import app.cardnest.vault.domain.model.CardType
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CardTest {

    @Test
    fun `isExpired returns true for expired card`() {
        val card = Card(
            id = "test",
            nickname = "Test",
            cardholderName = "Test",
            issuer = "Test",
            network = CardNetwork.VISA,
            type = CardType.CREDIT,
            encryptedNumber = "encrypted",
            expiryMonth = 1,
            expiryYear = 2020,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )

        assertThat(card.isExpired()).isTrue()
    }

    @Test
    fun `isExpired returns false for valid card`() {
        val card = Card(
            id = "test",
            nickname = "Test",
            cardholderName = "Test",
            issuer = "Test",
            network = CardNetwork.VISA,
            type = CardType.CREDIT,
            encryptedNumber = "encrypted",
            expiryMonth = 12,
            expiryYear = 2030,
            createdAt = System.currentTimeMillis(),
            lastAccessedAt = System.currentTimeMillis(),
            modifiedAt = System.currentTimeMillis()
        )

        assertThat(card.isExpired()).isFalse()
    }

    @Test
    fun `CardNetwork fromNumber detects Visa`() {
        assertThat(CardNetwork.fromNumber("4532015112830366")).isEqualTo(CardNetwork.VISA)
    }

    @Test
    fun `CardNetwork fromNumber detects Mastercard`() {
        assertThat(CardNetwork.fromNumber("5425233430109903")).isEqualTo(CardNetwork.MASTERCARD)
    }

    @Test
    fun `CardNetwork fromNumber detects Amex`() {
        assertThat(CardNetwork.fromNumber("374245455400126")).isEqualTo(CardNetwork.AMEX)
    }

    @Test
    fun `CardNetwork fromNumber returns OTHER for unknown`() {
        assertThat(CardNetwork.fromNumber("9999999999999999")).isEqualTo(CardNetwork.OTHER)
    }
}
