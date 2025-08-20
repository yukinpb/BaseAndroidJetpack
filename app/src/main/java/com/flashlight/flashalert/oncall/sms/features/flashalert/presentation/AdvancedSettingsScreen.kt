package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation

import androidx.activity.compose.LocalActivity
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.NativeAdNoMedia
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.BatterySaverScheduleSlider
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.CustomToggle
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.TimePickerDialog
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.AdvancedSettingsViewModel
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.TimePickerType
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.AdsUtils
import com.nlbn.ads.util.Admob
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun AdvancedSettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: AdvancedSettingsViewModel = viewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()
    val activity = LocalActivity.current

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
                    text = stringResource(R.string.advanced_settings),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (Admob.getInstance().isLoadFullAds) {
                    // Advertisement Card
                    Box(
                        modifier = modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(Color(0xFF2F3C55))
                    ) {
                        NativeAdNoMedia(stringResource(R.string.native_Advanced)) { }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                }

                // Disable when phone is in use
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2F3C55))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_phone),
                            contentDescription = "Phone",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.disable_when_phone_in_use),
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W500,
                            modifier = Modifier.weight(1f),
                            fontFamily = InterFontFamily
                        )
                        CustomToggle(
                            checked = state.disableWhenPhoneInUse,
                            onCheckedChange = { viewModel.updateDisableWhenPhoneInUse(it) }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Battery saver schedule
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2F3C55))
                        .padding(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_battery),
                            contentDescription = "Battery",
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.battery_saver_schedule),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                fontFamily = InterFontFamily
                            )
                        }
                        CustomToggle(
                            checked = state.batterySaverEnabled,
                            onCheckedChange = { viewModel.updateBatterySaverEnabled(it) }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B465D))
                    )

                    BatterySaverScheduleSlider(
                        batteryThreshold = state.batteryThreshold,
                        onBatteryThresholdChange = { viewModel.updateBatteryThreshold(it) },
                        onResetToDefault = {
                            viewModel.updateBatteryThreshold(30f)
                        }
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time to flash off
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2F3C55))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_half_moon),
                                contentDescription = "Time",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = stringResource(R.string.time_to_flash_off),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W500,
                                    fontFamily = InterFontFamily
                                )
                            }
                            CustomToggle(
                                checked = state.timeToFlashOffEnabled,
                                onCheckedChange = { viewModel.updateTimeToFlashOffEnabled(it) }
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF3B465D))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = stringResource(R.string.select_downtime_to_disable_flash_alert),
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            fontFamily = InterFontFamily
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Time selectors
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // From time
                            Box(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                                    .clickableWithoutIndication {
                                        viewModel.showTimePickerDialog(TimePickerType.FROM_TIME)
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = stringResource(R.string.from),
                                        color = Color.White.copy(alpha = 0.6f),
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = state.timeFrom,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_expand_down),
                                        contentDescription = "Select time",
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }

                            // To time
                            Box(
                                modifier = Modifier
                                    .wrapContentWidth()
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color.White.copy(alpha = 0.2f))
                                    .padding(vertical = 8.dp, horizontal = 12.dp)
                                    .clickableWithoutIndication {
                                        viewModel.showTimePickerDialog(TimePickerType.TO_TIME)
                                    }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box {
                                        Text(
                                            text = stringResource(R.string.from),
                                            color = Color.Transparent,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400
                                        )
                                        Text(
                                            text = stringResource(R.string.to),
                                            color = Color.White.copy(alpha = 0.6f),
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.W400,
                                            modifier = Modifier.align(Alignment.Center)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = state.timeTo,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W400
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Image(
                                        painter = painterResource(id = R.drawable.ic_expand_down),
                                        contentDescription = "Select time",
                                        modifier = Modifier.size(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Enable Flash in mode
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF2F3C55))
                        .padding(16.dp)
                ) {
                    Column {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_flash),
                                contentDescription = "Flash",
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.width(16.dp))
                            Text(
                                text = stringResource(R.string.enable_flash_in_mode),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W500,
                                modifier = Modifier.weight(1f),
                                fontFamily = InterFontFamily
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(1.dp)
                                .background(Color(0xFF3B465D))
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Mode options
                        Column {
                            // Ringtone mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.ringtone),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    modifier = Modifier.weight(1f),
                                    fontFamily = InterFontFamily
                                )
                                CustomToggle(
                                    checked = state.enableFlashInRingtoneMode,
                                    onCheckedChange = { viewModel.updateEnableFlashInRingtoneMode(it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Vibrate mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.vibrate),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    modifier = Modifier.weight(1f),
                                    fontFamily = InterFontFamily
                                )
                                CustomToggle(
                                    checked = state.enableFlashInVibrateMode,
                                    onCheckedChange = { viewModel.updateEnableFlashInVibrateMode(it) }
                                )
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            // Mute mode
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.mute),
                                    color = Color.White,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.W400,
                                    modifier = Modifier.weight(1f),
                                    fontFamily = InterFontFamily
                                )
                                CustomToggle(
                                    checked = state.enableFlashInMuteMode,
                                    onCheckedChange = { viewModel.updateEnableFlashInMuteMode(it) }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Time Picker Dialog
        if (state.showTimePickerDialog) {
            val currentTime = when (state.timePickerType) {
                TimePickerType.FROM_TIME -> state.timeFrom
                TimePickerType.TO_TIME -> state.timeTo
                TimePickerType.NONE -> "00:00"
            }

            val onTimeSelected = { time: String ->
                when (state.timePickerType) {
                    TimePickerType.FROM_TIME -> viewModel.updateTimeFrom(time)
                    TimePickerType.TO_TIME -> viewModel.updateTimeTo(time)
                    TimePickerType.NONE -> {}
                }
            }

            TimePickerDialog(
                onDismiss = { viewModel.hideTimePickerDialog() },
                onTimeSelected = onTimeSelected,
                currentTime = currentTime
            )
        }
    }
} 