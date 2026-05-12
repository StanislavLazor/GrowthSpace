package com.lazor.growthspace.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lazor.growthspace.navigation.NavigationItem
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.lazor.growthspace.ui.theme.PrimaryBlue
import com.lazor.growthspace.ui.theme.SurfaceDark
import com.lazor.growthspace.ui.theme.TextGray
import androidx.compose.ui.unit.dp
import com.lazor.growthspace.navigation.Routes

@Composable
fun BottomNavigationBar(navController: NavController) {
    val items = listOf(
        NavigationItem.Home,
        NavigationItem.Sessions,
        NavigationItem.Chat,
        NavigationItem.Progress,
        NavigationItem.Profile
    )

    NavigationBar(
        containerColor = SurfaceDark,
        tonalElevation = 0.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, style = MaterialTheme.typography.labelSmall) },
                selected = currentRoute == item.route ||
                        (item.route == Routes.HOME && currentRoute?.startsWith("coach_profile") == true),
                onClick = {
                    navController.navigate(item.route) {
                        // Очищаємо стек до головного екрана, але ЗБЕРІГАЄМО стан
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Уникаємо створення копій одного й того ж екрана
                        launchSingleTop = true
                        // ВІДНОВЛЮЄМО стан (наприклад, позицію скролу), якщо ми сюди повертаємось
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = SurfaceDark
                )
            )
        }
    }
}