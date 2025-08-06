package com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.core.utils.getFlashlightMode
import com.flashlight.flashalert.oncall.sms.core.utils.saveFlashlightMode
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FlashlightViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(FlashlightScreenState())
    val state: StateFlow<FlashlightScreenState> = _state.asStateFlow()

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isFlashlightOn = false

    init {
        loadSavedState()
        setupCamera()
    }

    private fun loadSavedState() {
        val savedMode = SharedPrefs.getFlashlightMode()
        val savedStrobeSpeed = SharedPrefs.strobeSpeed
        _state.value = _state.value.copy(
            selectedMode = savedMode,
            strobeSpeed = savedStrobeSpeed
        )
    }

    // Starts + stop listening to compass sensors (accelerometer and magnetometer)
    fun startListening() {
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(compassListener, accel, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(compassListener, magnet, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopListening() {
        sensorManager.unregisterListener(compassListener)
    }

    fun onCompassSensorChanged(event: SensorEvent?) {
        when (event?.sensor?.type) {
            Sensor.TYPE_ACCELEROMETER -> accelerometerReading = event.values.clone()
            Sensor.TYPE_MAGNETIC_FIELD -> magnetometerReading = event.values.clone()
        }

        val rotationMatrix = FloatArray(9)
        val success = SensorManager.getRotationMatrix(
            rotationMatrix, null,
            accelerometerReading, magnetometerReading
        )

        if (success) {
            val orientationAngles = FloatArray(3)
            SensorManager.getOrientation(rotationMatrix, orientationAngles)
            val azimuthInRadians = orientationAngles[0]
            val azimuthInDegrees =
                ((Math.toDegrees(azimuthInRadians.toDouble()) + 360) % 360).toFloat()

            viewModelScope.launch {
                _state.value = _state.value.copy(compassAngle = azimuthInDegrees)
            }
        }
    }

    // Setup camera manager and find camera with flash
    private fun setupCamera() {
        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        cameraId = findCameraWithFlash()
    }

    private fun findCameraWithFlash(): String? {
        return cameraManager?.cameraIdList?.find { cameraId ->
            val characteristics = cameraManager?.getCameraCharacteristics(cameraId)
            val flashAvailable =
                characteristics?.get(CameraCharacteristics.FLASH_INFO_AVAILABLE) ?: false
            val facing = characteristics?.get(CameraCharacteristics.LENS_FACING)
            flashAvailable && facing == CameraMetadata.LENS_FACING_BACK
        }
    }

    fun onModeSelected(mode: FlashlightMode) {
        if (_state.value.currentState == FlashlightState.ON) {
            _state.value = _state.value.copy(currentState = FlashlightState.OFF)
            stopFlashlightMode()
        }

        if (_state.value.selectedMode == mode) {
            _state.value = _state.value.copy(
                selectedMode = FlashlightMode.NONE,
                currentState = FlashlightState.OFF
            )
            SharedPrefs.saveFlashlightMode(FlashlightMode.NONE)
        } else {
            _state.value = _state.value.copy(
                selectedMode = mode,
                currentState = FlashlightState.OFF
            )
            SharedPrefs.saveFlashlightMode(mode)
        }
    }

    fun onMainButtonClick() {
        if (_state.value.selectedMode == FlashlightMode.NONE) {
            Toast.makeText(
                context,
                context.getString(R.string.please_select_a_mode_first),
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        when (_state.value.currentState) {
            FlashlightState.OFF -> {
                _state.value = _state.value.copy(currentState = FlashlightState.ON)
                startFlashlightMode()
            }

            FlashlightState.ON -> {
                _state.value = _state.value.copy(currentState = FlashlightState.OFF)
                stopFlashlightMode()
            }
        }
    }

    private fun startFlashlightMode() {
        when (_state.value.selectedMode) {
            FlashlightMode.FLASHLIGHT -> {
                turnOnFlashlight()
            }

            FlashlightMode.SOS -> {
                startSOSMode()
            }

            FlashlightMode.STROBE -> {
                startStrobeMode()
            }

            else -> {}
        }
    }

    private fun stopFlashlightMode() {
        turnOffFlashlight()
    }

    private fun turnOnFlashlight() {
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, true)
                isFlashlightOn = true
            } ?: run {
                Toast.makeText(context, "No camera with flash found", Toast.LENGTH_SHORT)
                    .show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to turn on flashlight: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun turnOffFlashlight() {
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, false)
                isFlashlightOn = false
            }
        } catch (e: Exception) {
            Toast.makeText(context, "Unable to turn off flashlight: ${e.message}", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun startSOSMode() {
        viewModelScope.launch {
            while (_state.value.currentState == FlashlightState.ON &&
                _state.value.selectedMode == FlashlightMode.SOS
            ) {
                for (i in 1..3) {
                    turnOnFlashlight()
                    delay(200)
                    turnOffFlashlight()
                    delay(200)
                }

                for (i in 1..3) {
                    turnOnFlashlight()
                    delay(600)
                    turnOffFlashlight()
                    delay(200)
                }

                for (i in 1..3) {
                    turnOnFlashlight()
                    delay(200)
                    turnOffFlashlight()
                    delay(200)
                }

                delay(1000)
            }
        }
    }

    private fun startStrobeMode() {
        viewModelScope.launch {
            while (_state.value.currentState == FlashlightState.ON &&
                _state.value.selectedMode == FlashlightMode.STROBE
            ) {
                turnOnFlashlight()
                val periodMs = (1000 / _state.value.strobeSpeed).toLong()
                delay(periodMs)
                turnOffFlashlight()
                delay(periodMs)
            }
        }
    }

    fun onStrobeSpeedChanged(speed: Float) {
        _state.value = _state.value.copy(strobeSpeed = speed)
        SharedPrefs.strobeSpeed = speed
    }

    override fun onCleared() {
        super.onCleared()
        // Đảm bảo tắt flashlight khi ViewModel bị destroy
        if (isFlashlightOn) {
            turnOffFlashlight()
        }
    }

    private val compassListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            onCompassSensorChanged(event)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
} 