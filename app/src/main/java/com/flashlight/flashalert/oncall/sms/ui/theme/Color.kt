package com.flashlight.flashalert.oncall.sms.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Seed Colors
val error = Color(0xFFBA1A1A)

// Gradient Colors
val gradientStart = Color(0xFF12C0FC)
val gradientEnd = Color(0xFF1264C8)

// Gradient Brushes
val gradientBrush = Brush.verticalGradient(
    colors = listOf(gradientStart, gradientEnd)
)