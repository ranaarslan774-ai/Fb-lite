package com.example.model

enum class FbSpeedMode(
    val title: String,
    val subtitle: String,
    val baseUrl: String,
    val iconName: String
) {
    STANDARD(
        title = "Standard Mobile",
        subtitle = "Full mobile UI with stories and reactions",
        baseUrl = "https://m.facebook.com",
        iconName = "standard"
    ),
    TOUCH(
        title = "Touch Lite",
        subtitle = "Lightweight touch interface with quick load",
        baseUrl = "https://touch.facebook.com",
        iconName = "touch"
    ),
    SUPER_LITE(
        title = "Super-Lite Basic",
        subtitle = "Ultra-fast text-first mode for 2G / weak networks",
        baseUrl = "https://mbasic.facebook.com",
        iconName = "lite"
    );

    fun getTargetUrl(path: String = ""): String {
        val cleanPath = if (path.startsWith("/")) path else if (path.isNotEmpty()) "/$path" else ""
        return "$baseUrl$cleanPath"
    }
}
