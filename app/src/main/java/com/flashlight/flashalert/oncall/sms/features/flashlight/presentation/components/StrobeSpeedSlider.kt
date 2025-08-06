package com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

@Composable
fun StrobeSpeedSlider(
    speed: Float,
    onSpeedChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val gradientActive = {
        Brush.linearGradient(
            colors = listOf(Color(0xFF12C0FC), Color(0xFF1264C8)),
        )
    }

    val gradientInactive = {
        Brush.linearGradient(
            colors = listOf(Color(0xFF999999), Color(0xFFCCCCCC)),
        )
    }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(R.string.flashing_speed),
            color = Color.White,
            fontSize = 12.sp,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.W600
        )

        ColorfulIconSlider(
            value = speed,
            onValueChange = onSpeedChanged,
            valueRange = 1f..50f,
            colors = MaterialSliderDefaults.materialColors(
                inactiveTrackColor = SliderBrushColor(
                    brush = gradientInactive()
                ),
                activeTrackColor = SliderBrushColor(
                    brush = gradientActive(),
                )
            ),
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 4.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_slider_thumb),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }

        Text(
            text = speed.toInt().toString(),
            color = Color(0xFF00B9FF),
            fontSize = 12.sp,
            fontFamily = InterFontFamily,
            fontWeight = FontWeight.W700
        )
    }
} 