package com.lazor.growthspace.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lazor.growthspace.ui.auth.ForgotPasswordScreen
import com.lazor.growthspace.ui.auth.LoginScreen
import com.lazor.growthspace.ui.auth.RegisterScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.REGISTER // Починаємо з екрана входу
    ) {
        // Екран Логіну
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // TODO: Сюди ми додамо перехід на головну пізніше
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.REGISTER)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(Routes.FORGOT_PASSWORD)
                }
            )
        }

        // Екран Реєстрації
        composable(Routes.REGISTER) {
            RegisterScreen(
                onRegisterSuccess = {
                    // TODO: Логіка після реєстрації
                },


                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack() // Повернення на логін
                },
                onSendLinkClick = { email ->
                    // Тут буде логіка Firebase, поки просто лог
                    println("Надіслати посилання на: $email")
                }
            )
        }
    }
}