package com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class AdvancedSettingsState(
    val disableWhenPhoneInUse: Boolean = true,
    val batterySaverEnabled: Boolean = true,
    val batteryThreshold: Float = 30f,
    val timeToFlashOffEnabled: Boolean = true,
    val timeFrom: String = "00:00",
    val timeTo: String = "00:00",
    val enableFlashInRingtoneMode: Boolean = true,
    val enableFlashInVibrateMode: Boolean = true,
    val enableFlashInMuteMode: Boolean = true,
    val showTimePickerDialog: Boolean = false,
    val timePickerType: TimePickerType = TimePickerType.NONE
)

enum class TimePickerType {
    NONE, FROM_TIME, TO_TIME
}

class AdvancedSettingsViewModel : ViewModel() {
    private val _state = MutableStateFlow(AdvancedSettingsState())
    val state: StateFlow<AdvancedSettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        viewModelScope.launch {
            _state.value = AdvancedSettingsState(
                disableWhenPhoneInUse = SharedPrefs.disableWhenPhoneInUse,
                batterySaverEnabled = SharedPrefs.batterySaverEnabled,
                batteryThreshold = SharedPrefs.batteryThreshold.toFloat(),
                timeToFlashOffEnabled = SharedPrefs.timeToFlashOffEnabled,
                timeFrom = SharedPrefs.timeFrom,
                timeTo = SharedPrefs.timeTo,
                enableFlashInRingtoneMode = SharedPrefs.enableFlashInRingtoneMode,
                enableFlashInVibrateMode = SharedPrefs.enableFlashInVibrateMode,
                enableFlashInMuteMode = SharedPrefs.enableFlashInMuteMode
            )
        }
    }

    fun showTimePickerDialog(type: TimePickerType) {
        _state.value = _state.value.copy(
            showTimePickerDialog = true,
            timePickerType = type
        )
    }

    fun hideTimePickerDialog() {
        _state.value = _state.value.copy(
            showTimePickerDialog = false,
            timePickerType = TimePickerType.NONE
        )
    }

    fun updateTimeFrom(time: String) {
        viewModelScope.launch {
            // Validate that from time doesn't exceed to time
            if (isValidTimeRange(time, state.value.timeTo)) {
                SharedPrefs.timeFrom = time
                _state.value = _state.value.copy(timeFrom = time)
            }
        }
    }

    fun updateTimeTo(time: String) {
        viewModelScope.launch {
            // Validate that to time isn't less than from time
            if (isValidTimeRange(state.value.timeFrom, time)) {
                SharedPrefs.timeTo = time
                _state.value = _state.value.copy(timeTo = time)
            }
        }
    }

    // Validate time range to prevent invalid configurations
    private fun isValidTimeRange(fromTime: String, toTime: String): Boolean {
        return try {
            val from = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(fromTime)
            val to = SimpleDateFormat("HH:mm", Locale.getDefault()).parse(toTime)
            
            if (from != null && to != null) {
                // Allow same time (no restriction) or valid range
                from == to || from.before(to) || isCrossMidnightRange(from, to)
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }

    // Check if time range crosses midnight (e.g., 22:00 to 06:00)
    private fun isCrossMidnightRange(from: Date, to: Date): Boolean {
        return from.after(to)
    }

    fun updateDisableWhenPhoneInUse(enabled: Boolean) {
        viewModelScope.launch {
            SharedPrefs.disableWhenPhoneInUse = enabled
            _state.value = _state.value.copy(disableWhenPhoneInUse = enabled)
        }
    }

    fun updateBatterySaverEnabled(enabled: Boolean) {
        viewModelScope.launch {
            SharedPrefs.batterySaverEnabled = enabled
            _state.value = _state.value.copy(batterySaverEnabled = enabled)
        }
    }

    fun updateBatteryThreshold(threshold: Float) {
        viewModelScope.launch {
            SharedPrefs.batteryThreshold = threshold.toInt()
            _state.value = _state.value.copy(batteryThreshold = threshold)
        }
    }

    fun updateTimeToFlashOffEnabled(enabled: Boolean) {
        viewModelScope.launch {
            SharedPrefs.timeToFlashOffEnabled = enabled
            _state.value = _state.value.copy(timeToFlashOffEnabled = enabled)
        }
    }

    fun updateEnableFlashInRingtoneMode(enabled: Boolean) {
        viewModelScope.launch {
            SharedPrefs.enableFlashInRingtoneMode = enabled
            _state.value = _state.value.copy(enableFlashInRingtoneMode = enabled)
        }
    }

    fun updateEnableFlashInVibrateMode(enabled: Boolean) {
        viewModelScope.launch {
            SharedPrefs.enableFlashInVibrateMode = enabled
            _state.value = _state.value.copy(enableFlashInVibrateMode = enabled)
        }
    }

    fun updateEnableFlashInMuteMode(enabled: Boolean) {
        viewModelScope.launch {
            SharedPrefs.enableFlashInMuteMode = enabled
            _state.value = _state.value.copy(enableFlashInMuteMode = enabled)
        }
    }

    fun resetToDefault() {
        viewModelScope.launch {
            val defaultState = AdvancedSettingsState()
            _state.value = defaultState
            
            // Update SharedPrefs with default values
            SharedPrefs.disableWhenPhoneInUse = defaultState.disableWhenPhoneInUse
            SharedPrefs.batterySaverEnabled = defaultState.batterySaverEnabled
            SharedPrefs.batteryThreshold = defaultState.batteryThreshold.toInt()
            SharedPrefs.timeToFlashOffEnabled = defaultState.timeToFlashOffEnabled
            SharedPrefs.timeFrom = defaultState.timeFrom
            SharedPrefs.timeTo = defaultState.timeTo
            SharedPrefs.enableFlashInRingtoneMode = defaultState.enableFlashInRingtoneMode
            SharedPrefs.enableFlashInVibrateMode = defaultState.enableFlashInVibrateMode
            SharedPrefs.enableFlashInMuteMode = defaultState.enableFlashInMuteMode
        }
    }

    // Get current battery level for UI display
    fun getCurrentBatteryLevel(): Int {
        return SharedPrefs.batteryThreshold
    }
} 