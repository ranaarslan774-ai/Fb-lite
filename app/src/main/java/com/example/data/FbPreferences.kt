package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.model.FbSpeedMode

class FbPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("fb_lite_preferences", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_SPEED_MODE = "key_speed_mode"
        private const val KEY_DATA_SAVER = "key_data_saver"
        private const val KEY_NIGHT_MODE = "key_night_mode"
        private const val KEY_DESKTOP_MODE = "key_desktop_mode"
        private const val KEY_TEXT_ZOOM = "key_text_zoom"
    }

    var speedMode: FbSpeedMode
        get() {
            val name = prefs.getString(KEY_SPEED_MODE, FbSpeedMode.STANDARD.name)
            return try {
                FbSpeedMode.valueOf(name ?: FbSpeedMode.STANDARD.name)
            } catch (e: Exception) {
                FbSpeedMode.STANDARD
            }
        }
        set(value) {
            prefs.edit().putString(KEY_SPEED_MODE, value.name).apply()
        }

    var isDataSaverEnabled: Boolean
        get() = prefs.getBoolean(KEY_DATA_SAVER, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DATA_SAVER, value).apply()
        }

    var isNightModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_NIGHT_MODE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_NIGHT_MODE, value).apply()
        }

    var isDesktopModeEnabled: Boolean
        get() = prefs.getBoolean(KEY_DESKTOP_MODE, false)
        set(value) {
            prefs.edit().putBoolean(KEY_DESKTOP_MODE, value).apply()
        }

    var textZoom: Int
        get() = prefs.getInt(KEY_TEXT_ZOOM, 100)
        set(value) {
            prefs.edit().putInt(KEY_TEXT_ZOOM, value).apply()
        }
}
