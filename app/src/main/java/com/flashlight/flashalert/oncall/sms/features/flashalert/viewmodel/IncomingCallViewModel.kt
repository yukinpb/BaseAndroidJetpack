package com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.camera2.CameraManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.core.utils.Constants
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.features.flashalert.service.FlashAlertService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class BlinkMode(val displayName: String) {
    RHYTHM("Rhythm"),
    CONTINUOUS("Continuous")
}

data class IncomingCallState(
    val isFlashEnabled: Boolean = false,
    val flashSpeed: Float = 750f, // Default to 750ms
    val isLoading: Boolean = false,
    val hasPhonePermission: Boolean = false,
    val blinkMode: BlinkMode = BlinkMode.RHYTHM,
    val isTestRunning: Boolean = false
)

class IncomingCallViewModel : ViewModel() {
    private val _state = MutableStateFlow(IncomingCallState())
    val state: StateFlow<IncomingCallState> = _state.asStateFlow()

    private var testFlashJob: Job? = null

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                isFlashEnabled = SharedPrefs.incomingCallFlashEnabled,
                flashSpeed = SharedPrefs.incomingCallFlashSpeed,
                blinkMode = SharedPrefs.incomingCallBlinkMode
            )
        }
    }
    
    fun checkServiceState(context: Context) {
        val isFlashEnabled = SharedPrefs.incomingCallFlashEnabled
        val isServiceRunning = FlashAlertService.isServiceRunning()
        
        // Nếu flash được enable nhưng service không chạy, tự động start service
        if (isFlashEnabled && !isServiceRunning) {
            FlashAlertService.startService(context)
        }
        
        // Cập nhật state để UI hiển thị đúng
        _state.value = _state.value.copy(isFlashEnabled = isFlashEnabled)
    }

    fun checkPhonePermission(context: Context) {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.READ_PHONE_STATE
        ) == PackageManager.PERMISSION_GRANTED
        
        _state.value = _state.value.copy(hasPhonePermission = hasPermission)
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
    
    fun toggleFlashEnabled(enabled: Boolean, context: Context) {
        viewModelScope.launch {
            if (enabled && !_state.value.hasPhonePermission) {
                // Request permission first
                // This should be handled in the UI layer
                return@launch
            }
            
            SharedPrefs.incomingCallFlashEnabled = enabled
            _state.value = _state.value.copy(isFlashEnabled = enabled)
            
            if (enabled) {
                // Check if service is already running, if not then start it
                if (!FlashAlertService.isServiceRunning()) {
                    FlashAlertService.startService(context)
                }
            } else {
                // Stop background service if no flash features are enabled
                if (!SharedPrefs.smsFlashEnabled && !SharedPrefs.appNotificationFlashEnabled) {
                    FlashAlertService.stopService(context)
                }
            }
        }
    }

    fun updateFlashSpeed(speed: Float) {
        viewModelScope.launch {
            val reversedValue = Constants.MAX_SPEED_FLASH - speed + Constants.MIN_SPEED_FLASH
            SharedPrefs.incomingCallFlashSpeed = reversedValue
            _state.value = _state.value.copy(flashSpeed = speed)
        }
    }

    fun updateBlinkMode(mode: BlinkMode, context: Context) {
        viewModelScope.launch {
            // Tự động dừng flash test khi đổi mode
            if (_state.value.isTestRunning) {
                stopTestFlash(context)
            }
            
            SharedPrefs.incomingCallBlinkMode = mode
            _state.value = _state.value.copy(blinkMode = mode)
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            val defaultSpeed = 750f // 750ms
            SharedPrefs.incomingCallFlashSpeed = defaultSpeed
            _state.value = _state.value.copy(flashSpeed = defaultSpeed)
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
                    when (_state.value.blinkMode) {
                        BlinkMode.RHYTHM -> testFlashRhythm(context, SharedPrefs.incomingCallFlashSpeed)
                        BlinkMode.CONTINUOUS -> testFlashContinue(context, SharedPrefs.incomingCallFlashSpeed)
                    }
                } catch (e: Exception) {
                    // Handle error
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

    private suspend fun testFlashRhythm(context: Context, speedMs: Float) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

        cameraManager?.let { cm ->
            try {
                // Find back camera
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }

                cameraId?.let { id ->
                    while (_state.value.isTestRunning) {
                        // Flash pattern: ON -> OFF -> ON -> OFF
                        val flashDuration = (speedMs / 2).toLong()

                        // First flash
                        cm.setTorchMode(id, true)
                        delay(flashDuration)
                        cm.setTorchMode(id, false)
                        delay(flashDuration)

                        // Second flash
                        cm.setTorchMode(id, true)
                        delay(flashDuration)
                        cm.setTorchMode(id, false)
                        delay(flashDuration)

                        // Check if test is still running
                        if (!_state.value.isTestRunning) break
                    }
                }
            } catch (e: Exception) {
                // Handle camera error
                e.printStackTrace()
            }
        }
    }

    private suspend fun testFlashContinue(context: Context, speedMs: Float) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager

        cameraManager?.let { cm ->
            try {
                // Find back camera
                val cameraId = cm.cameraIdList.find { id ->
                    val characteristics = cm.getCameraCharacteristics(id)
                    val facing =
                        characteristics.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
                    facing == android.hardware.camera2.CameraCharacteristics.LENS_FACING_BACK
                }

                cameraId?.let { id ->
                    while (_state.value.isTestRunning) {
                        // Continuous flash: ON -> OFF
                        cm.setTorchMode(id, true)
                        delay(speedMs.toLong())
                        cm.setTorchMode(id, false)
                        delay(speedMs.toLong())

                        // Check if test is still running
                        if (!_state.value.isTestRunning) break
                    }
                }
            } catch (e: Exception) {
                // Handle camera error
                e.printStackTrace()
            }
        }
    }

    private fun turnOffFlash(context: Context) {
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as? CameraManager
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
                }
            } catch (e: Exception) { /* Handle error */
            }
        }
    }
} 