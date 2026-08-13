package com.example.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PlayCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.FbTab
import com.example.ui.theme.FbBlue

@Composable
fun FbBottomBar(
    activeTab: FbTab,
    onTabSelected: (FbTab) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .height(64.dp),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        // 1. Home Feed
        NavigationBarItem(
            selected = activeTab == FbTab.HOME,
            onClick = { onTabSelected(FbTab.HOME) },
            icon = {
                Icon(
                    imageVector = if (activeTab == FbTab.HOME) Icons.Filled.Home else Icons.Outlined.Home,
                    contentDescription = "News Feed"
                )
            },
            label = { Text("Feed", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FbBlue,
                selectedTextColor = FbBlue,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_home")
        )

        // 2. Messenger / Messages
        NavigationBarItem(
            selected = activeTab == FbTab.MESSAGES,
            onClick = { onTabSelected(FbTab.MESSAGES) },
            icon = {
                Icon(
                    imageVector = if (activeTab == FbTab.MESSAGES) Icons.Filled.ChatBubble else Icons.Outlined.ChatBubbleOutline,
                    contentDescription = "Messages"
                )
            },
            label = { Text("Chats", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FbBlue,
                selectedTextColor = FbBlue,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_messages")
        )

        // 3. Notifications
        NavigationBarItem(
            selected = activeTab == FbTab.NOTIFICATIONS,
            onClick = { onTabSelected(FbTab.NOTIFICATIONS) },
            icon = {
                Icon(
                    imageVector = if (activeTab == FbTab.NOTIFICATIONS) Icons.Filled.Notifications else Icons.Outlined.NotificationsNone,
                    contentDescription = "Notifications"
                )
            },
            label = { Text("Alerts", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FbBlue,
                selectedTextColor = FbBlue,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_notifications")
        )

        // 4. Videos / Watch
        NavigationBarItem(
            selected = activeTab == FbTab.VIDEOS,
            onClick = { onTabSelected(FbTab.VIDEOS) },
            icon = {
                Icon(
                    imageVector = if (activeTab == FbTab.VIDEOS) Icons.Filled.PlayCircle else Icons.Outlined.PlayCircleOutline,
                    contentDescription = "Videos"
                )
            },
            label = { Text("Watch", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FbBlue,
                selectedTextColor = FbBlue,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_videos")
        )

        // 5. Menu
        NavigationBarItem(
            selected = activeTab == FbTab.MENU,
            onClick = { onTabSelected(FbTab.MENU) },
            icon = {
                Icon(
                    imageVector = if (activeTab == FbTab.MENU) Icons.Filled.Menu else Icons.Outlined.Menu,
                    contentDescription = "Menu & Shortcuts"
                )
            },
            label = { Text("Menu", fontSize = 11.sp) },
            colors = NavigationBarItemDefaults.colors(
                selectedIconColor = FbBlue,
                selectedTextColor = FbBlue,
                indicatorColor = MaterialTheme.colorScheme.surfaceVariant,
                unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag("tab_menu")
        )
    }
}
