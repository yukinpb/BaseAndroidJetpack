package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation

import android.widget.Toast
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.AppNotificationEnableFlashComponent
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.FlashSpeedComponent
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.FlashTimeComponent
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.SmsPermissionDialog
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.AppNotificationViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.AppSelectionScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun AppNotificationScreen(
    modifier: Modifier = Modifier,
    viewModel: AppNotificationViewModel = viewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LaunchedEffect(Unit) {
        viewModel.checkNotificationPermission(context)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.enableFlashAfterPermission(context)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
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
                        .clickableWithoutIndication { navigator.navigateUp() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.app_notifications),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Advertisement banner
            Image(
                painter = painterResource(id = R.drawable.img_ads_native),
                contentDescription = "App Notifications",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Enable Flash for App Notifications with Select Applications
            AppNotificationEnableFlashComponent(
                title = stringResource(R.string.enable_flash_for_app_notifications),
                isEnabled = state.isFlashEnabled,
                onToggle = { enabled ->
                    viewModel.toggleFlashEnabled(enabled, context)
                },
                onSelectApplications = {
                    if (!state.isFlashEnabled) {
                        Toast.makeText(context,
                            context.getString(R.string.please_enable_flash_for_app_notifications_first), Toast.LENGTH_SHORT).show()
                    } else {
                        navigator.navigate(AppSelectionScreenDestination)
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
                    modifier = Modifier
                        .padding(vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Flash times section
                    FlashTimeComponent(
                        flashTimes = state.flashTimes,
                        onFlashTimesChange = { times ->
                            viewModel.updateFlashTimes(times)
                        },
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    // Divider
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
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp)
                    )

                    // Divider
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
                            modifier = Modifier.clickableWithoutIndication {
                                viewModel.testFlashSettings(
                                    context
                                )
                            }
                        )
                    }
                }

                // Overlay when flash is disabled
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

        // Permission Dialog
        if (state.showPermissionDialog) {
            SmsPermissionDialog(
                onDismiss = { viewModel.hidePermissionDialog() },
                onOpenSettings = { viewModel.openNotificationAccessSettings(context) }
            )
        }
    }
} 