package com.flashlight.flashalert.oncall.sms.features.flashlight.viewmodel

enum class FlashlightMode {
    NONE,
    FLASHLIGHT,
    SOS,
    STROBE
}

enum class FlashlightState {
    OFF,
    ON
}

data class FlashlightScreenState(
    val selectedMode: FlashlightMode = FlashlightMode.NONE,
    val currentState: FlashlightState = FlashlightState.OFF,
    val strobeSpeed: Float = 10f,
    val compassAngle: Float = 0f
) 