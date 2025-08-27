package com.flashlight.flashalert.oncall.sms.features.camera.viewmodel

import android.Manifest
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
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
    val isCameraReady: Boolean = false,
    val lastPhotoUri: Uri? = null,
    val showScreenFlash: Boolean = false,
    val isAutomaticUpdateZoom: Boolean = false
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

    private val knownGalleryPackages = listOf(
        "com.google.android.apps.photos",         // Google Photos
        "com.sec.android.gallery3d",              // Samsung Gallery
        "com.miui.gallery",                       // Xiaomi Gallery
        "com.coloros.gallery3d",                  // OPPO Gallery
        "com.vivo.gallery",                       // Vivo Gallery
        "com.sonyericsson.album",                 // Sony Album
        "com.htc.album",                          // HTC Album
        "com.lge.gallery",                        // LG Gallery
        "com.motorola.gallery",                   // Motorola
        "com.asus.gallery"                        // ASUS
    )

    fun handleEvent(event: CameraEvent, isAutomaticUpdateZoom: Boolean = false) {
        when (event) {
            is CameraEvent.ToggleFlash -> {
                _state.value = _state.value.copy(
                    isFlashOn = !_state.value.isFlashOn
                )
                updateFlash()
            }

            is CameraEvent.UpdateZoom -> {
                val clampedZoom = event.zoomLevel.coerceIn(1.0f, 5.0f)
                _state.value = _state.value.copy(
                    currentZoom = clampedZoom,
                    isAutomaticUpdateZoom = isAutomaticUpdateZoom
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
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Below API 33
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            } else {
                // API < 29 (Android 10 and below)
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED &&
                        ContextCompat.checkSelfPermission(
                            context,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
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
            android.util.Log.e("CameraViewModel", "ImageCapture is null")
            return
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // Android 10+: Sử dụng MediaStore API
                takePhotoWithMediaStore()
            } else {
                // Android 9 trở xuống: Sử dụng File API
                takePhotoWithFile()
            }
        } catch (e: Exception) {
            android.util.Log.e("CameraViewModel", "Error taking photo: ${e.message}")
            e.printStackTrace()
        }
    }

    private fun takePhotoWithMediaStore() {
        val contentValues = android.content.ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "Flashlight_${System.currentTimeMillis()}.jpg")
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            context.contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture?.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    android.util.Log.d("CameraViewModel", "Photo saved with MediaStore: ${outputFileResults.savedUri}")
                    
                    // Create flash effect
                    viewModelScope.launch {
                        createFlashEffect()
                    }
                    
                    fetchLastPhotoUri()
                }

                override fun onError(exception: ImageCaptureException) {
                    android.util.Log.e("CameraViewModel", "Error saving photo: ${exception.message}")
                    exception.printStackTrace()
                }
            }
        )
    }

    private fun takePhotoWithFile() {
        // Tạo thư mục nếu chưa tồn tại
        val picturesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
        if (!picturesDir.exists()) {
            val created = picturesDir.mkdirs()
            if (!created) {
                android.util.Log.e("CameraViewModel", "Failed to create Pictures directory")
                return
            }
        }

        val fileName = "Flashlight_${System.currentTimeMillis()}.jpg"
        val photoFile = File(picturesDir, fileName)

        android.util.Log.d("CameraViewModel", "Saving photo to: ${photoFile.absolutePath}")

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture?.takePicture(
            outputOptions,
            cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    android.util.Log.d("CameraViewModel", "Photo saved to file: ${photoFile.absolutePath}")
                    
                    // Create flash effect
                    viewModelScope.launch {
                        createFlashEffect()
                    }

                    // Thông báo MediaStore về ảnh mới (quan trọng cho Android 9)
                    try {
                        // Cách 1: Sử dụng MediaStore.Images.Media.DATA (deprecated nhưng hoạt động trên Android 9)
                        val values = android.content.ContentValues().apply {
                            put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                            put(MediaStore.Images.Media.DATA, photoFile.absolutePath)
                            put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                            put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
                        }

                        var uri = context.contentResolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            values
                        )
                        
                        if (uri != null) {
                            android.util.Log.d("CameraViewModel", "MediaStore updated successfully with DATA: $uri")
                        } else {
                            // Cách 2: Thử sử dụng RELATIVE_PATH (Android 10+ style)
                            android.util.Log.w("CameraViewModel", "First attempt failed, trying RELATIVE_PATH method")
                            val values2 = android.content.ContentValues().apply {
                                put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
                                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                                put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                                put(MediaStore.Images.Media.DATE_MODIFIED, System.currentTimeMillis() / 1000)
                            }
                            
                            uri = context.contentResolver.insert(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                values2
                            )
                            
                            if (uri != null) {
                                android.util.Log.d("CameraViewModel", "MediaStore updated successfully with RELATIVE_PATH: $uri")
                            } else {
                                android.util.Log.e("CameraViewModel", "Both MediaStore update methods failed")
                                
                                                                 // Cách 3: Sử dụng MediaScannerConnection để force scan file
                                 try {
                                     MediaScannerConnection.scanFile(
                                         context,
                                         arrayOf(photoFile.absolutePath),
                                         arrayOf("image/jpeg"),
                                         object : MediaScannerConnection.OnScanCompletedListener {
                                             override fun onScanCompleted(path: String?, uri: Uri?) {
                                                 if (uri != null) {
                                                     android.util.Log.d("CameraViewModel", "MediaScanner completed: $uri")
                                                 } else {
                                                     android.util.Log.e("CameraViewModel", "MediaScanner failed for path: $path")
                                                 }
                                             }
                                         }
                                     )
                                     android.util.Log.d("CameraViewModel", "MediaScanner started")
                                 } catch (e: Exception) {
                                     android.util.Log.e("CameraViewModel", "Failed to start MediaScanner: ${e.message}")
                                 }
                            }
                        }
                    } catch (e: Exception) {
                        android.util.Log.e("CameraViewModel", "Error updating MediaStore: ${e.message}")
                        e.printStackTrace()
                    }

                    fetchLastPhotoUri()
                }

                override fun onError(exception: ImageCaptureException) {
                    android.util.Log.e("CameraViewModel", "Error saving photo: ${exception.message}")
                    exception.printStackTrace()
                }
            }
        )
    }

    fun openGallery(context: Context) {
        try {
            // Try to open the default gallery app directly
            val intent = Intent(Intent.ACTION_MAIN).apply {
                addCategory(Intent.CATEGORY_APP_GALLERY)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: try to open with MediaStore URI
            try {
                val pm = context.packageManager

                for (pkg in knownGalleryPackages) {
                    try {
                        val launchIntent = pm.getLaunchIntentForPackage(pkg)
                        if (launchIntent != null) {
                            context.startActivity(launchIntent)
                            Log.d("GalleryLauncher", "Mở gallery bằng: $pkg")
                            return
                        }
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            } catch (e2: Exception) {
                Toast.makeText(context, "Gallery application not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        cameraExecutor.shutdown()
    }
}
