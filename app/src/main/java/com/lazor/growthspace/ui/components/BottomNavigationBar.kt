package com.lazor.growthspace.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.lazor.growthspace.navigation.NavigationItem
import com.lazor.growthspace.ui.theme.PrimaryBlue
import com.lazor.growthspace.ui.theme.SurfaceDark
import com.lazor.growthspace.ui.theme.TextGray
import com.lazor.growthspace.ui.theme.TextWhite

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
        containerColor = SurfaceDark, // Використовуємо колір із нашої теми
        tonalElevation = 8.dp
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(text = item.title, style = MaterialTheme.typography.labelSmall) },
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        // Повертаємося до стартового екрана графа, щоб не накопичувати стеки
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) { saveState = true }
                        }
                        // Уникаємо створення копій одного і того ж екрана
                        launchSingleTop = true
                        // Зберігаємо стан екрана при перемиканні
                        restoreState = true
                    }
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = PrimaryBlue,
                    selectedTextColor = PrimaryBlue,
                    unselectedIconColor = TextGray,
                    unselectedTextColor = TextGray,
                    indicatorColor = SurfaceDark // Прибираємо стандартне коло виділення або робимо його в колір фону
                )
            )
        }
    }
}