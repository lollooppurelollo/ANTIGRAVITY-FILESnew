// =============================================================
// AFA - Attività Fisica Adattata
// Bottom Navigation Bar
// =============================================================
package com.afa.fitadapt.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.afa.fitadapt.ui.navigation.Screen

/**
 * Elemento della bottom navigation bar.
 */
data class BottomNavItem(
    val label: String,
    val route: String,
    val icon: ImageVector
)

/**
 * I 5 tab della bottom navigation.
 */
val bottomNavItems = listOf(
    BottomNavItem("Home", Screen.Home.route, Icons.Outlined.Home),
    BottomNavItem("Scheda", Screen.ActiveCard.route, Icons.Outlined.FitnessCenter),
    BottomNavItem("Progressi", Screen.Progress.route, Icons.AutoMirrored.Outlined.ShowChart),
    BottomNavItem("Diario", Screen.Diary.route, Icons.Outlined.Book),
    BottomNavItem("Altro", Screen.More.route, Icons.Outlined.Menu),
)

/**
 * Bottom Navigation Bar dell'app.
 * Mostra 5 tab: Home, Scheda, Progressi, Diario, Altro.
 */
@Composable
fun BottomNavBar(
    navController: NavController,
    currentRoute: String?
) {
    Surface(
        modifier = Modifier
            .padding(start = 12.dp, end = 12.dp, bottom = 12.dp, top = 8.dp) // Aumentato top padding per evitare "taglio"
            .clip(RoundedCornerShape(28.dp)),
        tonalElevation = 8.dp,
        shadowElevation = 8.dp,
        color = MaterialTheme.colorScheme.surface.copy(alpha = 0.98f)
    ) {
        NavigationBar(
            containerColor = Color.Transparent,
            tonalElevation = 0.dp,
            modifier = Modifier.height(84.dp) // Aumentata altezza per ospitare meglio etichette
        ) {
            bottomNavItems.forEach { item ->
                val isSelected = currentRoute == item.route

                NavigationBarItem(
                    selected = isSelected,
                    onClick = {
                        if (currentRoute != item.route) {
                            navController.navigate(item.route) {
                                popUpTo(Screen.Home.route) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    },
                    icon = {
                        Icon(
                            imageVector = item.icon,
                            contentDescription = item.label,
                            modifier = Modifier.size(24.dp)
                        )
                    },
                    label = { 
                        Text(
                            text = item.label,
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                            maxLines = 1,
                            softWrap = false
                        ) 
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    )
                )
            }
        }
    }
}
