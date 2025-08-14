package com.flashlight.flashalert.oncall.sms.features.flashalert.service

import android.content.Intent
import android.provider.Telephony
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs

class SmsNotificationListenerService : NotificationListenerService() {

    companion object {
        private const val TAG = "SmsNotificationListener"
        private var isServiceRunning = false

        fun isServiceRunning(): Boolean = isServiceRunning
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "SmsNotificationListenerService created")
        isServiceRunning = true
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "SmsNotificationListenerService destroyed")
        isServiceRunning = false
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)

        // Check if this is an SMS notification
        if (isSmsNotification(sbn)) {
            Log.d(TAG, "SMS notification detected")
            if (SharedPrefs.smsFlashEnabled) {
                // Notify the main FlashAlertService about SMS detection
                notifyMainService()
            }
        }
    }

    private fun isSmsNotification(sbn: StatusBarNotification): Boolean {
        val packageName = sbn.packageName

        // Get the default SMS app package name
        val defaultSmsApp = Telephony.Sms.getDefaultSmsPackage(this)

        // Check if this notification is from the default SMS app
        if (defaultSmsApp != null && packageName == defaultSmsApp) {
            Log.d(TAG, "SMS notification from default SMS app: $defaultSmsApp")
            return true
        }

        // Additional check for common SMS apps if default app is not set
        if (defaultSmsApp == null) {
            val commonSmsApps = listOf(
                "com.android.mms",                    // AOSP Messages
                "com.google.android.apps.messaging",  // Google Messages
                "com.samsung.android.messaging",      // Samsung Messages
                "com.oneplus.mms",                    // OnePlus Messages
                "com.miui.mms",                       // Xiaomi Messages
                "com.huawei.mms",                     // Huawei Messages
                "com.oppo.mms",                       // OPPO Messages
                "com.vivo.mms"                        // VIVO Messages
            )

            if (packageName in commonSmsApps) {
                Log.d(TAG, "SMS notification from common SMS app: $packageName")
                return true
            }
        }

        Log.d(TAG, "Not an SMS notification from: $packageName")
        return false
    }

    private fun notifyMainService() {
        try {
            // Send broadcast to notify main service
            val intent = Intent("SMS_DETECTED")
            sendBroadcast(intent)
            Log.d(TAG, "SMS detection broadcast sent")
        } catch (e: Exception) {
            Log.e(TAG, "Error sending SMS detection broadcast", e)
        }
    }
} 