package com.lazor.growthspace.ui.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.lazor.growthspace.navigation.Routes
import com.lazor.growthspace.ui.chat.ChatListScreen
import com.lazor.growthspace.ui.chat.ChatScreen
import com.lazor.growthspace.ui.coach.*
import com.lazor.growthspace.ui.components.BottomNavigationBar
import com.lazor.growthspace.ui.home.HomeScreen
import com.lazor.growthspace.ui.profile.EditProfileScreen
import com.lazor.growthspace.ui.profile.ProfileScreen
import com.lazor.growthspace.ui.progress.ProgressScreen
import com.lazor.growthspace.ui.session.SessionDetailsScreen
import com.lazor.growthspace.ui.session.SessionsScreen
import com.lazor.growthspace.ui.session.SessionsViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainScreen(
    onLogout: () -> Unit,
    onNavigateToLegal: (String) -> Unit = {}
) {
    val navController = rememberNavController()

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavRoutes = listOf(
        Routes.HOME,
        "sessions",
        "chat",
        "progress",
        "profile"
    )

    val showBottomBar = currentRoute in bottomNavRoutes

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Routes.HOME,
            modifier = Modifier.padding(innerPadding)
        ) {
            // ГОЛОВНА ВКЛАДКА (КОУЧІ)
            composable(Routes.HOME) {
                HomeScreen(navController = navController)
            }

            // ПРОЦЕС БРОНЮВАННЯ (Ці екрани не мають BottomBar)
            composable(
                route = Routes.COACH_PROFILE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getString("id") ?: ""
                CoachProfileScreen(
                    coachId = coachId,
                    onBackClick = { navController.popBackStack() },
                    onBookSessionClick = { navController.navigate("booking_date/$coachId") }
                )
            }

            composable(
                route = Routes.BOOKING_DATE,
                arguments = listOf(navArgument("id") { type = NavType.StringType })
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getString("id") ?: ""
                BookingDateScreen(
                    coachId = coachId,
                    onBackClick = { navController.popBackStack() },
                    onNextClick = { date -> navController.navigate("booking_time/$coachId/$date") }
                )
            }

            composable(
                route = Routes.BOOKING_TIME,
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getString("id") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                BookingTimeScreen(
                    coachId = coachId,
                    selectedDate = date,
                    onBackClick = { navController.popBackStack() },
                    onChangeDateClick = { navController.popBackStack() },
                    onConfirmClick = { time -> navController.navigate("booking_confirmation/$coachId/$date/$time") }
                )
            }

            composable(
                route = Routes.BOOKING_CONFIRMATION,
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getString("id") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val time = backStackEntry.arguments?.getString("time") ?: ""
                BookingConfirmScreen(
                    coachId = coachId,
                    selectedDate = date,
                    selectedTime = time,
                    onBackClick = { navController.popBackStack() },
                    onSuccess = { navController.navigate("booking_status/$coachId/$date/$time") }
                )
            }

            composable(
                route = Routes.BOOKING_STATUS,
                arguments = listOf(
                    navArgument("id") { type = NavType.StringType },
                    navArgument("date") { type = NavType.StringType },
                    navArgument("time") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachId = backStackEntry.arguments?.getString("id") ?: ""
                val date = backStackEntry.arguments?.getString("date") ?: ""
                val time = backStackEntry.arguments?.getString("time") ?: ""

                BookingStatusScreen(
                    coachId = coachId,
                    date = date,
                    time = time,
                    onGoToSessions = {
                        // 1. ВИДАЛЯЄМО ВСЕ АЖ ДО ГОЛОВНОГО ЕКРАНА
                        navController.popBackStack(Routes.HOME, inclusive = false)

                        // 2. ТЕПЕР ПЕРЕХОДИМО НА СЕСІЇ
                        navController.navigate("sessions") {
                            popUpTo(navController.graph.findStartDestination().id) {
                                saveState = false
                            }
                            launchSingleTop = true
                            restoreState = false
                        }
                    }
                )
            }

            // ВКЛАДКИ BOTTOM NAV
            composable("sessions") {
                SessionsScreen(
                    onSessionClick = { sessionId ->
                        navController.navigate("session_details/$sessionId")
                    }
                )
            }

            composable("session_details/{sessionId}") { backStackEntry ->
                val sessionId = backStackEntry.arguments?.getString("sessionId") ?: ""

                // Отримуємо ViewModel через Koin
                val sessionsViewModel: SessionsViewModel = koinViewModel()
                val state by sessionsViewModel.state.collectAsState()

                // Шукаємо потрібну сесію за ID зі списку завантажених
                val session = state.sessions.find { it.id == sessionId }
                val currentUser = state.currentUser

                if (session != null && currentUser != null) {
                    val isCoach = currentUser.role == "coach"
                    val otherUserName = if (isCoach) session.clientName else session.coachName

                    SessionDetailsScreen(
                        session = session,
                        isCoach = isCoach,
                        otherUserName = otherUserName,
                        onBackClick = { navController.popBackStack() },
                        onStatusChange = { newStatus ->
                            sessionsViewModel.updateSessionStatus(session.id, newStatus)
                            navController.popBackStack() // Повертаємось назад після зміни статусу
                        },
                        onSaveNotes = { notes, privateNotes ->
                            sessionsViewModel.saveSessionNotes(session.id, notes, privateNotes.takeIf { isCoach })
                            navController.popBackStack() // Повертаємось назад після збереження
                        }
                    )
                }
            }

            composable("chat") {
                ChatListScreen(
                    onChatClick = { name, id ->
                        val encodedName = android.net.Uri.encode(name)
                        navController.navigate("chat_room/$encodedName/$id")
                    }
                )
            }

            composable(
                route = "chat_room/{coachName}/{coachId}",
                arguments = listOf(
                    navArgument("coachName") { type = NavType.StringType },
                    navArgument("coachId") { type = NavType.StringType }
                )
            ) { backStackEntry ->
                val coachName = backStackEntry.arguments?.getString("coachName") ?: "Чат"
                val coachId = backStackEntry.arguments?.getString("coachId") ?: ""

                ChatScreen(
                    coachName = coachName,
                    coachId = coachId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable("progress") { ProgressScreen() }

            composable("profile") {
                ProfileScreen(
                    onLogoutClick = { onLogout() },
                    onEditAvatarClick = { navController.navigate("edit_profile") },
                    onLegalClick = { type -> onNavigateToLegal(type) },
                    onScheduleClick = { navController.navigate("coach_schedule") }
                )
            }

            // ЕКРАНИ ПРОФІЛЮ (БЕЗ BOTTOM BAR)
            composable("edit_profile") {
                EditProfileScreen(
                    onBackClick = { navController.popBackStack() },
                    onSaveClick = { navController.popBackStack() }
                )
            }

            composable("coach_schedule") {
                CoachScheduleScreen(
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}