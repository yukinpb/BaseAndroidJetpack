package com.flashlight.flashalert.oncall.sms.core.utils

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// UI Extensions
fun Modifier.defaultPadding() = this.padding(Constants.DEFAULT_PADDING.dp)
fun Modifier.smallPadding() = this.padding(Constants.DEFAULT_SPACING.dp)

/**
 * Extension function to override clickable with indication = null and interactionSource = remember { MutableInteractionSource() }
 */
@Composable
fun Modifier.clickableWithoutIndication(
    enabled: Boolean = true,
    onClick: () -> Unit
): Modifier = this.clickable(
    enabled = enabled,
    onClick = onClick,
    indication = null,
    interactionSource = remember { MutableInteractionSource() }
)

// String Extensions
fun String?.orEmpty(): String = this ?: ""

// Number Extensions
fun Int.toDp() = this.dp
fun Float.toDp() = this.dp 