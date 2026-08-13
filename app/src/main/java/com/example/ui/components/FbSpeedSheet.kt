package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.CleanHands
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DataSaverOn
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.FbSpeedMode
import com.example.ui.FbUiState
import com.example.ui.theme.FbBlue
import com.example.ui.theme.FbRed

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun FbSpeedSheet(
    uiState: FbUiState,
    onDismiss: () -> Unit,
    onSpeedModeSelected: (FbSpeedMode) -> Unit,
    onToggleDataSaver: () -> Unit,
    onToggleNightMode: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onTextZoomChanged: (Int) -> Unit,
    onShortcutClicked: (String) -> Unit,
    onClearCache: () -> Unit,
    onClearAllAndLogout: () -> Unit,
    onReload: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Log Out / Clear Session", fontWeight = FontWeight.Bold) },
            text = { Text("This will clear all cookies and login sessions. You can log in again anytime.") },
            confirmButton = {
                Button(
                    onClick = {
                        showLogoutDialog = false
                        onDismiss()
                        onClearAllAndLogout()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = FbRed)
                ) {
                    Text("Log Out")
                }
            },
            dismissButton = {
                TextButton(onClick = { showLogoutDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(vertical = 10.dp)
                    .width(40.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f))
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(FbBlue.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Speed,
                            contentDescription = null,
                            tint = FbBlue,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Speed & Lite Controls",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Fast loading & lightweight browsing",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                IconButton(onClick = onDismiss) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close settings"
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Speed Engine Selector
            Text(
                text = "SPEED ENGINE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FbBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                SpeedModeItem(
                    mode = FbSpeedMode.STANDARD,
                    isSelected = uiState.speedMode == FbSpeedMode.STANDARD,
                    icon = Icons.Default.FlashOn,
                    badge = "Recommended",
                    onClick = {
                        onSpeedModeSelected(FbSpeedMode.STANDARD)
                        onDismiss()
                    }
                )

                SpeedModeItem(
                    mode = FbSpeedMode.TOUCH,
                    isSelected = uiState.speedMode == FbSpeedMode.TOUCH,
                    icon = Icons.Default.TouchApp,
                    badge = "Faster UI",
                    onClick = {
                        onSpeedModeSelected(FbSpeedMode.TOUCH)
                        onDismiss()
                    }
                )

                SpeedModeItem(
                    mode = FbSpeedMode.SUPER_LITE,
                    isSelected = uiState.speedMode == FbSpeedMode.SUPER_LITE,
                    icon = Icons.Default.Wifi,
                    badge = "2G / Weak Net",
                    onClick = {
                        onSpeedModeSelected(FbSpeedMode.SUPER_LITE)
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // 2. Quick Toggles
            Text(
                text = "PERFORMANCE & DISPLAY",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FbBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(8.dp))

            ToggleRow(
                icon = Icons.Default.DataSaverOn,
                title = "Data Saver Mode",
                subtitle = "Reduces heavy scripts and media for blazing speed",
                isChecked = uiState.isDataSaverEnabled,
                onCheckedChange = { onToggleDataSaver() },
                testTag = "toggle_data_saver"
            )

            ToggleRow(
                icon = Icons.Default.DarkMode,
                title = "Night / Dark Mode",
                subtitle = "Easy on eyes and saves battery",
                isChecked = uiState.isNightModeEnabled,
                onCheckedChange = { onToggleNightMode() },
                testTag = "toggle_night_mode"
            )

            ToggleRow(
                icon = Icons.Default.DesktopWindows,
                title = "Desktop Site",
                subtitle = "Request full desktop web version",
                isChecked = uiState.isDesktopModeEnabled,
                onCheckedChange = { onToggleDesktopMode() },
                testTag = "toggle_desktop_mode"
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Text Zoom
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.TextFields,
                        contentDescription = null,
                        tint = FbBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Text Size Zoom: ${uiState.textZoom}%",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                if (uiState.textZoom != 100) {
                    TextButton(onClick = { onTextZoomChanged(100) }) {
                        Text("Reset", fontSize = 12.sp)
                    }
                }
            }
            Slider(
                value = uiState.textZoom.toFloat(),
                onValueChange = { onTextZoomChanged(it.toInt()) },
                valueRange = 75f..130f,
                steps = 5,
                colors = SliderDefaults.colors(
                    thumbColor = FbBlue,
                    activeTrackColor = FbBlue
                ),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // 3. Quick Shortcuts
            Text(
                text = "FAST SHORTCUTS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = FbBlue,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(10.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ShortcutChip(
                    icon = Icons.Default.Person,
                    label = "Profile",
                    onClick = {
                        onShortcutClicked("${uiState.speedMode.baseUrl}/me")
                        onDismiss()
                    }
                )
                ShortcutChip(
                    icon = Icons.Default.Bookmark,
                    label = "Saved",
                    onClick = {
                        onShortcutClicked("${uiState.speedMode.baseUrl}/saved")
                        onDismiss()
                    }
                )
                ShortcutChip(
                    icon = Icons.Default.ShoppingBag,
                    label = "Marketplace",
                    onClick = {
                        onShortcutClicked("${uiState.speedMode.baseUrl}/marketplace")
                        onDismiss()
                    }
                )
                ShortcutChip(
                    icon = Icons.Default.Group,
                    label = "Groups",
                    onClick = {
                        onShortcutClicked("${uiState.speedMode.baseUrl}/groups")
                        onDismiss()
                    }
                )
            }

            Spacer(modifier = Modifier.height(20.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant)
            Spacer(modifier = Modifier.height(16.dp))

            // 4. Action Boosters & Logout
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        onClearCache()
                        onDismiss()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("boost_speed_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CleanHands,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Speed Boost", fontSize = 13.sp)
                }

                OutlinedButton(
                    onClick = { showLogoutDialog = true },
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = FbRed),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("logout_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Log Out", fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SpeedModeItem(
    mode: FbSpeedMode,
    isSelected: Boolean,
    icon: ImageVector,
    badge: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) FbBlue.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (isSelected) Modifier.border(1.5.dp, FbBlue, RoundedCornerShape(12.dp)) else Modifier
            )
            .testTag("speed_mode_${mode.name.lowercase()}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) FbBlue else MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = mode.title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (isSelected) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                Text(
                    text = mode.subtitle,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!isChecked) }
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isChecked) FbBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Switch(
            checked = isChecked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = FbBlue
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

@Composable
private fun ShortcutChip(
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp,
        modifier = Modifier.height(38.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = FbBlue,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}
