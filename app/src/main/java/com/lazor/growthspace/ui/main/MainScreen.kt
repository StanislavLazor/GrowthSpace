package com.lazor.growthspace.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lazor.growthspace.navigation.Routes
import com.lazor.growthspace.ui.components.BottomNavigationBar
import com.lazor.growthspace.ui.home.HomeScreen
import com.lazor.growthspace.ui.coach.CoachProfileScreen

@Composable
fun MainScreen() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavigationBar(navController = navController) }
    ) { innerPadding ->
        // Навігація всередині головного екрана
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Routes.HOME) {
                // Передаємо navController, щоб HomeScreen міг відкривати інші екрани
                HomeScreen(navController = navController)
            }

            // НОВИЙ БЛОК: Екран профілю коуча
            composable(
                route = Routes.COACH_PROFILE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                // Витягуємо ID з маршруту
                val coachId = backStackEntry.arguments?.getInt("id") ?: 1

                CoachProfileScreen(
                    coachId = coachId,
                    onBackClick = {
                        navController.popBackStack() // Повернення назад
                    },
                    onBookSessionClick = {
                        // Тимчасова заглушка для бронювання
                        println("Бронювання сесії з коучем $coachId")
                    }
                )
            }

            composable("sessions") { }
            composable("chat") { }
            composable("progress") { }
            composable("profile") { }
        }
    }
}