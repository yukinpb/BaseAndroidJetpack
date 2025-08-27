package com.flashlight.flashalert.oncall.sms.features.compass.viewmodel

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.camera2.CameraManager
import android.os.Looper
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CompassScreenState(
    val compass: CompassState = CompassState()
)

@HiltViewModel
class CompassViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CompassScreenState())
    val state: StateFlow<CompassScreenState> = _state.asStateFlow()

    private val fusedLocationClient: FusedLocationProviderClient =
        LocationServices.getFusedLocationProviderClient(context)
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private var cameraManager: CameraManager? = null
    private var cameraId: String? = null
    private var isFlashlightOn = false

    private var accelerometerReading = FloatArray(3)
    private var magnetometerReading = FloatArray(3)

    fun startListening() {
        // Setup camera
        setupCamera()

        // Start location updates
        startLocationUpdates()

        // Start compass sensor
        val accel = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnet = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)
        sensorManager.registerListener(compassListener, accel, SensorManager.SENSOR_DELAY_UI)
        sensorManager.registerListener(compassListener, magnet, SensorManager.SENSOR_DELAY_UI)
    }

    fun stopListening() {
        sensorManager.unregisterListener(compassListener)
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }

    fun startLocationUpdates() {
        try {
            val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
                .setMinUpdateIntervalMillis(5000L)
                .build()

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        } catch (e: SecurityException) {
            // Handle permission not granted
            e.printStackTrace()
        }
    }

    fun checkAndRequestLocationSettings(activity: Activity) {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000L)
            .setMinUpdateIntervalMillis(5000L)
            .build()

        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
            .setAlwaysShow(true)

        val client = LocationServices.getSettingsClient(activity)
        val task = client.checkLocationSettings(builder.build())

        task.addOnSuccessListener { locationSettingsResponse ->
            // GPS is enabled, start location updates
            startLocationUpdates()
        }.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                try {
                    // Show the dialog by calling startResolutionForResult
                    exception.startResolutionForResult(activity, LOCATION_SETTINGS_REQUEST_CODE)
                } catch (sendEx: Exception) {
                    // Ignore the error
                }
            }
        }
    }

    companion object {
        const val LOCATION_SETTINGS_REQUEST_CODE = 1001
    }

    private val locationCallback = object : com.google.android.gms.location.LocationCallback() {
        override fun onLocationResult(locationResult: com.google.android.gms.location.LocationResult) {
            locationResult.lastLocation?.let { location ->
                val latLng = LatLng(location.latitude, location.longitude)
                viewModelScope.launch {
                    _state.value = _state.value.copy(
                        compass = _state.value.compass.copy(
                            currentLocation = latLng,
                            cameraPosition = CameraPosition.fromLatLngZoom(latLng, 18f)
                        )
                    )
                }
            }
        }
    }

    fun toggleDirection() {
        viewModelScope.launch {
            _state.value = _state.value.copy(
                compass = _state.value.compass.copy(
                    isDirectionEnabled = !_state.value.compass.isDirectionEnabled
                )
            )
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
                characteristics?.get(android.hardware.camera2.CameraCharacteristics.FLASH_INFO_AVAILABLE)
                    ?: false
            val facing =
                characteristics?.get(android.hardware.camera2.CameraCharacteristics.LENS_FACING)
            flashAvailable && facing == android.hardware.camera2.CameraMetadata.LENS_FACING_BACK
        }
    }

    fun toggleFlash() {
        viewModelScope.launch {
            val newFlashState = !_state.value.compass.isFlashEnabled
            _state.value = _state.value.copy(
                compass = _state.value.compass.copy(isFlashEnabled = newFlashState)
            )

            if (newFlashState) {
                turnOnFlashlight()
            } else {
                turnOffFlashlight()
            }
        }
    }

    private fun turnOnFlashlight() {
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, true)
                isFlashlightOn = true
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun turnOffFlashlight() {
        try {
            cameraId?.let { id ->
                cameraManager?.setTorchMode(id, false)
                isFlashlightOn = false
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun onCompassSensorChanged(event: SensorEvent?) {
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
                _state.value = _state.value.copy(
                    compass = _state.value.compass.copy(angle = azimuthInDegrees)
                )
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        if (isFlashlightOn) {
            turnOffFlashlight()
        }
        stopListening()
    }

    private val compassListener = object : SensorEventListener {
        override fun onSensorChanged(event: SensorEvent?) {
            onCompassSensorChanged(event)
        }

        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
    }
}