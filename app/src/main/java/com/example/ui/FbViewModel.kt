package com.example.ui

import android.app.Application
import android.webkit.CookieManager
import android.webkit.WebStorage
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.FbPreferences
import com.example.model.FbSpeedMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

sealed class FbWebCommand {
    data class LoadUrl(val url: String) : FbWebCommand()
    object Reload : FbWebCommand()
    object GoBack : FbWebCommand()
    object GoForward : FbWebCommand()
    object ClearCache : FbWebCommand()
    object ClearAllData : FbWebCommand()
    data class ApplyDarkMode(val enabled: Boolean) : FbWebCommand()
}

class FbViewModel(application: Application) : AndroidViewModel(application) {

    private val preferences = FbPreferences(application)

    private val _uiState = MutableStateFlow(
        FbUiState(
            currentUrl = preferences.speedMode.baseUrl,
            speedMode = preferences.speedMode,
            isDataSaverEnabled = preferences.isDataSaverEnabled,
            isNightModeEnabled = preferences.isNightModeEnabled,
            isDesktopModeEnabled = preferences.isDesktopModeEnabled,
            textZoom = preferences.textZoom
        )
    )
    val uiState: StateFlow<FbUiState> = _uiState.asStateFlow()

    private val _webCommands = MutableSharedFlow<FbWebCommand>()
    val webCommands: SharedFlow<FbWebCommand> = _webCommands.asSharedFlow()

    fun onUrlChanged(url: String) {
        val tab = when {
            url.contains("/messages") -> FbTab.MESSAGES
            url.contains("/notifications") -> FbTab.NOTIFICATIONS
            url.contains("/watch") || url.contains("/videos") -> FbTab.VIDEOS
            url.contains("/menu") || url.contains("/bookmarks") || url.contains("/pages") -> FbTab.MENU
            url.contains("facebook.com") && !url.contains("/messages") && !url.contains("/notifications") -> FbTab.HOME
            else -> _uiState.value.activeTab
        }

        _uiState.update {
            it.copy(
                currentUrl = url,
                activeTab = tab,
                isOffline = false
            )
        }
    }

    fun onTitleChanged(title: String) {
        val cleanTitle = if (title.isBlank() || title.equals("Facebook", ignoreCase = true) || title.startsWith("http")) {
            "Facebook Lite"
        } else {
            title
        }
        _uiState.update { it.copy(pageTitle = cleanTitle) }
    }

    fun onProgressChanged(progress: Int) {
        _uiState.update {
            it.copy(
                progress = progress,
                isLoading = progress in 1..99
            )
        }
    }

    fun onLoadingStateChanged(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    fun onNavigationStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        _uiState.update {
            it.copy(
                canGoBack = canGoBack,
                canGoForward = canGoForward
            )
        }
    }

    fun onNetworkError(isOffline: Boolean) {
        _uiState.update {
            it.copy(
                isOffline = isOffline,
                isLoading = false
            )
        }
    }

    fun navigateToTab(tab: FbTab) {
        val baseUrl = _uiState.value.speedMode.baseUrl
        val targetUrl = when (tab) {
            FbTab.HOME -> baseUrl
            FbTab.MESSAGES -> when (_uiState.value.speedMode) {
                FbSpeedMode.SUPER_LITE -> "$baseUrl/messages"
                else -> "$baseUrl/messages"
            }
            FbTab.NOTIFICATIONS -> "$baseUrl/notifications.php"
            FbTab.VIDEOS -> "$baseUrl/watch"
            FbTab.MENU -> when (_uiState.value.speedMode) {
                FbSpeedMode.SUPER_LITE -> "$baseUrl/menu/bookmarks"
                else -> "$baseUrl/menu"
            }
        }
        _uiState.update { it.copy(activeTab = tab) }
        loadUrl(targetUrl)
    }

    fun loadUrl(url: String) {
        viewModelScope.launch {
            _webCommands.emit(FbWebCommand.LoadUrl(url))
        }
    }

    fun reload() {
        viewModelScope.launch {
            _webCommands.emit(FbWebCommand.Reload)
        }
    }

    fun goBack() {
        viewModelScope.launch {
            _webCommands.emit(FbWebCommand.GoBack)
        }
    }

    fun goForward() {
        viewModelScope.launch {
            _webCommands.emit(FbWebCommand.GoForward)
        }
    }

    fun setSpeedMode(mode: FbSpeedMode) {
        preferences.speedMode = mode
        _uiState.update { it.copy(speedMode = mode) }
        loadUrl(mode.baseUrl)
        showSnackbar("Switched to ${mode.title}")
    }

    fun toggleDataSaver() {
        val newState = !_uiState.value.isDataSaverEnabled
        preferences.isDataSaverEnabled = newState
        _uiState.update { it.copy(isDataSaverEnabled = newState) }
        showSnackbar(if (newState) "Data Saver enabled" else "Data Saver disabled")
        reload()
    }

    fun toggleNightMode() {
        val newState = !_uiState.value.isNightModeEnabled
        preferences.isNightModeEnabled = newState
        _uiState.update { it.copy(isNightModeEnabled = newState) }
        viewModelScope.launch {
            _webCommands.emit(FbWebCommand.ApplyDarkMode(newState))
        }
        showSnackbar(if (newState) "Night Mode enabled" else "Night Mode disabled")
    }

    fun toggleDesktopMode() {
        val newState = !_uiState.value.isDesktopModeEnabled
        preferences.isDesktopModeEnabled = newState
        _uiState.update { it.copy(isDesktopModeEnabled = newState) }
        showSnackbar(if (newState) "Desktop Site requested" else "Mobile Site requested")
        reload()
    }

    fun setTextZoom(zoom: Int) {
        val clamped = zoom.coerceIn(70, 150)
        preferences.textZoom = clamped
        _uiState.update { it.copy(textZoom = clamped) }
    }

    fun showSpeedSheet(show: Boolean) {
        _uiState.update { it.copy(isSpeedSheetVisible = show) }
    }

    fun showSnackbar(message: String) {
        _uiState.update { it.copy(snackbarMessage = message) }
    }

    fun dismissSnackbar() {
        _uiState.update { it.copy(snackbarMessage = null) }
    }

    fun boostSpeedClearCache() {
        viewModelScope.launch {
            _webCommands.emit(FbWebCommand.ClearCache)
            showSnackbar("Speed boosted! Web cache cleaned.")
        }
    }

    fun clearAllDataAndLogout() {
        viewModelScope.launch {
            CookieManager.getInstance().removeAllCookies(null)
            CookieManager.getInstance().flush()
            WebStorage.getInstance().deleteAllData()
            _webCommands.emit(FbWebCommand.ClearAllData)
            loadUrl(_uiState.value.speedMode.baseUrl)
            showSnackbar("All sessions and cookies cleared.")
        }
    }

    fun searchFacebook(query: String) {
        if (query.isNotBlank()) {
            val encoded = java.net.URLEncoder.encode(query, "UTF-8")
            val searchUrl = "${_uiState.value.speedMode.baseUrl}/search/top/?q=$encoded"
            loadUrl(searchUrl)
        }
    }
}
