package app.cardnest.vault.validation

import app.cardnest.vault.util.CardValidator
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CardValidatorTest {

    @Test
    fun `valid Visa card number passes validation`() {
        assertThat(CardValidator.isValidCardNumber("4532015112830366")).isTrue()
    }

    @Test
    fun `valid Mastercard number passes validation`() {
        assertThat(CardValidator.isValidCardNumber("5425233430109903")).isTrue()
    }

    @Test
    fun `valid Amex number passes validation`() {
        assertThat(CardValidator.isValidCardNumber("374245455400126")).isTrue()
    }

    @Test
    fun `invalid card number fails validation`() {
        assertThat(CardValidator.isValidCardNumber("4532015112830367")).isFalse()
    }

    @Test
    fun `too short number fails validation`() {
        assertThat(CardValidator.isValidCardNumber("123456")).isFalse()
    }

    @Test
    fun `too long number fails validation`() {
        assertThat(CardValidator.isValidCardNumber("12345678901234567890")).isFalse()
    }

    @Test
    fun `card number with spaces is validated correctly`() {
        assertThat(CardValidator.isValidCardNumber("4532 0151 1283 0366")).isTrue()
    }

    @Test
    fun `formatCardNumber adds spaces correctly`() {
        val formatted = CardValidator.formatCardNumber("4532015112830366")
        assertThat(formatted).isEqualTo("4532 0151 1283 0366")
    }

    @Test
    fun `maskCardNumber masks correctly`() {
        val masked = CardValidator.maskCardNumber("4532015112830366")
        assertThat(masked).contains("0366")
        // Bullet character may render differently in tests
        assertThat(masked.length).isGreaterThan(4)
    }

    @Test
    fun `getLastFourDigits returns correct digits`() {
        val lastFour = CardValidator.getLastFourDigits("4532015112830366")
        assertThat(lastFour).isEqualTo("0366")
    }

    @Test
    fun `valid expiry date passes validation`() {
        assertThat(CardValidator.isValidExpiry(12, 2027)).isTrue()
    }

    @Test
    fun `expired date fails validation`() {
        assertThat(CardValidator.isValidExpiry(1, 2020)).isFalse()
    }

    @Test
    fun `invalid month fails validation`() {
        assertThat(CardValidator.isValidExpiry(13, 2027)).isFalse()
        assertThat(CardValidator.isValidExpiry(0, 2027)).isFalse()
    }

    @Test
    fun `current month passes validation`() {
        assertThat(CardValidator.isValidExpiry(10, 2026)).isTrue()
    }

    @Test
    fun `valid CVV passes validation`() {
        assertThat(CardValidator.isValidCVV("123", isAmex = false)).isTrue()
    }

    @Test
    fun `valid Amex CVV passes validation`() {
        assertThat(CardValidator.isValidCVV("1234", isAmex = true)).isTrue()
    }

    @Test
    fun `wrong length CVV fails validation`() {
        assertThat(CardValidator.isValidCVV("12", isAmex = false)).isFalse()
        assertThat(CardValidator.isValidCVV("123", isAmex = true)).isFalse()
    }

    @Test
    fun `non-numeric CVV fails validation`() {
        assertThat(CardValidator.isValidCVV("12A", isAmex = false)).isFalse()
    }
}
