package com.example.ui

import com.example.model.FbSpeedMode

enum class FbTab {
    HOME,
    MESSAGES,
    NOTIFICATIONS,
    VIDEOS,
    MENU
}

data class FbUiState(
    val currentUrl: String = "https://m.facebook.com",
    val pageTitle: String = "Facebook Lite",
    val isLoading: Boolean = false,
    val progress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val isOffline: Boolean = false,
    val speedMode: FbSpeedMode = FbSpeedMode.STANDARD,
    val isDataSaverEnabled: Boolean = false,
    val isNightModeEnabled: Boolean = false,
    val isDesktopModeEnabled: Boolean = false,
    val textZoom: Int = 100,
    val activeTab: FbTab = FbTab.HOME,
    val isSpeedSheetVisible: Boolean = false,
    val snackbarMessage: String? = null
)
