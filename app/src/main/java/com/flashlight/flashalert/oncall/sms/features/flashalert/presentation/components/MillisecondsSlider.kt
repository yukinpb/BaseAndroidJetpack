package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.Constants
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

@Composable
fun MillisecondsSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = Constants.MIN_SPEED_FLASH..Constants.MAX_SPEED_FLASH
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

    val reversedValue = valueRange.endInclusive - value + valueRange.start

    Column(modifier = modifier) {
        Text(
            text = "${reversedValue.toInt()} ms",
            color = Color.White.copy(alpha = 0.6f),
            fontSize = 12.sp,
            fontWeight = FontWeight.W400,
            fontFamily = InterFontFamily,
            modifier = Modifier
                .padding(start = 6.dp)
                .offset(y = 12.dp)
        )

        ColorfulIconSlider(
            value = value,
            onValueChange = onValueChange,
            valueRange = valueRange,
            colors = MaterialSliderDefaults.materialColors(
                inactiveTrackColor = SliderBrushColor(
                    brush = gradientInactive()
                ),
                activeTrackColor = SliderBrushColor(
                    brush = gradientActive(),
                )
            )
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_slider_thumb),
                contentDescription = null,
                modifier = Modifier.size(15.dp)
            )
        }
    }
} 