package com.flashlight.flashalert.oncall.sms.core.ui.components.bottombar

import androidx.annotation.StringRes
import com.flashlight.flashalert.oncall.sms.R
import com.ramcosta.composedestinations.generated.destinations.CameraScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FlashAlertScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FlashlightScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LedScreenScreenDestination
import com.ramcosta.composedestinations.spec.DirectionDestinationSpec

enum class BottomBarDestination(
    val direction: DirectionDestinationSpec,
    val selectedIcon: Int,
    val unselectedIcon: Int,
    @StringRes val label: Int
) {
    FLASHLIGHT(
        direction = FlashlightScreenDestination,
        selectedIcon = R.drawable.ic_flash_light_selected,
        unselectedIcon = R.drawable.ic_flash_light_unselect,
        label = R.string.flashlight
    ),
    FLASH_ALERT(
        direction = FlashAlertScreenDestination,
        selectedIcon = R.drawable.ic_flash_alert_selected,
        unselectedIcon = R.drawable.ic_flash_alert_unselect,
        label = R.string.flash_alert
    ),
    CAMERA(
        direction = CameraScreenDestination,
        selectedIcon = R.drawable.ic_camera_selected,
        unselectedIcon = R.drawable.ic_camera_unselect,
        label = R.string.camera
    ),
    LED_SCREEN(
        direction = LedScreenScreenDestination,
        selectedIcon = R.drawable.ic_led_screen_selected,
        unselectedIcon = R.drawable.ic_led_screen_unselect,
        label = R.string.led_screen
    )
} 