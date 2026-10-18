package app.cardnest.vault

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import app.cardnest.vault.data.preferences.AppTheme
import app.cardnest.vault.data.preferences.UserPreferences
import app.cardnest.vault.ui.CardNestApp
import app.cardnest.vault.ui.theme.CardNestTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var userPreferences: UserPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val theme by userPreferences.theme.collectAsState(initial = AppTheme.SYSTEM)
            val useDynamicColors by userPreferences.useDynamicColors.collectAsState(initial = true)

            val darkTheme = when (theme) {
                AppTheme.LIGHT -> false
                AppTheme.DARK -> true
                AppTheme.SYSTEM -> isSystemInDarkTheme()
            }

            CardNestTheme(
                darkTheme = darkTheme,
                dynamicColor = useDynamicColors
            ) {
                CardNestApp()
            }
        }
    }
}
