package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.ui.components.CustomSlider
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.ui.input.pointer.pointerInput

@Composable
fun BatterySaverScheduleSlider(
    isEnable: Boolean,
    batteryThreshold: Float,
    onBatteryThresholdChange: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(modifier = modifier) {
        Column(modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth().offset(y = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.turn_off_if_battery_below),
                    color = Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = InterFontFamily,
                    modifier = Modifier
                        .weight(1f)
                )

                Text(
                    text = "${batteryThreshold.toInt()}%",
                    color = Color(0xFF00C8FF),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Battery threshold slider
            CustomSlider(
                value = batteryThreshold,
                onValueChange = onBatteryThresholdChange,
                valueRange = 10f..100f,
                modifier = Modifier.fillMaxWidth()
            )
        }

        if (!isEnable) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .background(Color.Black.copy(alpha = 0.5f))
                    .pointerInput(Unit) {}
            )
        }
    }
} 