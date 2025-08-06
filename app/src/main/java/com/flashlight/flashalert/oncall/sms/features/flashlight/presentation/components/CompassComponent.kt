package com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

private fun getDirectionText(angle: Float): String {
    return when (angle.toInt()) {
        in 0..11, in 349..360 -> "N"
        in 12..33 -> "NNE"
        in 34..56 -> "NE"
        in 57..78 -> "ENE"
        in 79..101 -> "E"
        in 102..123 -> "ESE"
        in 124..146 -> "SE"
        in 147..168 -> "SSE"
        in 169..191 -> "S"
        in 192..213 -> "SSW"
        in 214..236 -> "SW"
        in 237..258 -> "WSW"
        in 259..281 -> "W"
        in 282..303 -> "WNW"
        in 304..326 -> "NW"
        in 327..348 -> "NNW"
        else -> "N"
    }
}

@Composable
fun CompassComponent(
    angle: Float,
    modifier: Modifier = Modifier
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
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .width(88.dp)
                .height(40.dp)
                .border(
                    width = 1.dp,
                    color = Color(0xFF505459),
                    shape = RoundedCornerShape(24.dp)
                )
                .background(
                    color = Color(0xFF13171A),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "${getDirectionText(angle)}${angle.toInt()}°",
                color = Color.White,
                fontSize = 12.sp,
                fontWeight = FontWeight.W700,
                fontFamily = InterFontFamily
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_compass_arrow_down),
            contentDescription = "Direction Indicator",
            modifier = Modifier.size(8.dp)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.img_compass),
                contentDescription = "Compass",
                modifier = Modifier
                    .size(101.dp)
                    .rotate(-animatedAngle)
            )
        }
    }
}
