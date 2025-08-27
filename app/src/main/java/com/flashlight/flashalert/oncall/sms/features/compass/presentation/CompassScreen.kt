package com.flashlight.flashalert.oncall.sms.features.compass.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.compass.presentation.components.CompassDialComponent
import com.flashlight.flashalert.oncall.sms.features.compass.presentation.components.GoogleMapView
import com.flashlight.flashalert.oncall.sms.features.compass.viewmodel.CompassViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.AdsUtils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>
fun CompassScreen(
    modifier: Modifier = Modifier,
    viewModel: CompassViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var shouldCenterToCurrentLocation by remember { mutableStateOf(false) }
    val activity = LocalActivity.current

    DisposableEffect(Unit) {
        viewModel.startListening()
        onDispose { viewModel.stopListening() }
    }

    // Check and request location settings when entering the screen
    LaunchedEffect(Unit) {
        activity?.let {
            viewModel.checkAndRequestLocationSettings(it)
        }
    }

    // Handle location settings result
    LaunchedEffect(Unit) {
        // This will be triggered when returning from GPS settings dialog
        // We can check if GPS is now enabled and start location updates
        if (activity != null) {
            // Small delay to ensure settings are applied
            kotlinx.coroutines.delay(500)
            viewModel.startLocationUpdates()
        }
    }

    // Reset the flag after it's been used
    LaunchedEffect(shouldCenterToCurrentLocation) {
        if (shouldCenterToCurrentLocation) {
            shouldCenterToCurrentLocation = false
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        GoogleMapView(
            currentLocation = state.compass.currentLocation,
            isDirectionEnabled = state.compass.isDirectionEnabled,
            compassAngle = state.compass.angle,
            shouldCenterToCurrentLocation = shouldCenterToCurrentLocation
        )

        CompassDialComponent(
            angle = state.compass.angle,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.TopCenter),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
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
                    text = "Compass",
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )

                Spacer(modifier = Modifier.weight(1f))

                Image(
                    painter = painterResource(id = R.drawable.ic_current_pos),
                    contentDescription = "Current Position",
                    modifier = Modifier
                        .size(31.dp)
                        .clickable {
                            // Center map on current location
                            if (state.compass.currentLocation != null) {
                                shouldCenterToCurrentLocation = true
                            }
                        }
                )
            }

            // Bottom buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 50.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Image(
                    painter = painterResource(
                        id = if (state.compass.isDirectionEnabled) R.drawable.ic_direction_on else R.drawable.ic_direction_off
                    ),
                    contentDescription = "Direction",
                    modifier = Modifier
                        .size(70.dp)
                        .clickable {
                            viewModel.toggleDirection()
                            if (state.compass.currentLocation != null) {
                                shouldCenterToCurrentLocation = true
                            }
                        }
                )

                Image(
                    painter = painterResource(
                        id = if (state.compass.isFlashEnabled) R.drawable.ic_flash_light_on else R.drawable.ic_flash_light_off
                    ),
                    contentDescription = "Flashlight",
                    modifier = Modifier
                        .size(70.dp)
                        .clickable { viewModel.toggleFlash() }
                )
            }
        }
    }
}