package com.flashlight.flashalert.oncall.sms.features.compass.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CompassDialComponent(
    modifier: Modifier = Modifier,
    angle: Float
) {
    var previousAngle by remember { mutableFloatStateOf(angle) }
    val adjustedAngle = remember(angle) {
        val delta = (angle - previousAngle + 540) % 360 - 180
        previousAngle = (previousAngle + delta + 360) % 360
        previousAngle
    }
    val animatedAngle by animateFloatAsState(
        targetValue = adjustedAngle,
        animationSpec = tween(durationMillis = 250),
        label = "CompassRotation"
    )

    Box(
        modifier = modifier.background(Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        // Background around circle (donut shape) with transparent center
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = size.width * 0.4f // Circle radius

            // Create path for donut shape (full screen minus circle)
            val path = Path().apply {
                // Full screen rectangle
                addRect(Rect(0f, 0f, size.width, size.height))
                // Circle hole (transparent center)
                addOval(Rect(
                    center.x - radius,
                    center.y - radius,
                    center.x + radius,
                    center.y + radius
                ))
                fillType = androidx.compose.ui.graphics.PathFillType.EvenOdd
            }

            // Draw black semi-transparent background around the circle
            drawPath(
                path = path,
                color = Color.Black.copy(alpha = 0.4f)
            )

            // Draw cardinal directions outside the circle
            drawIntoCanvas { canvas ->
                val directionRadius = radius + 65f // Position text outside the circle
                
                // Draw N, W, E, S labels
                val directions = listOf(
                    Triple(0f, "N", Color.Red),
                    Triple(90f, "E", Color.White),
                    Triple(180f, "S", Color.White),
                    Triple(270f, "W", Color.White)
                )

                directions.forEach { (directionAngle, label, color) ->
                    val angleRad = Math.toRadians((directionAngle - animatedAngle).toDouble())
                    val labelX = center.x + cos(angleRad).toFloat() * directionRadius
                    val labelY = center.y + sin(angleRad).toFloat() * directionRadius + 15f

                    canvas.nativeCanvas.drawText(
                        label,
                        labelX,
                        labelY,
                        android.graphics.Paint().apply {
                            this.color = color.toArgb()
                            textSize = 80f // Larger text size
                            textAlign = android.graphics.Paint.Align.CENTER
                            typeface = android.graphics.Typeface.DEFAULT_BOLD
                        }
                    )
                }
            }
        }
    }
}