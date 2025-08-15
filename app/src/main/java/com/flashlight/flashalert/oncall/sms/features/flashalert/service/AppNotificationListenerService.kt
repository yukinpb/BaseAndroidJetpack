package com.flashlight.flashalert.oncall.sms.features.flashalert.service

import android.content.Intent
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.core.utils.AdvancedSettingsChecker
import com.flashlight.flashalert.oncall.sms.core.utils.FlashType

class AppNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "AppNotificationListener"
        private var isServiceRunning = false

        fun isServiceRunning(): Boolean = isServiceRunning
    }

    private val advancedSettingsChecker by lazy { AdvancedSettingsChecker(this) }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "AppNotificationListenerService created")
        isServiceRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "AppNotificationListenerService destroyed")
        isServiceRunning = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        Log.d(TAG, "Selected app notification detected from: ${sbn.packageName}")

        // Check if this is a notification from a selected app
        if (isSelectedAppNotification(sbn)) {
            Log.d(TAG, "Selected app notification detected from: ${sbn.packageName}")
            if (SharedPrefs.appNotificationFlashEnabled && advancedSettingsChecker.shouldFlashBeEnabled(FlashType.APP_NOTIFICATION)) {
                // Notify the main FlashAlertService about app notification detection
                notifyMainService(sbn.packageName)
            } else {
                Log.d(TAG, "Flash disabled for app notification due to advanced settings")
            }
        }
    }

    private fun isSelectedAppNotification(sbn: StatusBarNotification): Boolean {
        val packageName = sbn.packageName
        val selectedPackages = SharedPrefs.selectedAppPackages

        // Check if this notification is from a selected app
        if (selectedPackages.contains(packageName)) {
            Log.d(TAG, "Notification from selected app: $packageName")
            return true
        }

        Log.d(TAG, "Not a selected app notification from: $packageName")
        return false
    }

    private fun notifyMainService(packageName: String) {
        try {
            // Send broadcast to notify main service with package name
            val intent = Intent("APP_NOTIFICATION_DETECTED")
            intent.putExtra("package_name", packageName)
            sendBroadcast(intent)
            Log.d(TAG, "App notification detection broadcast sent for: $packageName")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending app notification detection broadcast", e)
        }
    }
} 