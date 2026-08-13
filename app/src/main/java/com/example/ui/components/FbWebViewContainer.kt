package com.example.ui.components

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.GeolocationPermissions
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ui.FbUiState
import com.example.ui.FbWebCommand
import kotlinx.coroutines.flow.SharedFlow

private const val DESKTOP_USER_AGENT =
    "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"

private const val DARK_MODE_CSS = """
javascript:(function() {
    var style = document.getElementById('fb-lite-dark-style');
    if (!style) {
        style = document.createElement('style');
        style.id = 'fb-lite-dark-style';
        style.type = 'text/css';
        style.innerHTML = 'html, body, #root, .m-root, [role="main"] { background-color: #18191A !important; color: #E4E6EB !important; } ' +
                          'header, nav, [role="banner"], [role="navigation"] { background-color: #242526 !important; border-color: #3A3B3C !important; } ' +
                          'input, textarea, select { background-color: #3A3B3C !important; color: #FFFFFF !important; border-color: #4E4F50 !important; } ' +
                          'a, a * { color: #4599FF !important; } ' +
                          'div[data-sigil="story-div"], article, .story_body_container { background-color: #242526 !important; border-color: #3A3B3C !important; }';
        document.head.appendChild(style);
    }
})()
"""

private const val REMOVE_DARK_MODE_CSS = """
javascript:(function() {
    var style = document.getElementById('fb-lite-dark-style');
    if (style) {
        style.parentNode.removeChild(style);
    }
})()
"""

private const val DATA_SAVER_JS = """
javascript:(function() {
    var videos = document.querySelectorAll('video');
    for (var i = 0; i < videos.length; i++) {
        videos[i].autoplay = false;
        videos[i].preload = 'none';
    }
})()
"""

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun FbWebViewContainer(
    uiState: FbUiState,
    webCommands: SharedFlow<FbWebCommand>,
    onUrlChanged: (String) -> Unit,
    onTitleChanged: (String) -> Unit,
    onProgressChanged: (Int) -> Unit,
    onLoadingStateChanged: (Boolean) -> Unit,
    onNavigationStateChanged: (canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    onNetworkError: (Boolean) -> Unit,
    onFileChooserRequested: (ValueCallback<Array<Uri>>?, WebChromeClient.FileChooserParams?) -> Boolean,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val webView = remember {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                cacheMode = WebSettings.LOAD_DEFAULT
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                setSupportZoom(true)
                allowFileAccess = true
                allowContentAccess = true
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                mediaPlaybackRequiresUserGesture = false
            }

            // Configure persistent cookies
            val cookieManager = CookieManager.getInstance()
            cookieManager.setAcceptCookie(true)
            cookieManager.setAcceptThirdPartyCookies(this, true)

            // Setup download listener
            setDownloadListener { url, userAgent, contentDisposition, mimetype, _ ->
                try {
                    val request = DownloadManager.Request(Uri.parse(url)).apply {
                        setMimeType(mimetype)
                        addRequestHeader("User-Agent", userAgent)
                        addRequestHeader("Cookie", CookieManager.getInstance().getCookie(url))
                        setDescription("Downloading file from Facebook...")
                        setTitle("Facebook Download")
                        setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                        setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "FB_Download_${System.currentTimeMillis()}")
                    }
                    val dm = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    dm.enqueue(request)
                    Toast.makeText(context, "Downloading file...", Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (ex: Exception) {
                        Toast.makeText(context, "Cannot download file", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    // Handle system back gesture
    BackHandler(enabled = uiState.canGoBack) {
        if (webView.canGoBack()) {
            webView.goBack()
        }
    }

    // Listen to Web Commands
    LaunchedEffect(webCommands) {
        webCommands.collect { command ->
            when (command) {
                is FbWebCommand.LoadUrl -> {
                    webView.loadUrl(command.url)
                }
                is FbWebCommand.Reload -> {
                    webView.reload()
                }
                is FbWebCommand.GoBack -> {
                    if (webView.canGoBack()) webView.goBack()
                }
                is FbWebCommand.GoForward -> {
                    if (webView.canGoForward()) webView.goForward()
                }
                is FbWebCommand.ClearCache -> {
                    webView.clearCache(true)
                    webView.reload()
                }
                is FbWebCommand.ClearAllData -> {
                    webView.clearCache(true)
                    webView.clearHistory()
                    webView.clearFormData()
                }
                is FbWebCommand.ApplyDarkMode -> {
                    if (command.enabled) {
                        webView.evaluateJavascript(DARK_MODE_CSS, null)
                    } else {
                        webView.evaluateJavascript(REMOVE_DARK_MODE_CSS, null)
                    }
                }
            }
        }
    }

    // Apply Desktop / Mobile User Agent
    LaunchedEffect(uiState.isDesktopModeEnabled) {
        if (uiState.isDesktopModeEnabled) {
            webView.settings.userAgentString = DESKTOP_USER_AGENT
        } else {
            webView.settings.userAgentString = null // Reset to default mobile user agent
        }
    }

    // Apply Text Zoom
    LaunchedEffect(uiState.textZoom) {
        webView.settings.textZoom = uiState.textZoom
    }

    DisposableEffect(webView) {
        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                val url = request?.url?.toString() ?: return false
                val scheme = request.url.scheme?.lowercase() ?: ""

                // Non-web schemes (whatsapp:, tel:, mailto:, intent:, etc.)
                if (scheme != "http" && scheme != "https") {
                    return try {
                        val intent = Intent(Intent.ACTION_VIEW, request.url)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        context.startActivity(intent)
                        true
                    } catch (e: Exception) {
                        true
                    }
                }

                // Handle internal Facebook domains
                val host = request.url.host?.lowercase() ?: ""
                val isFbDomain = host.contains("facebook.com") ||
                        host.contains("fb.com") ||
                        host.contains("messenger.com") ||
                        host.contains("instagram.com") ||
                        host.contains("fbcdn.net") ||
                        host.contains("fbsbx.com") ||
                        host.contains("meta.com")

                return if (isFbDomain) {
                    false // Load in this WebView
                } else {
                    // External link: open in default browser
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                        context.startActivity(intent)
                    } catch (e: Exception) {
                        view?.loadUrl(url)
                    }
                    true
                }
            }

            override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                onLoadingStateChanged(true)
                if (url != null) {
                    onUrlChanged(url)
                }
                view?.let {
                    onNavigationStateChanged(it.canGoBack(), it.canGoForward())
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                onLoadingStateChanged(false)
                CookieManager.getInstance().flush()

                view?.let {
                    onNavigationStateChanged(it.canGoBack(), it.canGoForward())

                    // Apply night mode if enabled
                    if (uiState.isNightModeEnabled) {
                        it.evaluateJavascript(DARK_MODE_CSS, null)
                    }

                    // Apply data saver if enabled
                    if (uiState.isDataSaverEnabled) {
                        it.evaluateJavascript(DATA_SAVER_JS, null)
                    }
                }
            }

            override fun onReceivedError(
                view: WebView?,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                if (request?.isForMainFrame == true) {
                    val errorCode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        error?.errorCode ?: 0
                    } else {
                        0
                    }
                    val isConnError = errorCode == ERROR_HOST_LOOKUP ||
                            errorCode == ERROR_CONNECT ||
                            errorCode == ERROR_TIMEOUT ||
                            errorCode == ERROR_FAILED_SSL_HANDSHAKE

                    onNetworkError(isConnError)
                }
            }
        }

        webView.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView?, newProgress: Int) {
                super.onProgressChanged(view, newProgress)
                onProgressChanged(newProgress)
            }

            override fun onReceivedTitle(view: WebView?, title: String?) {
                super.onReceivedTitle(view, title)
                if (title != null) {
                    onTitleChanged(title)
                }
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: FileChooserParams?
            ): Boolean {
                return onFileChooserRequested(filePathCallback, fileChooserParams)
            }

            override fun onGeolocationPermissionsShowPrompt(
                origin: String?,
                callback: GeolocationPermissions.Callback?
            ) {
                callback?.invoke(origin, true, false)
            }
        }

        // Initial Load
        webView.loadUrl(uiState.speedMode.baseUrl)

        onDispose {
            (webView.parent as? ViewGroup)?.removeView(webView)
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
