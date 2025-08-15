package com.flashlight.flashalert.oncall.sms.core.utils

import android.content.Context
import android.media.AudioManager
import android.os.BatteryManager
import android.os.PowerManager
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Enum for different types of flash alerts
 */
enum class FlashType(val displayName: String) {
    INCOMING_CALL("Incoming Call"),
    SMS("SMS"),
    APP_NOTIFICATION("App Notification")
}

/**
 * Utility class to check advanced settings for flash alerts
 * Centralizes all the checking logic to avoid duplication across services
 */
class AdvancedSettingsChecker(private val context: Context) {
    
    companion object {
        private const val TAG = "AdvancedSettingsChecker"
    }

    private val powerManager by lazy {
        context.getSystemService(Context.POWER_SERVICE) as PowerManager
    }
    
    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    /**
     * Check if flash should be enabled based on all advanced settings
     * @param flashType Type of flash for logging purposes
     * @return true if flash should be enabled, false otherwise
     */
    fun shouldFlashBeEnabled(flashType: FlashType): Boolean {
        // Check if phone is in use and flash should be disabled
        if (SharedPrefs.disableWhenPhoneInUse && isPhoneInUse()) {
            Log.d(TAG, "Flash disabled for ${flashType.displayName}: Phone is in use (screen is on)")
            return false
        }

        // Check battery saver
        if (SharedPrefs.batterySaverEnabled && isBatteryLow()) {
            Log.d(TAG, "Flash disabled for ${flashType.displayName}: Battery is low (${getCurrentBatteryLevel()}%)")
            return false
        }

        // Check time restrictions
        if (SharedPrefs.timeToFlashOffEnabled && isInRestrictedTime()) {
            Log.d(TAG, "Flash disabled for ${flashType.displayName}: Currently in restricted time period")
            return false
        }

        // Check sound mode restrictions
        if (!isFlashAllowedInCurrentSoundMode()) {
            Log.d(TAG, "Flash disabled for ${flashType.displayName}: Not allowed in current sound mode")
            return false
        }

        Log.d(TAG, "Flash enabled for ${flashType.displayName}: All conditions met")
        return true
    }

    /**
     * Check if phone is currently in use (screen is unlocked/on)
     */
    private fun isPhoneInUse(): Boolean {
        return powerManager.isInteractive
    }

    /**
     * Check if battery is low based on threshold
     */
    private fun isBatteryLow(): Boolean {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        val batteryLevel = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
        return batteryLevel <= SharedPrefs.batteryThreshold
    }

    /**
     * Get current battery level for logging
     */
    private fun getCurrentBatteryLevel(): Int {
        val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
    }

    /**
     * Check if current time is in restricted period
     */
    private fun isInRestrictedTime(): Boolean {
        return try {
            val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
            val fromTime = SharedPrefs.timeFrom
            val toTime = SharedPrefs.timeTo

            // If from and to are the same, no restriction
            if (fromTime == toTime) return false

            val current = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(currentTime)
            val from = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(fromTime)
            val to = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(toTime)

            if (current != null && from != null && to != null) {
                // Handle time ranges that cross midnight
                return if (from.before(to)) {
                    // Normal range (e.g., 09:00 to 18:00)
                    current.after(from) && current.before(to)
                } else {
                    // Crosses midnight (e.g., 22:00 to 06:00)
                    current.after(from) || current.before(to)
                }
            }
            false
        } catch (e: Exception) {
            Log.e(TAG, "Error checking time restrictions", e)
            false
        }
    }

    /**
     * Check if flash is allowed in current sound mode
     */
    private fun isFlashAllowedInCurrentSoundMode(): Boolean {
        return when (audioManager.ringerMode) {
            AudioManager.RINGER_MODE_NORMAL -> {
                SharedPrefs.enableFlashInRingtoneMode
            }
            AudioManager.RINGER_MODE_VIBRATE -> {
                SharedPrefs.enableFlashInVibrateMode
            }
            AudioManager.RINGER_MODE_SILENT -> {
                SharedPrefs.enableFlashInMuteMode
            }
            else -> true // Default to true if unknown mode
        }
    }
}
