package com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightMode

@Composable
fun ModeButtonsComponent(
    selectedMode: FlashlightMode,
    onModeSelected: (FlashlightMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        ModeButton(
            mode = FlashlightMode.SOS,
            icon = R.drawable.ic_sos,
            isSelected = selectedMode == FlashlightMode.SOS,
            onClick = { onModeSelected(FlashlightMode.SOS) }
        )

        ModeButton(
            mode = FlashlightMode.FLASHLIGHT,
            icon = R.drawable.ic_flash_light,
            isSelected = selectedMode == FlashlightMode.FLASHLIGHT,
            onClick = { onModeSelected(FlashlightMode.FLASHLIGHT) }
        )

        ModeButton(
            mode = FlashlightMode.STROBE,
            icon = R.drawable.ic_strobe,
            isSelected = selectedMode == FlashlightMode.STROBE,
            onClick = { onModeSelected(FlashlightMode.STROBE) }
        )
    }
}

@Composable
private fun ModeButton(
    mode: FlashlightMode,
    icon: Int,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val colorButtonBrush = when (mode) {
        FlashlightMode.FLASHLIGHT -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF078CBE),
                Color(0xFF04AAE9)
            )
        )

        FlashlightMode.SOS -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFFB80B0B),
                Color(0xFFFF0000)
            )
        )

        FlashlightMode.STROBE -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF600CCE),
                Color(0xFF903CFF)
            )
        )

        else -> Brush.verticalGradient(
            colors = listOf(
                Color(0xFF212121),
                Color(0xFF2E3236)
            )
        )
    }

    val text = when (mode) {
        FlashlightMode.FLASHLIGHT -> "Flashlight"
        FlashlightMode.SOS -> "SOS"
        FlashlightMode.STROBE -> "Strobe"
        else -> "Unknown"
    }

    val colorText = if (isSelected) {
        when (mode) {
            FlashlightMode.FLASHLIGHT -> Color(0xFF12C0FC)
            FlashlightMode.SOS -> Color(0xFFFF0000)
            FlashlightMode.STROBE -> Color(0xFFA662FF)
            else -> Color.White
        }
    } else {
        Color.White
    }

    Column(
        modifier = Modifier
            .wrapContentHeight(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(67.dp)
                .clip(CircleShape)
                .background(
                    brush = if (isSelected) {
                        colorButtonBrush
                    } else {
                        Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF212121),
                                Color(0xFF2E3236)
                            )
                        )
                    }
                )
                .shadow(
                    elevation = 32.dp,
                    shape = RoundedCornerShape(
                        topStart = 12.dp,
                        topEnd = 12.dp
                    ),
                    clip = true,
                    ambientColor = Color.Gray.copy(alpha = 0.4f),
                    spotColor = Color.Gray.copy(alpha = 0.6f)
                )
                .clickable(
                    onClick = onClick,
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = icon),
                contentDescription = mode.name,
                modifier = Modifier.size(32.dp)
            )
        }

        Text(
            text = text,
            color = colorText,
            modifier = Modifier.padding(top = 4.dp)
        )
    }

} 