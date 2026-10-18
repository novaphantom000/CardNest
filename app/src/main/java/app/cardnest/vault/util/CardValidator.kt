package app.cardnest.vault.util

/**
 * Validates card numbers using Luhn algorithm.
 */
object CardValidator {

    fun isValidCardNumber(number: String): Boolean {
        val cleaned = number.filter { it.isDigit() }

        if (cleaned.length !in 13..19) {
            return false
        }

        return luhnCheck(cleaned)
    }

    private fun luhnCheck(number: String): Boolean {
        var sum = 0
        var alternate = false

        for (i in number.length - 1 downTo 0) {
            var digit = number[i].digitToInt()

            if (alternate) {
                digit *= 2
                if (digit > 9) {
                    digit -= 9
                }
            }

            sum += digit
            alternate = !alternate
        }

        return sum % 10 == 0
    }

    fun formatCardNumber(number: String): String {
        val cleaned = number.filter { it.isDigit() }
        return cleaned.chunked(4).joinToString(" ")
    }

    fun maskCardNumber(number: String): String {
        val cleaned = number.filter { it.isDigit() }
        if (cleaned.length < 4) return "****"

        val lastFour = cleaned.takeLast(4)
        val masked = "•".repeat(cleaned.length - 4)
        return formatCardNumber(masked + lastFour)
    }

    fun getLastFourDigits(number: String): String {
        val cleaned = number.filter { it.isDigit() }
        return cleaned.takeLast(4)
    }

    fun isValidExpiry(month: Int, year: Int): Boolean {
        if (month !in 1..12) return false
        if (year < 2000) return false

        val now = java.util.Calendar.getInstance()
        val currentYear = now.get(java.util.Calendar.YEAR)
        val currentMonth = now.get(java.util.Calendar.MONTH) + 1

        return year > currentYear || (year == currentYear && month >= currentMonth)
    }

    fun isValidCVV(cvv: String, isAmex: Boolean = false): Boolean {
        val expectedLength = if (isAmex) 4 else 3
        return cvv.length == expectedLength && cvv.all { it.isDigit() }
    }
}
