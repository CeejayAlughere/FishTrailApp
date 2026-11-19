package com.fishtrail.app

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.fishtrail.app.ui.theme.HarbourInk
import com.fishtrail.app.ui.theme.TideMint

private data class Tab(val route: String, val label: String, val icon: ImageVector)

private val tabs = listOf(
    Tab("home", "Home", Icons.Outlined.Home),
    Tab("map", "Map", Icons.Outlined.Map),
    Tab("catchlog", "Log catch", Icons.Filled.Add),
    Tab("history", "History", Icons.Outlined.History),
    Tab("settings", "Settings", Icons.Outlined.Settings)
)

@Composable
fun BottomNavigationBar(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxWidth()
            .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier.widthIn(max = 520.dp).fillMaxWidth(),
            shape = RoundedCornerShape(26.dp),
            color = HarbourInk,
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.18f)),
            shadowElevation = 5.dp
        ) {
            Row(
                modifier = Modifier.selectableGroup().padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                tabs.forEach { tab ->
                    val selected = currentRoute == tab.route
                    val isCatch = tab.route == "catchlog"
                    val indicator by animateColorAsState(
                        if (isCatch) TideMint else if (selected) Color.White.copy(alpha = 0.15f)
                        else Color.Transparent,
                        label = "Tab indicator"
                    )
                    Column(
                        modifier = Modifier.weight(1f).clip(RoundedCornerShape(20.dp))
                            .selectable(selected, role = Role.Tab, onClick = { onItemClick(tab.route) })
                            .heightIn(min = 60.dp).padding(vertical = 4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier.size(width = 40.dp, height = 28.dp)
                                .background(indicator, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(tab.icon, contentDescription = null,
                                tint = if (isCatch) HarbourInk else if (selected) TideMint else Color.White,
                                modifier = Modifier.size(22.dp))
                        }
                        Text(tab.label, style = MaterialTheme.typography.labelSmall,
                            color = if (selected) TideMint else Color.White)
                        Box(Modifier.size(width = 12.dp, height = 2.dp)
                            .background(if (selected) TideMint else Color.Transparent, RoundedCornerShape(1.dp)))
                    }
                }
            }
        }
    }
}
