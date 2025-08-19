package com.flashlight.flashalert.oncall.sms.features.ledscreen.viewmodel

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LedScreenState(
    val currentMode: LedScreenMode = LedScreenMode.STATIC,
    val currentColor: Color = Color.White,
    val brightness: Float = 1.0f,
    val isAnimating: Boolean = false,
    val isBlueTop: Boolean = true  // Track which color is on top in TWO_COLOR mode
)

enum class LedScreenMode {
    STATIC,
    CONTINUOUS,
    TWO_COLOR
}

sealed class LedScreenEvent {
    object EnterStaticMode : LedScreenEvent()
    object EnterContinuousMode : LedScreenEvent()
    object EnterTwoColorMode : LedScreenEvent()
    data class UpdateColor(val color: Color) : LedScreenEvent()
    data class UpdateBrightness(val brightness: Float) : LedScreenEvent()
}

@HiltViewModel
class LedScreenViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(LedScreenState())
    val state: StateFlow<LedScreenState> = _state.asStateFlow()

    init {
        // Load saved settings from SharedPrefs
        loadSavedSettings()
    }

    private fun loadSavedSettings() {
        val savedColor = SharedPrefs.ledScreenColor
        val savedBrightness = SharedPrefs.ledScreenBrightness

        val parsedColor = try {
            val colorInt = savedColor.toLong(16).toInt()
            Color(colorInt)
        } catch (e: Exception) {
            e.printStackTrace()
            Color.White
        }

        _state.value = _state.value.copy(
            currentColor = parsedColor,
            brightness = savedBrightness
        )
    }

    fun handleEvent(event: LedScreenEvent) {
        when (event) {
            is LedScreenEvent.EnterStaticMode -> {
                enterStaticMode()
            }

            is LedScreenEvent.EnterContinuousMode -> {
                enterContinuousMode()
            }

            is LedScreenEvent.EnterTwoColorMode -> {
                enterTwoColorMode()
            }

            is LedScreenEvent.UpdateColor -> {
                updateColor(event.color)
            }

            is LedScreenEvent.UpdateBrightness -> {
                updateBrightness(event.brightness)
            }
        }
    }

    private fun enterStaticMode() {
        _state.value = _state.value.copy(
            currentMode = LedScreenMode.STATIC,
            isAnimating = false
        )
    }

    private fun enterContinuousMode() {
        if (_state.value.currentMode == LedScreenMode.CONTINUOUS && _state.value.isAnimating) {
            closeAnimation()
            return
        }

        _state.value = _state.value.copy(
            currentMode = LedScreenMode.CONTINUOUS,
            isAnimating = true
        )
        startContinuousColorAnimation()
    }

    private fun enterTwoColorMode() {
        if (_state.value.currentMode == LedScreenMode.TWO_COLOR && _state.value.isAnimating) {
            closeAnimation()
            return
        }

        _state.value = _state.value.copy(
            currentMode = LedScreenMode.TWO_COLOR,
            isAnimating = true,
            isBlueTop = true  // Reset to blue on top
        )
        startTwoColorAnimation()
    }

    private fun closeAnimation() {
        _state.value = _state.value.copy(
            currentMode = LedScreenMode.STATIC,
            isAnimating = false
        )
        loadSavedSettings()
    }

    private fun updateColor(color: Color) {
        _state.value = _state.value.copy(currentColor = color)

        // Save to SharedPrefs
        val colorString = String.format("%08X", color.toArgb())
        SharedPrefs.ledScreenColor = colorString
    }

    private fun updateBrightness(brightness: Float) {
        _state.value = _state.value.copy(brightness = brightness)

        // Save to SharedPrefs
        SharedPrefs.ledScreenBrightness = brightness
    }

    private fun startContinuousColorAnimation() {
        viewModelScope.launch {
            val colors = listOf(
                Color.Red, Color.Green, Color.Blue, Color.Yellow,
                Color.Cyan, Color.Magenta, Color.White, Color.Black
            )

            var colorIndex = 0
            while (_state.value.currentMode == LedScreenMode.CONTINUOUS && _state.value.isAnimating) {
                _state.value = _state.value.copy(currentColor = colors[colorIndex])
                colorIndex = (colorIndex + 1) % colors.size
                delay(500) // Change color every 500ms
            }
        }
    }

    private fun startTwoColorAnimation() {
        viewModelScope.launch {
            while (_state.value.currentMode == LedScreenMode.TWO_COLOR && _state.value.isAnimating) {
                // Toggle which color is on top
                _state.value = _state.value.copy(
                    isBlueTop = !_state.value.isBlueTop
                )
                delay(200) // Switch colors every 200ms
            }
        }
    }
}
