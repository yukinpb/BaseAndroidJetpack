package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation

import android.Manifest
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.NativeAdNoMedia
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.BlinkModeDialog
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.EnableFlashComponent
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.FlashSpeedComponent
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.IncomingCallViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.AdsUtils
import com.nlbn.ads.util.Admob
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun IncomingCallScreen(
    modifier: Modifier = Modifier,
    viewModel: IncomingCallViewModel = viewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val activity = LocalActivity.current

    var showBlinkModeDialog by remember { mutableStateOf(false) }

    // Permission launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.checkPhonePermission(context)
            // Now enable flash
            viewModel.toggleFlashEnabled(true, context)
        }
    }

    // Check permission and service state on first load
    LaunchedEffect(Unit) {
        viewModel.checkPhonePermission(context)
        viewModel.checkServiceState(context)
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_flash_alert),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp)
        ) {
            // Header with back arrow and title
            Row(
                modifier = Modifier.fillMaxWidth(),
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
                    text = stringResource(R.string.incoming_call),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (Admob.getInstance().isLoadFullAds) {
                Box(
                    modifier = modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2F3C55))
                ) {
                    NativeAdNoMedia(stringResource(R.string.native_incoming)) { }
                }

                Spacer(modifier = Modifier.height(12.dp))
            }

            // Enable Flash for Incoming Call
            EnableFlashComponent(
                title = stringResource(R.string.enable_flash_for_incoming_call),
                isEnabled = state.isFlashEnabled,
                onToggle = { enabled ->
                    if (enabled && !state.hasPhonePermission) {
                        // Request READ_PHONE_STATE permission
                        permissionLauncher.launch(Manifest.permission.READ_PHONE_STATE)
                    } else {
                        viewModel.toggleFlashEnabled(enabled, context)
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Flash Settings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2F3C55))
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Blink mode row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showBlinkModeDialog = true }
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.blink_mode),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = InterFontFamily
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = state.blinkMode.displayName,
                                color = Color(0xFF12C0FC),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = InterFontFamily
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painter = painterResource(id = R.drawable.ic_expand),
                                contentDescription = "Arrow",
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B465D))
                    )

                    // Flash speed section
                    FlashSpeedComponent(
                        flashSpeed = state.flashSpeed,
                        onFlashSpeedChange = { speed ->
                            viewModel.updateFlashSpeed(speed)
                        },
                        onResetToDefault = {
                            viewModel.resetToDefault()
                        },
                        modifier = Modifier.padding(vertical = 12.dp, horizontal = 16.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B465D))
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Trial Run button
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (state.isTestRunning) stringResource(R.string.stop) else stringResource(
                                R.string.trial_run
                            ),
                            color = Color(0xFF00C8FF),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.clickable { viewModel.testFlashSettings(context) }
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Overlay when flash is disabled - cover the entire Flash Settings Card
                if (!state.isFlashEnabled) {
                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color.Black.copy(alpha = 0.5f))
                            .pointerInput(Unit) {}
                    )
                }
            }
        }
    }

    if (showBlinkModeDialog) {
        BlinkModeDialog(
            currentMode = state.blinkMode,
            onModeSelected = { mode ->
                viewModel.updateBlinkMode(mode, context)
            },
            onDismiss = { showBlinkModeDialog = false }
        )
    }
} 