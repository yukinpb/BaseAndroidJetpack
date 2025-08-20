package com.flashlight.flashalert.oncall.sms.features.splash.viewmodel

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _isFlashOn = MutableStateFlow(false)
    val isFlashOn: StateFlow<Boolean> = _isFlashOn.asStateFlow()

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null

    init {
        setupCamera()
    }

    // Setup camera manager và tìm camera có flash
    private fun setupCamera() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = findCameraWithFlash()
    }

    // Tìm camera có flash
    private fun findCameraWithFlash(): String? {
        return cameraManager?.cameraIdList?.find { cameraId ->
            val characteristics = cameraManager?.getCameraCharacteristics(cameraId)
            val flashAvailable = characteristics?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
            val facing = characteristics?.get(CameraCharacteristics.LENS_FACING)
            flashAvailable && facing == CameraMetadata.LENS_FACING_BACK
        }
    }

    // Bật flash tự động nếu setting cho phép
    fun turnOnFlashlightIfEnabled() {
        if (SharedPrefs.isAutomaticOn) {
            turnOnFlashlight()
        }
    }

    // Bật flash
    private fun turnOnFlashlight() {
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, true)
                _isFlashOn.value = true
                Log.d("SplashViewModel", "Flashlight turned ON automatically")
            } ?: run {
                Log.e("SplashViewModel", "No camera with flash found")
            }
        } catch (e: Exception) {
            Log.e("SplashViewModel", "Unable to turn on flashlight: ${e.message}")
        }
    }

    // Tắt flash
    fun turnOffFlashlight() {
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, false)
                _isFlashOn.value = false
                Log.d("SplashViewModel", "Flashlight turned OFF")
            }
        } catch (e: Exception) {
            Log.e("SplashViewModel", "Unable to turn off flashlight: ${e.message}")
        }
    }

    override fun onCleared() {
        super.onCleared()
        // Đảm bảo tắt flash khi ViewModel bị destroy
        if (_isFlashOn.value) {
            turnOffFlashlight()
        }
    }
}
