package com.fishtrail.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AddCircleOutline
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier,
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp
    ) {
        val selectedColors = NavigationBarItemDefaults.colors(
            selectedIconColor = MaterialTheme.colorScheme.primary,
            selectedTextColor = MaterialTheme.colorScheme.primary,
            indicatorColor = MaterialTheme.colorScheme.primaryContainer,
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )

        // Home
        val homeSelected = currentRoute == "home"
        NavigationBarItem(
            selected = homeSelected,
            onClick = { onItemClick("home") },
            icon = {
                Icon(
                    imageVector = if (homeSelected) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text(
                    text = "Home",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (homeSelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = selectedColors
        )

        // Catch Log
        val catchLogSelected = currentRoute == "catchlog"
        NavigationBarItem(
            selected = catchLogSelected,
            onClick = { onItemClick("catchlog") },
            icon = {
                Icon(
                    imageVector = if (catchLogSelected) Icons.Filled.AddCircle else Icons.Outlined.AddCircleOutline,
                    contentDescription = "Catch Log"
                )
            },
            label = {
                Text(
                    text = "Catch Log",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (catchLogSelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = selectedColors
        )

        // Map
        val mapSelected = currentRoute == "map"
        NavigationBarItem(
            selected = mapSelected,
            onClick = { onItemClick("map") },
            icon = {
                Icon(
                    imageVector = if (mapSelected) Icons.Filled.Map else Icons.Outlined.Map,
                    contentDescription = "Map"
                )
            },
            label = {
                Text(
                    text = "Map",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (mapSelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = selectedColors
        )

        // History
        val historySelected = currentRoute == "history"
        NavigationBarItem(
            selected = historySelected,
            onClick = { onItemClick("history") },
            icon = {
                Icon(
                    imageVector = if (historySelected) Icons.Filled.History else Icons.Outlined.History,
                    contentDescription = "History"
                )
            },
            label = {
                Text(
                    text = "History",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (historySelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = selectedColors
        )

        // Settings
        val settingsSelected = currentRoute == "settings"
        NavigationBarItem(
            selected = settingsSelected,
            onClick = { onItemClick("settings") },
            icon = {
                Icon(
                    imageVector = if (settingsSelected) Icons.Filled.Settings else Icons.Outlined.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (settingsSelected) FontWeight.Bold else FontWeight.Medium
                )
            },
            colors = selectedColors
        )
    }
}
