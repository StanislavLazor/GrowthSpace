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
        startDestination = Routes.LOGIN // Починаємо з логіну
    ) {
        // Екран Логіну - тепер приймає тільки navController
        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        // Екран Реєстрації - тепер приймає тільки navController
        composable(Routes.REGISTER) {
            RegisterScreen(navController = navController)
        }

        // Екран відновлення пароля (залишаємо як було, поки не переписали його UI)
        composable(Routes.FORGOT_PASSWORD) {
            ForgotPasswordScreen(
                onBackClick = { navController.popBackStack() },
                onSendLinkClick = { email -> println("Відправлено на $email") }
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

        // Екрани Умов та Політики
        composable("legal/{type}") { backStackEntry ->
            val type = backStackEntry.arguments?.getString("type") ?: "terms"
            val title = if (type == "terms") "Умови користування" else "Політика компанії"
            val text = if (type == "terms")
                com.lazor.growthspace.ui.LegalData.termsText
            else
                com.lazor.growthspace.ui.LegalData.policyText

            com.lazor.growthspace.ui.legal.LegalScreen(
                title = title,
                content = text,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}