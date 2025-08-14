package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.flashlight.flashalert.oncall.sms.R

@Composable
fun CustomToggle(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Image(
        painter = painterResource(
            id = if (checked) R.drawable.toggle_on else R.drawable.toggle_off
        ),
        contentDescription = if (checked) "Toggle On" else "Toggle Off",
        modifier = modifier
            .size(36.dp, 21.dp)
            .clickable { onCheckedChange(!checked) }
    )
} 