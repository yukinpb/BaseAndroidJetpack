package com.flashlight.flashalert.oncall.sms.core.utils

import android.content.Context
import android.content.SharedPreferences
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightMode
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.BlinkMode

private const val PREFS_NAME = "shared_prefs_app"
private const val KEY_FIRST_INSTALL = "is_first_install"
private const val KEY_FIRST_SET_DEFAULT = "is_first_set_default"
private const val KEY_LANGUAGE_CODE = "language_code"
private const val KEY_FLASHLIGHT_MODE = "flashlight_mode"
private const val KEY_STROBE_SPEED = "strobe_speed"
private const val KEY_INCOMING_CALL_FLASH_ENABLED = "incoming_call_flash_enabled"
private const val KEY_INCOMING_CALL_FLASH_SPEED = "incoming_call_flash_speed"
private const val KEY_INCOMING_CALL_BLINK_MODE = "incoming_call_blink_mode"

// SMS Flash settings
private const val KEY_SMS_FLASH_ENABLED = "sms_flash_enabled"
private const val KEY_SMS_FLASH_TIMES = "sms_flash_times"
private const val KEY_SMS_FLASH_SPEED = "sms_flash_speed"

// App Notification Flash Settings
private const val KEY_APP_NOTIFICATION_FLASH_ENABLED = "app_notification_flash_enabled"
private const val KEY_APP_NOTIFICATION_FLASH_TIMES = "app_notification_flash_times"
private const val KEY_APP_NOTIFICATION_FLASH_SPEED = "app_notification_flash_speed"
private const val KEY_SELECTED_APP_PACKAGES = "selected_app_packages"

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

    var incomingCallFlashEnabled: Boolean
        get() = prefs.getBoolean(KEY_INCOMING_CALL_FLASH_ENABLED, false)
        set(value) {
            editor.putBoolean(KEY_INCOMING_CALL_FLASH_ENABLED, value).commit()
            editor.apply()
        }

    var incomingCallFlashSpeed: Float
        get() = prefs.getFloat(KEY_INCOMING_CALL_FLASH_SPEED, 750f)
        set(value) {
            editor.putFloat(KEY_INCOMING_CALL_FLASH_SPEED, value).commit()
            editor.apply()
        }

    var incomingCallBlinkMode: BlinkMode
        get() = try {
            BlinkMode.valueOf(prefs.getString(KEY_INCOMING_CALL_BLINK_MODE, "RHYTHM") ?: "RHYTHM")
        } catch (e: IllegalArgumentException) {
            BlinkMode.RHYTHM
        }
        set(value) {
            editor.putString(KEY_INCOMING_CALL_BLINK_MODE, value.name).commit()
            editor.apply()
        }

    // SMS Flash settings
    var smsFlashEnabled: Boolean
        get() = prefs.getBoolean(KEY_SMS_FLASH_ENABLED, false)
        set(value) {
            editor.putBoolean(KEY_SMS_FLASH_ENABLED, value).commit()
            editor.apply()
        }

    var smsFlashTimes: Int
        get() = prefs.getInt(KEY_SMS_FLASH_TIMES, 2)
        set(value) {
            editor.putInt(KEY_SMS_FLASH_TIMES, value).commit()
            editor.apply()
        }

    var smsFlashSpeed: Float
        get() = prefs.getFloat(KEY_SMS_FLASH_SPEED, 750f)
        set(value) {
            editor.putFloat(KEY_SMS_FLASH_SPEED, value).commit()
            editor.apply()
        }

    // App Notification Flash Settings
    var appNotificationFlashEnabled: Boolean
        get() = prefs.getBoolean(KEY_APP_NOTIFICATION_FLASH_ENABLED, false)
        set(value) {
            editor.putBoolean(KEY_APP_NOTIFICATION_FLASH_ENABLED, value).commit()
            editor.apply()
        }

    var appNotificationFlashTimes: Int
        get() = prefs.getInt(KEY_APP_NOTIFICATION_FLASH_TIMES, 2)
        set(value) {
            editor.putInt(KEY_APP_NOTIFICATION_FLASH_TIMES, value).commit()
            editor.apply()
        }

    var appNotificationFlashSpeed: Float
        get() = prefs.getFloat(KEY_APP_NOTIFICATION_FLASH_SPEED, 750f)
        set(value) {
            editor.putFloat(KEY_APP_NOTIFICATION_FLASH_SPEED, value).commit()
            editor.apply()
        }

    var selectedAppPackages: Set<String>
        get() = prefs.getStringSet(KEY_SELECTED_APP_PACKAGES, emptySet()) ?: emptySet()
        set(value) {
            editor.putStringSet(KEY_SELECTED_APP_PACKAGES, value).commit()
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

