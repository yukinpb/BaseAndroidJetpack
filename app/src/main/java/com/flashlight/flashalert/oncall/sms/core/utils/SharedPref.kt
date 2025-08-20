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

// Advanced Settings
private const val KEY_DISABLE_WHEN_PHONE_IN_USE = "disable_when_phone_in_use"
private const val KEY_BATTERY_SAVER_ENABLED = "battery_saver_enabled"
private const val KEY_BATTERY_THRESHOLD = "battery_threshold"
private const val KEY_TIME_TO_FLASH_OFF_ENABLED = "time_to_flash_off_enabled"
private const val KEY_TIME_FROM = "time_from"
private const val KEY_TIME_TO = "time_to"
private const val KEY_ENABLE_FLASH_IN_RINGTONE_MODE = "enable_flash_in_ringtone_mode"
private const val KEY_ENABLE_FLASH_IN_VIBRATE_MODE = "enable_flash_in_vibrate_mode"
private const val KEY_ENABLE_FLASH_IN_MUTE_MODE = "enable_flash_in_mute_mode"

// Settings
private const val KEY_IS_AUTOMATIC_ON = "is_automatic_on"

// LED Screen Settings
private const val KEY_LED_SCREEN_COLOR = "led_screen_color"
private const val KEY_LED_SCREEN_BRIGHTNESS = "led_screen_brightness"

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

    // Advanced Settings
    var disableWhenPhoneInUse: Boolean
        get() = prefs.getBoolean(KEY_DISABLE_WHEN_PHONE_IN_USE, true)
        set(value) {
            editor.putBoolean(KEY_DISABLE_WHEN_PHONE_IN_USE, value).commit()
            editor.apply()
        }

    var batterySaverEnabled: Boolean
        get() = prefs.getBoolean(KEY_BATTERY_SAVER_ENABLED, true)
        set(value) {
            editor.putBoolean(KEY_BATTERY_SAVER_ENABLED, value).commit()
            editor.apply()
        }

    var batteryThreshold: Int
        get() = prefs.getInt(KEY_BATTERY_THRESHOLD, 30)
        set(value) {
            editor.putInt(KEY_BATTERY_THRESHOLD, value).commit()
            editor.apply()
        }

    var timeToFlashOffEnabled: Boolean
        get() = prefs.getBoolean(KEY_TIME_TO_FLASH_OFF_ENABLED, true)
        set(value) {
            editor.putBoolean(KEY_TIME_TO_FLASH_OFF_ENABLED, value).commit()
            editor.apply()
        }

    var timeFrom: String
        get() = prefs.getString(KEY_TIME_FROM, "00:00") ?: "00:00"
        set(value) {
            editor.putString(KEY_TIME_FROM, value).commit()
            editor.apply()
        }

    var timeTo: String
        get() = prefs.getString(KEY_TIME_TO, "00:00") ?: "00:00"
        set(value) {
            editor.putString(KEY_TIME_TO, value).commit()
            editor.apply()
        }

    var enableFlashInRingtoneMode: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_FLASH_IN_RINGTONE_MODE, true)
        set(value) {
            editor.putBoolean(KEY_ENABLE_FLASH_IN_RINGTONE_MODE, value).commit()
            editor.apply()
        }

    var enableFlashInVibrateMode: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_FLASH_IN_VIBRATE_MODE, true)
        set(value) {
            editor.putBoolean(KEY_ENABLE_FLASH_IN_VIBRATE_MODE, value).commit()
            editor.apply()
        }

    var enableFlashInMuteMode: Boolean
        get() = prefs.getBoolean(KEY_ENABLE_FLASH_IN_MUTE_MODE, true)
        set(value) {
            editor.putBoolean(KEY_ENABLE_FLASH_IN_MUTE_MODE, value).commit()
            editor.apply()
        }

    // Settings
    var isAutomaticOn: Boolean
        get() = prefs.getBoolean(KEY_IS_AUTOMATIC_ON, false)
        set(value) {
            editor.putBoolean(KEY_IS_AUTOMATIC_ON, value).commit()
            editor.apply()
        }

    // LED Screen Settings
    var ledScreenColor: String
        get() = prefs.getString(KEY_LED_SCREEN_COLOR, "FFFFFFFF") ?: "FFFFFFFF"
        set(value) {
            editor.putString(KEY_LED_SCREEN_COLOR, value).commit()
            editor.apply()
        }

    var ledScreenBrightness: Float
        get() = prefs.getFloat(KEY_LED_SCREEN_BRIGHTNESS, 1.0f)
        set(value) {
            editor.putFloat(KEY_LED_SCREEN_BRIGHTNESS, value).commit()
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

