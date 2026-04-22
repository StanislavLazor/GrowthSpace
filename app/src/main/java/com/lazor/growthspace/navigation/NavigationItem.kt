package com.lazor.growthspace.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

sealed class NavigationItem(val route: String, val icon: ImageVector, val title: String) {
    object Home : NavigationItem(Routes.HOME, Icons.Default.Home, "Головна")
    object Sessions : NavigationItem("sessions", Icons.Default.DateRange, "Сесії")
    object Chat : NavigationItem("chat", Icons.Default.Chat, "Чат")
    object Progress : NavigationItem("progress", Icons.Default.TrendingUp, "Прогрес")
    object Profile : NavigationItem("profile", Icons.Default.Person, "Профіль")
}