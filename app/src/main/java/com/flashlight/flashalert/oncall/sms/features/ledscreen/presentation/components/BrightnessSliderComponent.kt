package com.flashlight.flashalert.oncall.sms.features.ledscreen.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun BrightnessSliderComponent(
    modifier: Modifier = Modifier,
    onBrightnessChanged: (Float) -> Unit,
    initialBrightness: Float = 1.0f
) {
    val brightnessGradient = Brush.horizontalGradient(
        colors = listOf(
            Color.LightGray,  // Light (bright)
            Color.Gray,       // Medium
            Color.DarkGray    // Dark (dim)
        )
    )

    var sliderWidth by remember { mutableFloatStateOf(0f) }
    var dragOffset by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp) // Tách padding ra ngoài
    ) {
        // Gradient background (measures true width)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    brush = brightnessGradient,
                    shape = RoundedCornerShape(20.dp)
                )
                .align(Alignment.Center)
                .onSizeChanged { size ->
                    with(density) {
                        sliderWidth = (size.width.toDp() - 24.dp).value
                    }
                }
        )

        // Set initial position of thumb once sliderWidth is known
        LaunchedEffect(sliderWidth) {
            if (sliderWidth > 0f) {
                dragOffset = (initialBrightness.coerceIn(0f, 1f)) * sliderWidth
                onBrightnessChanged(initialBrightness)
            }
        }

        // Thumb
        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = dragOffset.dp)
                .size(24.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .pointerInput(sliderWidth) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (sliderWidth > 0f) {
                                val newOffset = (dragOffset + dragAmount.x * 0.5f).coerceIn(0f, sliderWidth)
                                dragOffset = newOffset

                                val brightness = (newOffset / sliderWidth).coerceIn(0f, 1f)
                                onBrightnessChanged(brightness)
                            }
                        }
                    )
                }
        )
    }
}
