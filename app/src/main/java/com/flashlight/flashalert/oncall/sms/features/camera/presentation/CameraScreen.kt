package com.flashlight.flashalert.oncall.sms.features.camera.presentation

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import coil3.compose.AsyncImage
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.camera.presentation.components.FlashButtonComponent
import com.flashlight.flashalert.oncall.sms.features.camera.presentation.components.ZoomSliderComponent
import com.flashlight.flashalert.oncall.sms.features.camera.viewmodel.CameraEvent
import com.flashlight.flashalert.oncall.sms.features.camera.viewmodel.CameraViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.AdsUtils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun CameraScreen(
    modifier: Modifier = Modifier,
    viewModel: CameraViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val activity = LocalActivity.current

    // Permission launcher for storage permissions
    val storagePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // All permissions granted, fetch last photo
            viewModel.fetchLastPhotoUri()
        }
    }

    // Check and request permissions when entering screen
    LaunchedEffect(Unit) {
        val permissionsToRequest = mutableListOf<String>()

        // Check camera permission
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            // Camera permission will be handled by the AndroidView update callback
            return@LaunchedEffect
        }
        
        // Check storage permissions based on API level
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // API 33+ (Android 13+)
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_MEDIA_IMAGES) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // Below API 33
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) 
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        } else {
            // Below API 29
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }

        // Request permissions if needed
        if (permissionsToRequest.isNotEmpty()) {
            storagePermissionLauncher.launch(permissionsToRequest.toTypedArray())
        } else {
            // All permissions already granted, fetch last photo
            viewModel.fetchLastPhotoUri()
        }
    }

    // Cleanup camera when leaving screen
    DisposableEffect(lifecycleOwner) {
        onDispose {
            viewModel.pauseCamera()
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        // Camera Preview
        AndroidView(
            factory = { context ->
                PreviewView(context).apply {
                    this.scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTransformGestures { _, pan, zoom, _ ->
                        // Handle pinch to zoom
                        if (zoom != 1f) {
                            val newZoom = state.currentZoom * zoom
                            viewModel.handleEvent(CameraEvent.UpdateZoom(newZoom), isAutomaticUpdateZoom = true)
                        }
                    }
                },
            update = { previewView ->
                // Setup camera when PreviewView is created and permission is granted
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.CAMERA
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    viewModel.setupCamera(context, previewView)
                }
            }
        )

        // Screen flash overlay
        if (state.showScreenFlash) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            )
        }

        Box(
            modifier = modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.TopStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithoutIndication {
                            if (activity != null) {
                                AdsUtils.loadAndDisplayInter(
                                    context = activity,
                                    adUnitId = activity.getString(R.string.inter_inapp),
                                    onNextAction = {
                                        navigator.popBackStack()
                                    }
                                )
                            } else {
                                navigator.popBackStack()
                            }
                        }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.camera),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )
            }

            // Camera controls overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .wrapContentHeight()
                    .fillMaxWidth()
            ) {
                // Zoom slider component
                ZoomSliderComponent(
                    currentZoom = state.currentZoom,
                    onZoomChanged = { zoomLevel ->
                        viewModel.handleEvent(CameraEvent.UpdateZoom(zoomLevel))
                    },
                    isAutomaticUpdateZoom = state.isAutomaticUpdateZoom
                )

                Spacer(modifier = Modifier.height(12.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = Color(0xFF2D2D2D).copy(alpha = 0.6f),
                            shape = RectangleShape
                        )
                ) {
                    // Flash button component
                    FlashButtonComponent(
                        isFlashOn = state.isFlashOn,
                        onToggle = {
                            viewModel.handleEvent(CameraEvent.ToggleFlash)
                        },
                        modifier = Modifier
                            .padding(16.dp)
                            .align(Alignment.CenterStart)
                    )

                    Image(
                        painter = painterResource(id = R.drawable.btn_capture),
                        contentDescription = "Take Photo",
                        modifier = Modifier
                            .padding(vertical = 24.dp)
                            .size(58.dp)
                            .clickableWithoutIndication {
                                viewModel.handleEvent(CameraEvent.TakePhoto)
                            }
                            .align(Alignment.Center),
                        contentScale = ContentScale.Fit
                    )

                    if (state.lastPhotoUri != null) {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .align(Alignment.CenterEnd)
                                .background(Color(0xFF242424))
                                .clickableWithoutIndication {
                                    viewModel.openGallery(context)
                                }
                        ) {
                            AsyncImage(
                                model = state.lastPhotoUri,
                                contentDescription = "Last Photo",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(end = 16.dp)
                                .size(40.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF242424))
                                .align(Alignment.CenterEnd),
                            contentAlignment = Alignment.Center
                        ) {}
                    }
                }
            }
        }
    }
}