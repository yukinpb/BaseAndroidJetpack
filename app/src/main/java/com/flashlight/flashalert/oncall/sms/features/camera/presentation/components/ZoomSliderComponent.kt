package com.flashlight.flashalert.oncall.sms.features.camera.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun ZoomSliderComponent(
    modifier: Modifier = Modifier,
    isAutomaticUpdateZoom: Boolean,
    currentZoom: Float,
    onZoomChanged: (Float) -> Unit
) {
    // Create smaller incremental zoom levels (0.2x steps) from 1.0x to 5.2x
    val minZoom = 1.0f
    val maxZoom = 5.0f

    var dragOffset by remember {
        mutableFloatStateOf(0f)
    }
    var sliderWidth by remember { mutableFloatStateOf(0f) }
    val density = LocalDensity.current

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
    ) {
        // Slider with tick marks at the bottom
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .wrapContentHeight()
                .background(
                    color = Color(0xFF2D2D2D).copy(alpha = 0.6f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(top = 24.dp, bottom = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            // Tick marks across the slider
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .onSizeChanged { size ->
                        with(density) {
                            sliderWidth = (size.width.toDp() - 15.dp).value
                        }
                    },
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.Bottom
            ) {
                val majorZoomLevels = listOf(1f, 2f, 3f, 4f, 5f)
                majorZoomLevels.forEachIndexed { index, majorZoom ->
                    if (index < majorZoomLevels.size - 1) {
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.weight(5f)
                        ) {
                            // Add tick marks horizontally after each major zoom level (except the last one)
                            repeat(6) { tickIndex ->
                                Box(
                                    modifier = Modifier
                                        .size(1.dp, 10.dp)
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(0.5.dp)
                                        )
                                )
                            }
                        }
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceAround,
                            modifier = Modifier.weight(1f)
                        ) {
                            // Major zoom level label
                            Box(
                                modifier = Modifier
                                    .size(1.dp, 16.dp)
                                    .background(
                                        color = Color.White,
                                        shape = RoundedCornerShape(0.5.dp)
                                    )
                            )
                        }
                    }
                }
            }
        }

        LaunchedEffect(sliderWidth) {
            if (sliderWidth > 0f) {
                dragOffset = ((currentZoom - minZoom) / (maxZoom - minZoom)) * sliderWidth
            }
        }

        Column(
            modifier = Modifier
                .padding(bottom = 16.dp)
                .align(Alignment.BottomStart)
                .offset(x = if (isAutomaticUpdateZoom) (((currentZoom - minZoom) / (maxZoom - minZoom)) * sliderWidth + 6).dp else (dragOffset + 6).dp, y = 0.dp)
                .wrapContentHeight()
                .pointerInput(sliderWidth, minZoom, maxZoom) {
                    detectDragGestures(
                        onDrag = { change, dragAmount ->
                            change.consume()
                            // Reduce drag sensitivity for smoother movement (0.5f multiplier)
                            val newOffset =
                                (dragOffset + dragAmount.x * 0.5f).coerceIn(0f, sliderWidth)
                            dragOffset = newOffset
                            val newZoom = minZoom + (newOffset / sliderWidth) * (maxZoom - minZoom)

                            // Snap to nearest 0.2x increment
                            val snappedZoom = (newZoom * 5).roundToInt() / 5f
                            onZoomChanged(snappedZoom)
                        },
                        onDragEnd = {
                            // Snap to nearest 0.2x increment visually
                            val newZoom = minZoom + (dragOffset / sliderWidth) * (maxZoom - minZoom)
                            val snappedZoom = (newZoom * 5).roundToInt() / 5f
                            dragOffset =
                                ((snappedZoom - minZoom) / (maxZoom - minZoom)) * sliderWidth
                        }
                    )
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "x${String.format(Locale.US, "%.1f", currentZoom)}",
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.W500
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Yellow vertical line indicator (like in the image)
            Box(
                modifier = Modifier
                    .size(3.dp, 32.dp)
                    .background(
                        color = Color(0xFFFFD700), // Yellow color
                        shape = RoundedCornerShape(1.5.dp)
                    )
            )
        }
    }
}
