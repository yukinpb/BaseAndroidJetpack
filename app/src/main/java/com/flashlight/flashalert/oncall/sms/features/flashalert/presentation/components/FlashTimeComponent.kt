package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.ui.components.CustomSlider
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

@Composable
fun FlashTimeComponent(
    flashTimes: Int,
    onFlashTimesChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.flash_times),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = InterFontFamily
            )
            Text(
                text = "$flashTimes time(s)",
                color = Color.White.copy(alpha = 0.6f),
                fontSize = 14.sp,
                fontWeight = FontWeight.W500,
                fontFamily = InterFontFamily
            )
        }

        // Flash times slider
        CustomSlider(
            value = flashTimes.toFloat(),
            onValueChange = { onFlashTimesChange(it.toInt()) },
            valueRange = 1f..10f,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
} 