package app.cardnest.vault.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import app.cardnest.vault.ui.screens.auth.AuthScreen
import app.cardnest.vault.ui.screens.auth.AuthViewModel
import app.cardnest.vault.ui.screens.home.HomeScreen

@Composable
fun CardNestApp(
    navController: NavHostController = rememberNavController()
) {
    val authViewModel: AuthViewModel = hiltViewModel()
    val isUnlocked by authViewModel.isUnlocked.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = if (isUnlocked) "home" else "auth",
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(300)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(300)
                )
            }
        ) {
            composable("auth") {
                AuthScreen(
                    onUnlocked = {
                        navController.navigate("home") {
                            popUpTo("auth") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(navController = navController)
            }

            composable("vault") {
                // VaultScreen placeholder
            }

            composable("card_detail/{cardId}") {
                // CardDetailScreen placeholder
            }

            composable("settings") {
                // SettingsScreen placeholder
            }

            composable("backup") {
                // BackupScreen placeholder
            }

            composable("privacy") {
                // PrivacyScreen placeholder
            }
        }
    }
}
