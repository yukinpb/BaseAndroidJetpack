package com.flashlight.flashalert.oncall.sms.features.camera.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import kotlin.math.roundToInt

@Composable
fun ZoomSliderComponent(
    currentZoom: Float,
    onZoomChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    // Create smaller incremental zoom levels (0.2x steps) from 1.0x to 4.0x
    val minZoom = 1.0f
    val maxZoom = 4.0f

    // Calculate slider width and thumb position
    val sliderWidth = 320f // Total slider width in dp
    val thumbOffset = ((currentZoom - minZoom) / (maxZoom - minZoom)) * sliderWidth

    Box(
        modifier = modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(16.dp)
            .background(
                color = Color(0xFF2D2D2D).copy(alpha = 0.6f),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        // Major zoom level labels (1x, 2x, 3x, 4x) with tick marks horizontally
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .height(39.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            val majorZoomLevels = listOf(1f, 2f, 3f, 4f)
            majorZoomLevels.forEachIndexed { index, majorZoom ->
                if (index < majorZoomLevels.size - 1) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceAround,
                        modifier = Modifier.weight(5f)
                    ) {
                        // Major zoom level label
                        Text(
                            text = "${majorZoom.toInt()}x",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.clickable {
                                onZoomChanged(majorZoom)
                            }
                        )

                        // Add tick marks horizontally after each major zoom level (except the last one)
                        repeat(5) { tickIndex ->
                            Box(
                                modifier = Modifier
                                    .size(1.dp, 8.dp)
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
                        Text(
                            text = "${majorZoom.toInt()}x",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.W400,
                            modifier = Modifier.clickable {
                                onZoomChanged(majorZoom)
                            }
                        )
                    }
                }

            }
        }

        // Draggable Zoom thumb with larger hit box
        var dragOffset by remember { mutableFloatStateOf(thumbOffset) }

        Box(
            modifier = Modifier
                .align(Alignment.CenterStart)
                .offset(x = dragOffset.dp, y = 0.dp)
                .size(24.dp, 39.dp) // Larger hit box for easier dragging
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
                }
        ) {
            // Center the actual thumb image within the larger hit box
            Image(
                painter = painterResource(id = R.drawable.ic_zoom_thumb),
                contentDescription = "Zoom",
                modifier = Modifier
                    .size(8.dp, 39.dp)
                    .align(Alignment.Center)
            )
        }
    }
}
