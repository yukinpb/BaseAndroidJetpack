package com.flashlight.flashalert.oncall.sms.features.flashlight.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightMode
import com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel.FlashlightState

@Composable
fun MainButtonComponent(
    state: FlashlightState,
    selectedMode: FlashlightMode,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val buttonImage = when {
        state == FlashlightState.OFF -> R.drawable.img_button_off
        selectedMode == FlashlightMode.FLASHLIGHT -> R.drawable.img_button_on_flash_light
        selectedMode == FlashlightMode.SOS -> R.drawable.img_button_on_sos
        selectedMode == FlashlightMode.STROBE -> R.drawable.img_button_on_strobe
        else -> R.drawable.img_button_off
    }

    Box(
        modifier = modifier
            .wrapContentSize()
            .clip(CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = buttonImage),
            contentDescription = "Main Button",
            modifier = Modifier.size(180.dp)
        )
    }
} 