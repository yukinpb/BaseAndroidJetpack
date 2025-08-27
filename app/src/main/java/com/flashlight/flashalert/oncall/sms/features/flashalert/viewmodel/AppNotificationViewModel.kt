package com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.core.utils.Constants
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.features.flashalert.service.AppNotificationListenerService
import com.flashlight.flashalert.oncall.sms.features.flashalert.service.FlashAlertService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppNotificationState(
    val isFlashEnabled: Boolean = false,
    val flashTimes: Int = 2,
    val flashSpeed: Float = 750f,
    val isLoading: Boolean = false,
    val isTestRunning: Boolean = false,
    val showPermissionDialog: Boolean = false,
    val hasNotificationPermission: Boolean = false,
    val hasOpenedSettings: Boolean = false
)

class AppNotificationViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppNotificationState())
    val state: StateFlow<AppNotificationState> = _state.asStateFlow()

    private var testFlashJob: Job? = null

    init {
        loadSettings()
    }

    fun loadSettings(context: Context? = null) {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isFlashEnabled = SharedPrefs.appNotificationFlashEnabled,
                flashTimes = SharedPrefs.appNotificationFlashTimes,
                flashSpeed = SharedPrefs.appNotificationFlashSpeed
            )
        }
    }

    fun toggleFlashEnabled(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            if (enabled) {
                // Check if notification permission is already granted
                val hasPermission = isNotificationListenerEnabled(context)
                if (hasPermission) {
                    // Permission already granted, enable flash directly
                    SharedPrefs.appNotificationFlashEnabled = true
                    _state.value = _state.value.copy(
                        isFlashEnabled = true,
                        hasNotificationPermission = true
                    )
                    // Start services
                    startNotificationListenerService(context)
                } else {
                    // Show permission dialog
                    _state.value = _state.value.copy(showPermissionDialog = true)
                }
            } else {
                SharedPrefs.appNotificationFlashEnabled = false
                _state.value = _state.value.copy(isFlashEnabled = false)

                // Stop services if no flash features are enabled
                if (!SharedPrefs.incomingCallFlashEnabled && !SharedPrefs.smsFlashEnabled) {
                    FlashAlertService.stopService(context)
                }
            }
        }
    }

    fun hidePermissionDialog() {
        _state.value = _state.value.copy(showPermissionDialog = false)
    }

    fun enableFlashAfterPermission(context: Context) {
        viewModelScope.launch {
            // Only check permission if user has actually opened settings
            if (!_state.value.hasOpenedSettings) {
                return@launch
            }

            // Check if permission was actually granted after returning from settings
            val hasPermission = isNotificationListenerEnabled(context)
            if (hasPermission) {
                // Permission granted, enable flash
                SharedPrefs.appNotificationFlashEnabled = true
                _state.value = _state.value.copy(
                    isFlashEnabled = true,
                    showPermissionDialog = false,
                    hasNotificationPermission = true,
                    hasOpenedSettings = false  // Reset flag after checking
                )
                // Start services
                startNotificationListenerService(context)
            } else {
                // Permission not granted, keep dialog open or show error
                _state.value = _state.value.copy(
                    showPermissionDialog = false,
                    hasNotificationPermission = false,
                    hasOpenedSettings = false  // Reset flag after checking
                )
                // You might want to show a message that permission is required
            }
        }
    }

    fun openNotificationAccessSettings(context: Context) {
        try {
            _state.value = _state.value.copy(hasOpenedSettings = true)
            val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun checkNotificationPermission(context: Context) {
        val hasPermission = isNotificationListenerEnabled(context)
        _state.value = _state.value.copy(hasNotificationPermission = hasPermission)

        // If permission granted and flash is enabled, start both services
        if (hasPermission && _state.value.isFlashEnabled) {
            startNotificationListenerService(context)
        }
    }

    fun hasNotificationPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED
        } else {
            // Android 12 trở xuống không cần quyền notification
            true
        }
    }

    private fun isNotificationListenerEnabled(context: Context): Boolean {
        return NotificationManagerCompat.getEnabledListenerPackages(context)
            .contains(context.packageName)
    }

    private fun startNotificationListenerService(context: Context) {
        try {
            // Start the main FlashAlertService if not running
            if (!FlashAlertService.isServiceRunning()) {
                FlashAlertService.startService(context)
            }

            // Start the App notification listener service
            val intent = Intent(context, AppNotificationListenerService::class.java)
            context.startService(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun updateFlashTimes(times: Int) {
        viewModelScope.launch {
            SharedPrefs.appNotificationFlashTimes = times
            _state.value = _state.value.copy(flashTimes = times)
        }
    }

    fun updateFlashSpeed(speed: Float) {
        viewModelScope.launch {
            val reversedValue = Constants.MAX_SPEED_FLASH - speed + Constants.MIN_SPEED_FLASH
            SharedPrefs.appNotificationFlashSpeed = reversedValue
            _state.value = _state.value.copy(flashSpeed = speed)
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            val defaultSpeed = 750f // 750ms
            val defaultTimes = 2
            SharedPrefs.appNotificationFlashSpeed = defaultSpeed
            SharedPrefs.appNotificationFlashTimes = defaultTimes
            _state.value = _state.value.copy(
                flashSpeed = defaultSpeed,
                flashTimes = defaultTimes
            )
        }
    }

    fun testFlashSettings(context: Context) {
        if (_state.value.isTestRunning) {
            stopTestFlash(context)
        } else {
            startTestFlash(context)
        }
    }

    private fun startTestFlash(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isTestRunning = true)

            testFlashJob = viewModelScope.launch {
                try {
                    testFlashPattern(context, _state.value.flashTimes, SharedPrefs.appNotificationFlashSpeed)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun stopTestFlash(context: Context) {
        testFlashJob?.cancel()
        testFlashJob = null
        _state.value = _state.value.copy(isTestRunning = false)

        // Turn off flash
        viewModelScope.launch {
            turnOffFlash(context)
        }
    }

    private suspend fun testFlashPattern(context: Context, times: Int, speedMs: Float) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing = characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }

                cameraId?.let { id ->
                    repeat(times) { time ->
                        if (!_state.value.isTestRunning) return@repeat
                        
                        // Flash ON
                        cm.setTorchMode(id, true)
                        delay(speedMs.toLong())
                        
                        // Flash OFF
                        cm.setTorchMode(id, false)
                        if (time < times - 1) { // Không delay sau lần cuối
                            delay(speedMs.toLong())
                        }
                    }
                    
                    // Flash pattern completed, reset state
                    _state.value = _state.value.copy(isTestRunning = false)
                }
            } catch (e: Exception) {
                e.printStackTrace()
                // Reset state even if there's an error
                _state.value = _state.value.copy(isTestRunning = false)
            }
        } ?: run {
            // Camera manager not available, reset state
            _state.value = _state.value.copy(isTestRunning = false)
        }
    }

    private fun turnOffFlash(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
        cameraManager?.let { cm ->
            try {
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing = characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }
                cameraId?.let { id ->
                    cm.setTorchMode(id, false)
                }
            } catch (e: Exception) {
                // Handle error
                e.printStackTrace()
            }
        }
    }
} 