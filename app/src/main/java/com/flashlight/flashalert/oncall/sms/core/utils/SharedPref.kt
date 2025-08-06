package com.flashlight.flashalert.oncall.sms.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightMode

private const val PREFS_NAME = "shared_prefs_app"
private const val KEY_FIRST_INSTALL = "is_first_install"
private const val KEY_FIRST_SET_DEFAULT = "is_first_set_default"
private const val KEY_LANGUAGE_CODE = "language_code"
private const val KEY_FLASHLIGHT_MODE = "flashlight_mode"
private const val KEY_STROBE_SPEED = "strobe_speed"

object SharedPrefs {
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        val sharedPreferences = context.getSharedPreferences(
            PREFS_NAME,
            Context.MODE_PRIVATE
        )
        prefs = sharedPreferences
        editor = sharedPreferences.edit()
    }

    var isFirstInstall: Boolean
        get() = prefs.getBoolean(KEY_FIRST_INSTALL, true)
        set(z) {
            editor.putBoolean(KEY_FIRST_INSTALL, z).commit()
            editor.apply()
        }

    var isFirstSetDefault: Boolean
        get() = prefs.getBoolean(KEY_FIRST_SET_DEFAULT, true)
        set(value) {
            editor.putBoolean(KEY_FIRST_SET_DEFAULT, value).commit()
            editor.apply()
        }

    var languageCode: String
        get() = prefs.getString(KEY_LANGUAGE_CODE, "") ?: ""
        set(z) {
            editor.putString(KEY_LANGUAGE_CODE, z).commit()
            editor.apply()
        }

    var flashlightMode: String
        get() = prefs.getString(KEY_FLASHLIGHT_MODE, "NONE") ?: "NONE"
        set(value) {
            editor.putString(KEY_FLASHLIGHT_MODE, value).commit()
            editor.apply()
        }

    var strobeSpeed: Float
        get() = prefs.getFloat(KEY_STROBE_SPEED, 10f)
        set(value) {
            editor.putFloat(KEY_STROBE_SPEED, value).commit()
            editor.apply()
        }
}

fun SharedPrefs.saveFlashlightMode(mode: FlashlightMode) {
    flashlightMode = mode.name
}

fun SharedPrefs.getFlashlightMode(): FlashlightMode {
    return try {
        FlashlightMode.valueOf(flashlightMode)
    } catch (e: IllegalArgumentException) {
        FlashlightMode.NONE
    }
}

