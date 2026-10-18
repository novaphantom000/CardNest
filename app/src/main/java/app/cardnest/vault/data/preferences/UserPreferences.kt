package app.cardnest.vault.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val dataStore = context.dataStore

    // Theme
    val theme: Flow<AppTheme> = dataStore.data.map { prefs ->
        AppTheme.valueOf(prefs[THEME] ?: AppTheme.SYSTEM.name)
    }

    suspend fun setTheme(theme: AppTheme) {
        dataStore.edit { it[THEME] = theme.name }
    }

    // Dynamic colors
    val useDynamicColors: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DYNAMIC_COLORS] ?: true
    }

    suspend fun setDynamicColors(enabled: Boolean) {
        dataStore.edit { it[DYNAMIC_COLORS] = enabled }
    }

    // Biometric
    val requireBiometric: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[REQUIRE_BIOMETRIC] ?: true
    }

    suspend fun setRequireBiometric(required: Boolean) {
        dataStore.edit { it[REQUIRE_BIOMETRIC] = required }
    }

    // Auto-lock timeout (minutes)
    val autoLockTimeout: Flow<Int> = dataStore.data.map { prefs ->
        prefs[AUTO_LOCK_TIMEOUT] ?: 5
    }

    suspend fun setAutoLockTimeout(minutes: Int) {
        dataStore.edit { it[AUTO_LOCK_TIMEOUT] = minutes }
    }

    // Clipboard timeout (seconds)
    val clipboardTimeout: Flow<Int> = dataStore.data.map { prefs ->
        prefs[CLIPBOARD_TIMEOUT] ?: 30
    }

    suspend fun setClipboardTimeout(seconds: Int) {
        dataStore.edit { it[CLIPBOARD_TIMEOUT] = seconds }
    }

    // Expiry reminders
    val expiryRemindersEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[EXPIRY_REMINDERS] ?: true
    }

    suspend fun setExpiryRemindersEnabled(enabled: Boolean) {
        dataStore.edit { it[EXPIRY_REMINDERS] = enabled }
    }

    val reminderDaysBefore: Flow<Int> = dataStore.data.map { prefs ->
        prefs[REMINDER_DAYS] ?: 30
    }

    suspend fun setReminderDaysBefore(days: Int) {
        dataStore.edit { it[REMINDER_DAYS] = days }
    }

    // Reduced motion
    val reducedMotion: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[REDUCED_MOTION] ?: false
    }

    suspend fun setReducedMotion(enabled: Boolean) {
        dataStore.edit { it[REDUCED_MOTION] = enabled }
    }

    // First launch
    val isFirstLaunch: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[FIRST_LAUNCH] ?: true
    }

    suspend fun setFirstLaunchComplete() {
        dataStore.edit { it[FIRST_LAUNCH] = false }
    }

    companion object {
        private val THEME = stringPreferencesKey("theme")
        private val DYNAMIC_COLORS = booleanPreferencesKey("dynamic_colors")
        private val REQUIRE_BIOMETRIC = booleanPreferencesKey("require_biometric")
        private val AUTO_LOCK_TIMEOUT = intPreferencesKey("auto_lock_timeout")
        private val CLIPBOARD_TIMEOUT = intPreferencesKey("clipboard_timeout")
        private val EXPIRY_REMINDERS = booleanPreferencesKey("expiry_reminders")
        private val REMINDER_DAYS = intPreferencesKey("reminder_days")
        private val REDUCED_MOTION = booleanPreferencesKey("reduced_motion")
        private val FIRST_LAUNCH = booleanPreferencesKey("first_launch")
    }
}

enum class AppTheme {
    LIGHT,
    DARK,
    SYSTEM
}
