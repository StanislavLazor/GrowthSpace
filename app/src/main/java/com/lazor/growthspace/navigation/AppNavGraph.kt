package com.lazor.growthspace.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.lazor.growthspace.ui.auth.ForgotPasswordScreen
import com.lazor.growthspace.ui.auth.LoginScreen
import com.lazor.growthspace.ui.auth.RegisterScreen
import com.lazor.growthspace.ui.main.MainScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.REGISTER
    ) {
        // Екран Логіну
        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = {
                    // Перехід у додаток та очищення історії навігації (щоб не повернутися на логін)
                    navController.navigate(Routes.MAIN_APP) {
                        popUpTo(0) { inclusive = true }
                    }
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
                    // Перехід у додаток та очищення історії
                    navController.navigate(Routes.MAIN_APP) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onLoginClick = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.REGISTER) { inclusive = true }
                    }
                },
                onNavigateToLegal = { type ->
                    navController.navigate("legal/$type")
                }
            )
        }

        // Екран відновлення пароля
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                onSendLinkClick = { email ->
                    // Тимчасова заглушка для кнопки
                    println("Відправлено на $email")
                }
            )
        }

        // Головний екран додатку
        composable(Routes.MAIN_APP) {
            MainScreen(
                onLogout = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToLegal = { type ->
                    navController.navigate("legal/$type")
                }
            )
        }

        // Екран для юридичних текстів (Умови та Політика)
        composable("legal/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "terms"

            val title = if (type == "terms") "Умови користування" else "Політика компанії"
            val text = if (type == "terms") com.lazor.growthspace.ui.LegalData.termsText else com.lazor.growthspace.ui.LegalData.policyText

            com.lazor.growthspace.ui.legal.LegalScreen(
                title = title,
                content = text,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}