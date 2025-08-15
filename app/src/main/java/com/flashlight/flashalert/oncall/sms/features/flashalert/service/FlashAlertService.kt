package com.flashlight.flashalert.oncall.sms.features.flashalert.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.camera2.CameraManager
import android.os.IBinder
import android.telephony.PhoneStateListener
import android.telephony.TelephonyManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.core.utils.AdvancedSettingsChecker
import com.flashlight.flashalert.oncall.sms.core.utils.FlashType
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.BlinkMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class FlashAlertService : Service() {

    companion object {
        private const val TAG = "FlashAlertService"
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "FlashAlertChannel"

        @Volatile
        private var isServiceRunning = false

        fun startService(context: Context) {
            val intent = Intent(context, FlashAlertService::class.java)
            context.startForegroundService(intent)
        }

        fun stopService(context: Context) {
            val intent = Intent(context, FlashAlertService::class.java)
            context.stopService(intent)
        }

        fun isServiceRunning(): Boolean = isServiceRunning
    }

    private var phoneStateListener: PhoneStateListener? = null
    private var telephonyManager: TelephonyManager? = null
    private val advancedSettingsChecker by lazy { AdvancedSettingsChecker(this) }
    private var incomingCallFlashJob: Job? = null
    private var smsFlashJob: Job? = null
    private var appNotificationFlashJob: Job? = null
    private val serviceScope = CoroutineScope(Dispatchers.IO)

    private val smsDetectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "SMS_DETECTED") {
                Log.d(TAG, "SMS detection broadcast received")
                onSmsDetected()
            }
        }
    }

    private val appNotificationDetectionReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "APP_NOTIFICATION_DETECTED") {
                val packageName = intent.getStringExtra("package_name")
                Log.d(TAG, "App notification detection broadcast received for: $packageName")
                onAppNotificationDetected(packageName)
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "FlashAlertService created")
        isServiceRunning = true
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, createNotification())
        startPhoneStateListener()
        registerSmsDetectionReceiver()
        registerAppNotificationDetectionReceiver()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, "FlashAlertService started")
        isServiceRunning = true
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "FlashAlertService destroyed")
        isServiceRunning = false
        stopPhoneStateListener()
        stopAllFlash()
        unregisterSmsDetectionReceiver()
        unregisterAppNotificationDetectionReceiver()
    }

    private fun createNotificationChannel() {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Flash Alert Service",
            NotificationManager.IMPORTANCE_LOW
        ).apply {
            description = "Service để detect incoming calls và SMS, bật flash alert"
            setShowBadge(false)
        }

        val notificationManager =
            getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_flash_light)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun startPhoneStateListener() {
        telephonyManager = getSystemService(TELEPHONY_SERVICE) as TelephonyManager

        phoneStateListener = object : PhoneStateListener() {
            override fun onCallStateChanged(state: Int, phoneNumber: String?) {
                when (state) {
                    TelephonyManager.CALL_STATE_RINGING -> {
                        Log.d(TAG, "Incoming call detected")
                        if (SharedPrefs.incomingCallFlashEnabled && advancedSettingsChecker.shouldFlashBeEnabled(FlashType.INCOMING_CALL)) {
                            startIncomingCallFlash()
                        }
                    }

                    TelephonyManager.CALL_STATE_OFFHOOK -> {
                        Log.d(TAG, "Call answered")
                        stopIncomingCallFlash()
                    }

                    TelephonyManager.CALL_STATE_IDLE -> {
                        Log.d(TAG, "Call ended")
                        stopIncomingCallFlash()
                    }
                }
            }
        }

        telephonyManager?.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE)
        Log.d(TAG, "PhoneStateListener started")
    }

    private fun stopPhoneStateListener() {
        phoneStateListener?.let { listener ->
            telephonyManager?.listen(listener, PhoneStateListener.LISTEN_NONE)
        }
        phoneStateListener = null
        telephonyManager = null
        Log.d(TAG, "PhoneStateListener stopped")
    }

    // Function to be called from SmsNotificationListenerService
    fun onSmsDetected() {
        Log.d(TAG, "SMS detected from notification listener")
        if (SharedPrefs.smsFlashEnabled && advancedSettingsChecker.shouldFlashBeEnabled(FlashType.SMS)) {
            startSmsFlash()
        }
    }

    // Function to be called from AppNotificationListenerService
    fun onAppNotificationDetected(packageName: String?) {
        Log.d(TAG, "App notification detected from notification listener: $packageName")
        if (SharedPrefs.appNotificationFlashEnabled && advancedSettingsChecker.shouldFlashBeEnabled(FlashType.APP_NOTIFICATION)) {
            startAppNotificationFlash()
        }
    }

    private fun startIncomingCallFlash() {
        incomingCallFlashJob?.cancel()
        incomingCallFlashJob = serviceScope.launch {
            try {
                val blinkMode = SharedPrefs.incomingCallBlinkMode
                val flashSpeed = SharedPrefs.incomingCallFlashSpeed

                Log.d(TAG, "Starting incoming call flash with mode: $blinkMode, speed: $flashSpeed")

                when (blinkMode) {
                    BlinkMode.RHYTHM -> flashRhythmPattern(flashSpeed)
                    BlinkMode.CONTINUOUS -> flashContinuousPattern(flashSpeed)
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error starting incoming call flash", e)
            }
        }
    }

    private fun stopIncomingCallFlash() {
        incomingCallFlashJob?.cancel()
        incomingCallFlashJob = null
        Log.d(TAG, "Stopping incoming call flash")

        serviceScope.launch {
            turnOffFlash()
        }
    }

    private fun startSmsFlash() {
        smsFlashJob?.cancel()
        smsFlashJob = serviceScope.launch {
            try {
                val flashTimes = SharedPrefs.smsFlashTimes
                val flashSpeed = SharedPrefs.smsFlashSpeed

                Log.d(TAG, "Starting SMS flash with times: $flashTimes, speed: $flashSpeed")

                flashSmsPattern(flashTimes, flashSpeed)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting SMS flash", e)
            }
        }
    }

    private fun stopSmsFlash() {
        smsFlashJob?.cancel()
        smsFlashJob = null
        Log.d(TAG, "Stopping SMS flash")

        serviceScope.launch {
            turnOffFlash()
        }
    }

    private fun startAppNotificationFlash() {
        appNotificationFlashJob?.cancel()
        appNotificationFlashJob = serviceScope.launch {
            try {
                val flashTimes = SharedPrefs.appNotificationFlashTimes
                val flashSpeed = SharedPrefs.appNotificationFlashSpeed

                Log.d(TAG, "Starting app notification flash with times: $flashTimes, speed: $flashSpeed")

                flashAppNotificationPattern(flashTimes, flashSpeed)
            } catch (e: Exception) {
                Log.e(TAG, "Error starting app notification flash", e)
            }
        }
    }

    private fun stopAppNotificationFlash() {
        appNotificationFlashJob?.cancel()
        appNotificationFlashJob = null
        Log.d(TAG, "Stopping app notification flash")

        serviceScope.launch {
            turnOffFlash()
        }
    }

    private fun stopAllFlash() {
        stopIncomingCallFlash()
        stopSmsFlash()
        stopAppNotificationFlash()
    }

    private suspend fun flashRhythmPattern(speedMs: Float) {
        val cameraManager = getSystemService(CAMERA_SERVICE) as? CameraManager
        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }
                cameraId?.let { id ->
                    while (incomingCallFlashJob?.isActive == true) {
                        val flashDuration = (speedMs / 2).toLong()
                        cm.setTorchMode(id, true)
                        delay(flashDuration)
                        cm.setTorchMode(id, false)
                        delay(flashDuration)
                        cm.setTorchMode(id, true)
                        delay(flashDuration)
                        cm.setTorchMode(id, false)
                        delay(flashDuration)
                        if (incomingCallFlashJob?.isActive != true) break
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in rhythm flash pattern", e)
            }
        }
    }

    private suspend fun flashContinuousPattern(speedMs: Float) {
        val cameraManager = getSystemService(CAMERA_SERVICE) as? CameraManager
        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }
                cameraId?.let { id ->
                    while (incomingCallFlashJob?.isActive == true) {
                        cm.setTorchMode(id, true)
                        delay(speedMs.toLong())
                        cm.setTorchMode(id, false)
                        delay(speedMs.toLong())
                        if (incomingCallFlashJob?.isActive != true) break
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in continuous flash pattern", e)
            }
        }
    }

    private suspend fun flashSmsPattern(times: Int, speedMs: Float) {
        val cameraManager = getSystemService(CAMERA_SERVICE) as? CameraManager
        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }
                cameraId?.let { id ->
                    repeat(times) { time ->
                        if (smsFlashJob?.isActive != true) return@repeat

                        // Flash ON
                        cm.setTorchMode(id, true)
                        delay(speedMs.toLong())

                        // Flash OFF
                        cm.setTorchMode(id, false)
                        if (time < times - 1) {
                            delay(speedMs.toLong())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in SMS flash pattern", e)
            }
        }
    }

    private suspend fun flashAppNotificationPattern(times: Int, speedMs: Float) {
        val cameraManager = getSystemService(CAMERA_SERVICE) as? CameraManager
        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }
                cameraId?.let { id ->
                    repeat(times) { time ->
                        if (appNotificationFlashJob?.isActive != true) return@repeat

                        // Flash ON
                        cm.setTorchMode(id, true)
                        delay(speedMs.toLong())

                        // Flash OFF
                        cm.setTorchMode(id, false)
                        if (time < times - 1) {
                            delay(speedMs.toLong())
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error in app notification flash pattern", e)
            }
        }
    }

    private fun turnOffFlash() {
        val cameraManager = getSystemService(CAMERA_SERVICE) as? CameraManager
        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }
                cameraId?.let { id ->
                    cm.setTorchMode(id, false)
                    Log.d(TAG, "Flash turned off")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error turning off flash", e)
            }
        }
    }

    private fun registerSmsDetectionReceiver() {
        val intentFilter = IntentFilter("SMS_DETECTED")
        registerReceiver(smsDetectionReceiver, intentFilter, RECEIVER_EXPORTED)
        Log.d(TAG, "SMS detection receiver registered")
    }

    private fun unregisterSmsDetectionReceiver() {
        unregisterReceiver(smsDetectionReceiver)
        Log.d(TAG, "SMS detection receiver unregistered")
    }

    private fun registerAppNotificationDetectionReceiver() {
        val intentFilter = IntentFilter("APP_NOTIFICATION_DETECTED")
        registerReceiver(appNotificationDetectionReceiver, intentFilter, RECEIVER_EXPORTED)
        Log.d(TAG, "App notification detection receiver registered")
    }

    private fun unregisterAppNotificationDetectionReceiver() {
        unregisterReceiver(appNotificationDetectionReceiver)
        Log.d(TAG, "App notification detection receiver unregistered")
    }
} 