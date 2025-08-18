package com.flashlight.flashalert.oncall.sms.features.camera.viewmodel

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import javax.inject.Inject

data class CameraState(
    val isFlashOn: Boolean = false,
    val currentZoom: Float = 1f,
    val availableZoomLevels: List<Float> = (5..20).map { it * 0.2f }, // 1.0, 1.2, 1.4, ..., 4.0
    val isCameraReady: Boolean = false,
    val lastPhotoUri: Uri? = null,
    val showScreenFlash: Boolean = false
)

sealed class CameraEvent {
    object ToggleFlash : CameraEvent()
    data class UpdateZoom(val zoomLevel: Float) : CameraEvent()
    object TakePhoto : CameraEvent()
}

@HiltViewModel
class CameraViewModel @Inject constructor(
    @ApplicationContext private val context: Context
) : ViewModel() {

    private val _state = MutableStateFlow(CameraState())
    val state: StateFlow<CameraState> = _state.asStateFlow()

    // CameraX components
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var cameraProvider: ProcessCameraProvider? = null
    private lateinit var cameraExecutor: ExecutorService

    fun handleEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.ToggleFlash -> {
                _state.value = _state.value.copy(
                    isFlashOn = !_state.value.isFlashOn
                )
                updateFlash()
            }

            is CameraEvent.UpdateZoom -> {
                val clampedZoom = event.zoomLevel.coerceIn(1.0f, 4.0f)
                _state.value = _state.value.copy(
                    currentZoom = clampedZoom
                )
                updateZoom()
            }

            is CameraEvent.TakePhoto -> {
                takePhoto()
            }
        }
    }

    fun fetchLastPhotoUri() {
        viewModelScope.launch(Dispatchers.IO) {
            val uri = queryLatestImageUri()
            withContext(Dispatchers.Main) {
                _state.value = _state.value.copy(lastPhotoUri = uri)
            }
        }
    }

    private fun queryLatestImageUri(): Uri? {
        val cr = context.contentResolver
        val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

        try {
            // Check storage permissions based on API level
            val hasStoragePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                // API 33+ (Android 13+)
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                // Below API 33
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            }

            if (!hasStoragePermission) {
                return null
            }

            // Simple query to get the latest image
            val projection = arrayOf(MediaStore.Images.Media._ID)
            val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"

            cr.query(collection, projection, null, null, sortOrder)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                    val id = cursor.getLong(idColumn)
                    return ContentUris.withAppendedId(collection, id)
                }
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun setupCamera(context: Context, previewView: androidx.camera.view.PreviewView) {
        cameraExecutor = Executors.newSingleThreadExecutor()

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        cameraProviderFuture.addListener({
            try {
                cameraProvider = cameraProviderFuture.get()

                val preview = Preview.Builder().build().also {
                    it.surfaceProvider = previewView.surfaceProvider
                }
                imageCapture = ImageCapture.Builder().build()

                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                cameraProvider?.unbindAll()
                camera = cameraProvider?.bindToLifecycle(
                    context as androidx.lifecycle.LifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )

                _state.value = _state.value.copy(isCameraReady = true)
                updateFlash()
                updateZoom()

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(context))
    }

    fun pauseCamera() {
        cameraProvider?.unbindAll()
        _state.value = _state.value.copy(isCameraReady = false)
    }

    private fun updateFlash() {
        camera?.cameraControl?.enableTorch(_state.value.isFlashOn)
    }

    private fun updateZoom() {
        camera?.cameraControl?.setZoomRatio(_state.value.currentZoom)
    }

    private suspend fun createFlashEffect() {
        try {
            // Show screen flash
            _state.value = _state.value.copy(showScreenFlash = true)
            delay(100) // Flash for 100ms

            // Hide screen flash
            _state.value = _state.value.copy(showScreenFlash = false)
            delay(100) // Wait 100ms

            // Show screen flash again
            _state.value = _state.value.copy(showScreenFlash = true)
            delay(100) // Flash for 100ms

            // Hide screen flash
            _state.value = _state.value.copy(showScreenFlash = false)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun takePhoto() {

        val imageCapture = imageCapture
        if (imageCapture == null) {
            return
        }

        // Create file in Pictures directory (gallery)
        val picturesDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!picturesDir.exists()) {
            picturesDir.mkdirs()
        }

        val fileName = "Flashlight_${System.currentTimeMillis()}.jpg"
        val photoFile = File(picturesDir, fileName)

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // Create flash effect to notify user that photo was taken
                    viewModelScope.launch {
                        createFlashEffect()
                    }

                    // Notify MediaStore about the new image so it appears in gallery
                    try {
                        val values = android.content.ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(
                                MediaStore.Images.Media.RELATIVE_PATH,
                                Environment.DIRECTORY_PICTURES
                            )
                        }

                        context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }

                    fetchLastPhotoUri()
                }

                override fun onError(exception: ImageCaptureException) {
                    exception.printStackTrace()
                }
            }
        )
    }

    fun openGallery(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        context.startActivity(intent)
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}
