package com.flashlight.flashalert.oncall.sms.core.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flashlight.flashalert.oncall.sms.R
import com.smarttoolfactory.slider.ColorfulIconSlider
import com.smarttoolfactory.slider.MaterialSliderDefaults
import com.smarttoolfactory.slider.SliderBrushColor

@Composable
fun CustomSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    valueRange: ClosedFloatingPointRange<Float>,
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
        ),
        modifier = modifier,
    ) {
        Image(
            painter = painterResource(id = R.drawable.img_slider_thumb),
            contentDescription = null,
            modifier = Modifier.size(15.dp)
        )
    }
} 