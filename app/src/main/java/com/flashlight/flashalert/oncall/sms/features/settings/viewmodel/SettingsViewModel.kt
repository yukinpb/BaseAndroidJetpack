package com.flashlight.flashalert.oncall.sms.features.settings.viewmodel

import android.app.Application
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SettingsState(
    val isAutomaticOn: Boolean = false,
    val appVersion: String = "1.0.0",
    val showRatingDialog: Boolean = false,
    val showSatisfactionDialog: Boolean = false,
    val showThanksForFeedbackDialog: Boolean = false,
    val isGoToFeedback: Boolean = false
)

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val app: Application
) : ViewModel() {

    private val _state = MutableStateFlow(SettingsState())
    val state: StateFlow<SettingsState> = _state.asStateFlow()

    init {
        loadSettings()
    }

    private fun loadSettings() {
        // Load automatic on setting from SharedPrefs
        val isAutomaticOn = SharedPrefs.isAutomaticOn
        val appVersion = try {
            val pInfo = app.packageManager.getPackageInfo(app.packageName, 0)
            pInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            e.printStackTrace()
            "1.0.0"
        }
        _state.value = _state.value.copy(isAutomaticOn = isAutomaticOn, appVersion = appVersion)
    }

    fun toggleAutomaticOn() {
        val newValue = !_state.value.isAutomaticOn
        _state.value = _state.value.copy(isAutomaticOn = newValue)

        // Save to SharedPrefs
        SharedPrefs.isAutomaticOn = newValue
    }

    fun onRatingClick() {
        // Show satisfaction dialog first
        _state.value = _state.value.copy(showSatisfactionDialog = true)
    }

    fun hideRatingDialog() {
        _state.value = _state.value.copy(showRatingDialog = false)
    }

    fun hideSatisfactionDialog() {
        _state.value = _state.value.copy(showSatisfactionDialog = false)
    }

    fun onNotReallyClick() {
        hideSatisfactionDialog()
        onSendFeedback()
    }

    fun onAbsolutelyClick() {
        hideSatisfactionDialog()
        _state.value = _state.value.copy(showRatingDialog = true)
    }

    fun onRateOnGooglePlay() {
        // TODO: Implement rating on Google Play
        hideRatingDialog()
    }

    fun onSendFeedback() {
        hideRatingDialog()
        setGoToFeedback()
    }

    fun setGoToFeedback() {
        _state.value = _state.value.copy(isGoToFeedback = true)
    }

    fun hideThanksForFeedbackDialog() {
        _state.value =
            _state.value.copy(isGoToFeedback = false, showThanksForFeedbackDialog = false)
    }

    fun showThanksForFeedbackScreen() {
        if (_state.value.isGoToFeedback && SharedPrefs.isSubmitFeedback) {
            _state.value =
                _state.value.copy(showThanksForFeedbackDialog = true, isGoToFeedback = false)

            SharedPrefs.isSubmitFeedback = false
        }
    }

    fun onShareClick() {
        val appPackageName = app.packageName
        val appLink = "https://play.google.com/store/apps/details?id=$appPackageName"
//        val shareText = getString(R.string.share_app_message, appLink)
        val shareText = app.getString(R.string.share_app_message, "Coming soon on Google Play!")

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, app.getString(R.string.share_app_subject))
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        val chooserIntent = Intent.createChooser(
            shareIntent,
            app.getString(R.string.share_app_chooser)
        ).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        app.startActivity(chooserIntent)
    }

    fun onPrivacyClick() {
        // Handle privacy action
        viewModelScope.launch {
            // TODO: Implement privacy functionality
        }
    }
}
