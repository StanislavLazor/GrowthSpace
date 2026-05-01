package com.lazor.growthspace.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lazor.growthspace.ui.coach.BookingConfirmScreen
import androidx.navigation.navArgument
import com.lazor.growthspace.navigation.Routes
import com.lazor.growthspace.ui.coach.BookingTimeScreen
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.lazor.growthspace.ui.coach.BookingDateScreen
import com.lazor.growthspace.ui.coach.CoachProfileScreen
import com.lazor.growthspace.ui.components.BottomNavigationBar
import com.lazor.growthspace.ui.coach.BookingStatusScreen
import com.lazor.growthspace.ui.session.SessionsScreen
import com.lazor.growthspace.ui.home.HomeScreen
import com.lazor.growthspace.ui.progress.ProgressScreen
import com.lazor.growthspace.ui.chat.ChatListScreen
import com.lazor.growthspace.ui.profile.EditProfileScreen
import com.lazor.growthspace.ui.profile.ProfileScreen

@Composable
fun MainScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()

    // СЛУХАЄМО ПОТОЧНИЙ МАРШРУТ
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Визначаємо, чи потрібно ховати нижнє меню (якщо ми на екрані дати АБО підтвердження)
    val isBookingFlow = currentRoute?.startsWith("booking_date") == true ||
            currentRoute?.startsWith("booking_time") == true ||
            currentRoute?.startsWith("booking_confirmation") == true ||
            currentRoute?.startsWith("booking_status") == true

    Scaffold(
        bottomBar = {
            // ХОВАЄМО МЕНЮ, ЯКЩО МИ В ПРОЦЕСІ БРОНЮВАННЯ
            if (!isBookingFlow) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ГОЛОВНИЙ ЕКРАН
            composable(Routes.HOME) {
                HomeScreen(navController = navController)
            }

            // ПРОФІЛЬ КОУЧА
            composable(
                route = Routes.COACH_PROFILE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getInt("id") ?: 1
                CoachProfileScreen(
                    coachId = coachId,
                    onBackClick = { navController.popBackStack() },
                    onBookSessionClick = {
                        // Перехід на календар
                        navController.navigate("booking_date/$coachId")
                    }
                )
            }

            // ЕКРАН ВИБОРУ ДАТИ (КАЛЕНДАР)
            composable(
                route = Routes.BOOKING_DATE,
                arguments = listOf(navArgument("id") { type = NavType.IntType })
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getInt("id") ?: 1
                BookingDateScreen(
                    coachId = coachId,
                    onBackClick = { navController.popBackStack() },
                    onNextClick = { selectedDate ->
                        // ТЕПЕР ЙДЕМО НА ВИБІР ЧАСУ
                        navController.navigate("booking_time/$coachId/$selectedDate")
                    }
                )
            }

            // 2. ВИБІР ЧАСУ
            composable(
                route = Routes.BOOKING_TIME,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getInt("id") ?: 1
                val date = backStackEntry.arguments?.getString("date") ?: ""

                        BookingTimeScreen(
                            coachId = coachId,
                            selectedDate = date,
                            onBackClick = { navController.popBackStack() },
                            onChangeDateClick = { navController.popBackStack() }, // Повертає на календар
                            onConfirmClick = { selectedTime ->
                                navController.navigate("booking_confirmation/$coachId/$date/$selectedTime")
                            }
                        )
            }

            composable(
                route = Routes.BOOKING_CONFIRMATION,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getInt("id") ?: 1
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val time = backStackEntry.arguments?.getString("time") ?: ""

                BookingConfirmScreen(
                    coachId = coachId,
                    selectedDate = date,
                    selectedTime = time,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = {
                        navController.navigate("booking_status/$coachId/$date/$time") {
                            popUpTo(Routes.HOME)
                        }
                    }
                )
            }

            // СТАТУС БРОНЮВАННЯ
            composable(
                route = Routes.BOOKING_STATUS,
                arguments = listOf(
                    navArgument("id") { type = NavType.IntType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getInt("id") ?: 1
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val time = backStackEntry.arguments?.getString("time") ?: ""

                BookingStatusScreen(
                    coachId = coachId,
                    date = date,
                    time = time,
                    onGoToSessions = {
                        navController.navigate("sessions") {
                            popUpTo(Routes.HOME)
                            launchSingleTop = true
                        }
                    }
                )
            }

            // Порожні вкладки для нижнього меню
            composable("sessions") {
                SessionsScreen()
            }
            composable("chat") {
                ChatListScreen(
                    onChatClick = { coachName ->
                        val encodedName = android.net.Uri.encode(coachName)
                        navController.navigate("chat_room/$encodedName")
                    }
                )
            }

            composable(
                route = "chat_room/{coachName}",
                arguments = listOf(navArgument("coachName") { type = NavType.StringType })
            ) { backStackEntry ->
                val coachName = backStackEntry.arguments?.getString("coachName") ?: "Коуч"

                // Визначаємо ID коуча на основі його імені
                val coachId = when {
                    coachName.contains("Олександр") -> 1
                    coachName.contains("Олена") -> 2
                    coachName.contains("Дмитро") -> 3
                    else -> 1
                }

                com.lazor.growthspace.ui.chat.ChatScreen(
                    coachName = coachName,
                    onBackClick = { navController.popBackStack() },
                    onProfileClick = {
                        navController.navigate("coach_profile/$coachId")
                    }
                )
            }
            composable("progress") {
                ProgressScreen()
            }
            composable("profile") {
                ProfileScreen(
                    onLogoutClick = { onLogout()},
                    onEditAvatarClick = {
                        navController.navigate("edit_profile")
                    }
                )
            }
            composable("edit_profile") {
                EditProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = {
                        // Поки просто повертаємось назад
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}