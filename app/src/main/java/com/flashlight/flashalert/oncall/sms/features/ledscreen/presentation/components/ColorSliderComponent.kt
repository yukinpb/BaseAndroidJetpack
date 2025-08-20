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
fun ColorSliderComponent(
    onColorChanged: (Color) -> Unit,
    initialColor: Color = Color.White,
    modifier: Modifier = Modifier
) {
    val colors = listOf(
        Color(0xFF8B00FF), // Purple
        Color(0xFF0000FF), // Blue
        Color(0xFF00FFFF), // Cyan
        Color(0xFF00FF00), // Green
        Color(0xFFFFFF00), // Yellow
        Color(0xFFFF8000), // Orange
        Color(0xFFFF0000), // Red
        Color(0xFFFF00FF), // Magenta
        Color(0xFF8B4513)  // Brown
    )

    val gradient = Brush.horizontalGradient(colors = colors)
    var sliderWidth by remember { mutableFloatStateOf(0f) }
    var dragOffset by remember {
        mutableFloatStateOf(0f)
    }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(horizontal = 4.dp)
    ) {
        // Color gradient background with rounded corners
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .background(
                    brush = gradient,
                    shape = RoundedCornerShape(20.dp)
                )
                .align(Alignment.Center)
                .onSizeChanged { size ->
                    with(density) {
                        sliderWidth = (size.width.toDp() - 24.dp).value
                    }
                }
        )

        LaunchedEffect(sliderWidth) {
            if (sliderWidth > 0f) {
                dragOffset = calculateInitialOffset(initialColor, colors, sliderWidth)
                onColorChanged(initialColor)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = dragOffset.dp, y = 0.dp)
                .size(24.dp, 24.dp)
                .clip(CircleShape)
                .background(Color.Black)
                .pointerInput(sliderWidth) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            if (sliderWidth > 0f) {
                                // Reduce drag sensitivity for smoother movement (0.5f multiplier)
                                val newOffset =
                                    (dragOffset + dragAmount.x * 0.5f).coerceIn(0f, sliderWidth)
                                dragOffset = newOffset

                                // Calculate interpolated color based on position
                                val progress = newOffset / sliderWidth
                                val colorIndex = progress * (colors.size - 1)

                                // Interpolate between two nearest colors
                                val lowerIndex = colorIndex.toInt().coerceIn(0, colors.size - 1)
                                val upperIndex = (lowerIndex + 1).coerceIn(0, colors.size - 1)
                                val fraction = colorIndex - lowerIndex

                                val lowerColor = colors[lowerIndex]
                                val upperColor = colors[upperIndex]

                                // Interpolate RGB values
                                val interpolatedColor =
                                    interpolateColor(lowerColor, upperColor, fraction)

                                onColorChanged(interpolatedColor)
                            }
                        }
                    )
                }
        )
    }
}

private fun interpolateColor(color1: Color, color2: Color, fraction: Float): Color {
    val r1 = color1.red
    val g1 = color1.green
    val b1 = color1.blue

    val r2 = color2.red
    val g2 = color2.green
    val b2 = color2.blue

    val interpolatedRed = r1 + (r2 - r1) * fraction
    val interpolatedGreen = g1 + (g2 - g1) * fraction
    val interpolatedBlue = b1 + (b2 - b1) * fraction

    return Color(
        red = interpolatedRed.coerceIn(0f, 1f),
        green = interpolatedGreen.coerceIn(0f, 1f),
        blue = interpolatedBlue.coerceIn(0f, 1f),
        alpha = 1f
    )
}

private fun calculateInitialOffset(
    initialColor: Color,
    colors: List<Color>,
    sliderWidth: Float
): Float {
    // Find the closest color in our predefined list
    var minDistance = Float.MAX_VALUE
    var closestIndex = 0

    for (i in colors.indices) {
        val distance = calculateColorDistance(initialColor, colors[i])
        if (distance < minDistance) {
            minDistance = distance
            closestIndex = i
        }
    }

    // Calculate position based on closest color index
    val progress = closestIndex.toFloat() / (colors.size - 1)
    return progress * sliderWidth
}

private fun calculateColorDistance(color1: Color, color2: Color): Float {
    val r1 = color1.red
    val g1 = color1.green
    val b1 = color1.blue

    val r2 = color2.red
    val g2 = color2.green
    val b2 = color2.blue

    return kotlin.math.sqrt((r1 - r2) * (r1 - r2) + (g1 - g2) * (g1 - g2) + (b1 - b2) * (b1 - b2))
}
