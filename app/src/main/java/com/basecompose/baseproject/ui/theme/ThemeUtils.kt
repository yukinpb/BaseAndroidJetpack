package com.basecompose.baseproject.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

/**
 * Custom color palette cho app
 */
data class AppColors(
    val primary: Color,
    val onPrimary: Color,
    val primaryContainer: Color,
    val onPrimaryContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val error: Color,
    val onError: Color,
    val errorContainer: Color,
    val onErrorContainer: Color,
    val background: Color,
    val onBackground: Color,
    val surface: Color,
    val onSurface: Color,
    val surfaceVariant: Color,
    val onSurfaceVariant: Color,
    val outline: Color,
    val outlineVariant: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inversePrimary: Color,
    val surfaceTint: Color,
    val scrim: Color,
    val shadow: Color
)

/**
 * Local composition để truy cập app colors
 */
val LocalAppColors = staticCompositionLocalOf {
    AppColors(
        primary = Color.Unspecified,
        onPrimary = Color.Unspecified,
        primaryContainer = Color.Unspecified,
        onPrimaryContainer = Color.Unspecified,
        secondary = Color.Unspecified,
        onSecondary = Color.Unspecified,
        secondaryContainer = Color.Unspecified,
        onSecondaryContainer = Color.Unspecified,
        tertiary = Color.Unspecified,
        onTertiary = Color.Unspecified,
        tertiaryContainer = Color.Unspecified,
        onTertiaryContainer = Color.Unspecified,
        error = Color.Unspecified,
        onError = Color.Unspecified,
        errorContainer = Color.Unspecified,
        onErrorContainer = Color.Unspecified,
        background = Color.Unspecified,
        onBackground = Color.Unspecified,
        surface = Color.Unspecified,
        onSurface = Color.Unspecified,
        surfaceVariant = Color.Unspecified,
        onSurfaceVariant = Color.Unspecified,
        outline = Color.Unspecified,
        outlineVariant = Color.Unspecified,
        inverseSurface = Color.Unspecified,
        inverseOnSurface = Color.Unspecified,
        inversePrimary = Color.Unspecified,
        surfaceTint = Color.Unspecified,
        scrim = Color.Unspecified,
        shadow = Color.Unspecified
    )
}

/**
 * Extension function để lấy app colors
 */
@Composable
@ReadOnlyComposable
fun appColors(): AppColors = LocalAppColors.current

/**
 * Extension function để lấy color scheme từ MaterialTheme
 */
@Composable
@ReadOnlyComposable
fun colorScheme(): ColorScheme = MaterialTheme.colorScheme

/**
 * Extension function để check dark theme
 */
@Composable
@ReadOnlyComposable
fun isDarkTheme(): Boolean = isSystemInDarkTheme()

/**
 * Extension function để lấy typography
 */
@Composable
@ReadOnlyComposable
fun appTypography() = MaterialTheme.typography

/**
 * Extension function để lấy shape
 */
@Composable
@ReadOnlyComposable
fun appShape() = MaterialTheme.shapes 