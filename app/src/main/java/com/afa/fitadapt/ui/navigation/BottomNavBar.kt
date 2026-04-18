// =============================================================
// AFA - Attività Fisica Adattata
// Bottom Navigation Bar
// =============================================================
package com.afa.fitadapt.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import com.afa.fitadapt.ui.theme.NavyBlue
import com.afa.fitadapt.ui.theme.FitlyBlueLight
import com.afa.fitadapt.ui.theme.WarmWhite

/**
 * Elemento della bottom navigation bar.
 */
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

/**
 * I 4 tab della bottom navigation.
 */
val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Outlined.Home),
    BottomNavItem("Scheda", Screen.ActiveCard.route, Icons.Outlined.FitnessCenter),
    BottomNavItem("Progressi", Screen.Progress.route, Icons.AutoMirrored.Outlined.ShowChart),
    BottomNavItem("Altro", Screen.More.route, Icons.Outlined.Menu),
)

/**
 * Bottom Navigation Bar dell'app.
 * Mostra 4 tab: Home, Scheda, Progressi, Altro.
 * Visibile solo nelle schermate principali.
 */
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    NavigationBar(
        containerColor = WarmWhite,
    ) {
        bottomNavItems.forEach { item ->
            val isSelected = currentRoute == item.route

            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (currentRoute != item.route) {
                        navController.navigate(item.route) {
                            // Evita duplicati nello stack
                            popUpTo(Screen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                },
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label) },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = NavyBlue,
                    selectedTextColor = NavyBlue,
                    indicatorColor = FitlyBlueLight,
                )
            )
        }
    }
}
