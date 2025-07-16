package com.basecompose.baseproject.core.utils

import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// UI Extensions
fun Modifier.defaultPadding() = this.padding(Constants.DEFAULT_PADDING.dp)
fun Modifier.smallPadding() = this.padding(Constants.DEFAULT_SPACING.dp)

// String Extensions
fun String?.orEmpty(): String = this ?: ""

// Number Extensions
fun Int.toDp() = this.dp
fun Float.toDp() = this.dp 