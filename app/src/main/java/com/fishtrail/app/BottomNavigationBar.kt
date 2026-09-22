package com.fishtrail.app

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsScore
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    NavigationBar(
        modifier = modifier
    ) {

        NavigationBarItem(
            selected = currentRoute == "home",
            onClick = {
                onItemClick("home")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Home,
                    contentDescription = "Home"
                )
            },
            label = {
                Text("Home")
            }
        )

        NavigationBarItem(
            selected = currentRoute == "catchlog",
            onClick = {
                onItemClick("catchlog")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.SportsScore,
                    contentDescription = "Catch Log"
                )
            },
            label = {
                Text("Catch Log")
            }
        )

        NavigationBarItem(
            selected = currentRoute == "map",
            onClick = {
                onItemClick("map")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = "Map"
                )
            },
            label = {
                Text("Map")
            }
        )

        NavigationBarItem(
            selected = currentRoute == "history",
            onClick = {
                onItemClick("history")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History"
                )
            },
            label = {
                Text("History")
            }
        )

        NavigationBarItem(
            selected = currentRoute == "settings",
            onClick = {
                onItemClick("settings")
            },
            icon = {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Settings"
                )
            },
            label = {
                Text("Settings")
            }
        )
    }
}