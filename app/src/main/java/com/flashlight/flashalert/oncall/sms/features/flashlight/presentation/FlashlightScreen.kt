package com.flashlight.flashalert.oncall.sms.features.flashlight.presentation

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components.CompassComponent
import com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components.MainButtonComponent
import com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components.ModeButtonsComponent
import com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components.StrobeSpeedSlider
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightMode
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.CompassScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>(start = true)
fun FlashlightScreen(
    modifier: Modifier = Modifier,
    viewModel: FlashlightViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        
        if (fineLocationGranted || coarseLocationGranted) {
            // Navigate to compass screen
            navigator.navigate(CompassScreenDestination)
        }
    }

    DisposableEffect(Unit) {
        viewModel.startListening()
        onDispose { viewModel.stopListening() }
    }

    // Background based on selected mode
    val backgroundImage = when (state.selectedMode) {
        FlashlightMode.FLASHLIGHT -> R.drawable.bg_flash_light
        FlashlightMode.SOS -> R.drawable.bg_sos
        FlashlightMode.STROBE -> R.drawable.bg_strobe
        else -> R.drawable.bg_flash_light_off
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = backgroundImage),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.flashlight),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )

                // Settings icon
                Image(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Compass
            CompassComponent(
                angle = state.compassAngle,
                modifier = Modifier.wrapContentHeight(),
                onClick = {
                    val fineLocationPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_FINE_LOCATION
                    )
                    val coarseLocationPermission = ContextCompat.checkSelfPermission(
                        context, Manifest.permission.ACCESS_COARSE_LOCATION
                    )
                    
                    if (fineLocationPermission == android.content.pm.PackageManager.PERMISSION_GRANTED ||
                        coarseLocationPermission == android.content.pm.PackageManager.PERMISSION_GRANTED) {
                        // Navigate to compass screen
                        navigator.navigate(CompassScreenDestination)
                    } else {
                        // Request location permissions
                        locationPermissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION
                            )
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Main button
            MainButtonComponent(
                state = state.currentState,
                selectedMode = state.selectedMode,
                onClick = { viewModel.onMainButtonClick() }
            )

            Spacer(modifier = Modifier.height(40.dp))

            // Mode buttons
            ModeButtonsComponent(
                selectedMode = state.selectedMode,
                onModeSelected = { viewModel.onModeSelected(it) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Strobe speed slider (only show for STROBE mode)
            if (state.selectedMode == FlashlightMode.STROBE) {
                StrobeSpeedSlider(
                    speed = state.strobeSpeed,
                    onSpeedChanged = { viewModel.onStrobeSpeedChanged(it) },
                    modifier = Modifier.fillMaxWidth(0.85f)
                )
            }
        }
    }
} 