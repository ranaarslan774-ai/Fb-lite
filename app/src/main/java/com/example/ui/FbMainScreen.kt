package com.example.ui

import android.net.Uri
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.example.model.FbSpeedMode
import com.example.ui.components.FbBottomBar
import com.example.ui.components.FbOfflineView
import com.example.ui.components.FbSpeedSheet
import com.example.ui.components.FbTopBar
import com.example.ui.components.FbWebViewContainer

@Composable
fun FbMainScreen(
    viewModel: FbViewModel,
    onFileChooserRequested: (ValueCallback<Array<Uri>>?, WebChromeClient.FileChooserParams?) -> Boolean,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.dismissSnackbar()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            FbTopBar(
                uiState = uiState,
                onBackClick = { viewModel.goBack() },
                onReloadClick = { viewModel.reload() },
                onSpeedSheetClick = { viewModel.showSpeedSheet(true) },
                onSearch = { query -> viewModel.searchFacebook(query) }
            )
        },
        bottomBar = {
            FbBottomBar(
                activeTab = uiState.activeTab,
                onTabSelected = { tab -> viewModel.navigateToTab(tab) }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            FbWebViewContainer(
                uiState = uiState,
                webCommands = viewModel.webCommands,
                onUrlChanged = { url -> viewModel.onUrlChanged(url) },
                onTitleChanged = { title -> viewModel.onTitleChanged(title) },
                onProgressChanged = { progress -> viewModel.onProgressChanged(progress) },
                onLoadingStateChanged = { loading -> viewModel.onLoadingStateChanged(loading) },
                onNavigationStateChanged = { canBack, canFwd ->
                    viewModel.onNavigationStateChanged(canBack, canFwd)
                },
                onNetworkError = { isOffline -> viewModel.onNetworkError(isOffline) },
                onFileChooserRequested = onFileChooserRequested
            )

            // Offline Overlay
            AnimatedVisibility(
                visible = uiState.isOffline,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                FbOfflineView(
                    onRetry = { viewModel.reload() },
                    onOpenSuperLite = {
                        viewModel.setSpeedMode(FbSpeedMode.SUPER_LITE)
                    }
                )
            }
        }
    }

    // Speed & Lite Controls Sheet
    if (uiState.isSpeedSheetVisible) {
        FbSpeedSheet(
            uiState = uiState,
            onDismiss = { viewModel.showSpeedSheet(false) },
            onSpeedModeSelected = { mode -> viewModel.setSpeedMode(mode) },
            onToggleDataSaver = { viewModel.toggleDataSaver() },
            onToggleNightMode = { viewModel.toggleNightMode() },
            onToggleDesktopMode = { viewModel.toggleDesktopMode() },
            onTextZoomChanged = { zoom -> viewModel.setTextZoom(zoom) },
            onShortcutClicked = { url -> viewModel.loadUrl(url) },
            onClearCache = { viewModel.boostSpeedClearCache() },
            onClearAllAndLogout = { viewModel.clearAllDataAndLogout() },
            onReload = { viewModel.reload() }
        )
    }
}
