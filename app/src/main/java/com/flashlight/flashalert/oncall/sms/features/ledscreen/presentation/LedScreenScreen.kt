package com.flashlight.flashalert.oncall.sms.features.ledscreen.presentation

import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.ledscreen.presentation.components.BrightnessSliderComponent
import com.flashlight.flashalert.oncall.sms.features.ledscreen.presentation.components.ColorSliderComponent
import com.flashlight.flashalert.oncall.sms.features.ledscreen.viewmodel.LedScreenEvent
import com.flashlight.flashalert.oncall.sms.features.ledscreen.viewmodel.LedScreenMode
import com.flashlight.flashalert.oncall.sms.features.ledscreen.viewmodel.LedScreenViewModel
import com.flashlight.flashalert.oncall.sms.utils.AdsUtils
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun LedScreenScreen(
    modifier: Modifier = Modifier,
    viewModel: LedScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()
    val currentMode = state.currentMode
    val activity = LocalActivity.current

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Main LED screen background
        if (currentMode == LedScreenMode.TWO_COLOR) {
            // TWO_COLOR mode: Split screen into two halves
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Top half
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            color = if (state.isBlueTop) Color(0xFF002CFF) else Color(0xFFF71801),
                            shape = RoundedCornerShape(0.dp)
                        )
                )

                // Bottom half
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(
                            color = if (state.isBlueTop) Color(0xFFF71801) else Color(0xFF002CFF),
                            shape = RoundedCornerShape(0.dp)
                        )
                )
            }
        } else {
            // Other modes: Single color background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        color = state.currentColor.copy(alpha = state.brightness),
                        shape = RoundedCornerShape(0.dp)
                    )
            )
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(48.dp)
                    .clip(CircleShape)
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
                    },
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_close_ledscreen),
                    contentDescription = "Close",
                    modifier = Modifier.size(24.dp)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Bottom
            ) {
                if (currentMode == LedScreenMode.STATIC) {
                    ColorSliderComponent(
                        onColorChanged = { color ->
                            viewModel.handleEvent(LedScreenEvent.UpdateColor(color))
                        },
                        initialColor = state.currentColor,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }

                if (currentMode != LedScreenMode.TWO_COLOR) {
                    BrightnessSliderComponent(
                        onBrightnessChanged = { brightness ->
                            viewModel.handleEvent(LedScreenEvent.UpdateBrightness(brightness))
                        },
                        initialBrightness = state.brightness,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )
                }

                // Mode buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Mode 1: Continuous Color
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .clickable {
                                viewModel.handleEvent(LedScreenEvent.EnterContinuousMode)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentMode == LedScreenMode.CONTINUOUS) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_select_mode_ledscreen),
                                contentDescription = "Continuous Mode Active",
                                modifier = Modifier.size(54.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mode_continue_ledscreen),
                                contentDescription = "Continuous Mode Inactive",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }

                    // Mode 2: Two Color
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(CircleShape)
                            .clickableWithoutIndication {
                                viewModel.handleEvent(LedScreenEvent.EnterTwoColorMode)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentMode == LedScreenMode.TWO_COLOR) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_select_mode_ledscreen),
                                contentDescription = "Two Color Mode Active",
                                modifier = Modifier.size(54.dp)
                            )
                        } else {
                            Image(
                                painter = painterResource(id = R.drawable.ic_mode_2_color_ledscreen),
                                contentDescription = "Two Color Mode Inactive",
                                modifier = Modifier.size(40.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
} 