package com.flashlight.flashalert.oncall.sms.features.settings.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.features.settings.presentation.components.RatingDialog
import com.flashlight.flashalert.oncall.sms.features.settings.presentation.components.SettingItem
import com.flashlight.flashalert.oncall.sms.features.settings.presentation.components.SettingSection
import com.flashlight.flashalert.oncall.sms.features.settings.presentation.components.ThanksForFeedbackDialog
import com.flashlight.flashalert.oncall.sms.features.settings.viewmodel.SettingsViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.FeedbackScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LanguageScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Monitor lifecycle events to show thanks for feedback dialog
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.showThanksForFeedbackScreen()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.bg_flash_alert),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back button
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable { navigator.navigateUp() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.settings),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Automatic On Section
            SettingSection(
                title = stringResource(R.string.automatic_on),
                subtitle = stringResource(R.string.turn_on_flashlight_on_startup),
                icon = R.drawable.ic_flash_camera,
                showToggle = true,
                isToggleOn = state.isAutomaticOn,
                onToggleChanged = { viewModel.toggleAutomaticOn() }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // General Settings Section
            SettingSection(
                title = "General Settings",
                showToggle = false
            ) {
                SettingItem(
                    icon = R.drawable.ic_language_setting,
                    title = stringResource(R.string.language),
                    onClick = { navigator.navigate(LanguageScreenDestination(true)) }
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF3B465D))
                )

                SettingItem(
                    icon = R.drawable.ic_rating_setting,
                    title = stringResource(R.string.rating),
                    onClick = { viewModel.onRatingClick() }
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF3B465D))
                )

                SettingItem(
                    icon = R.drawable.ic_share_setting,
                    title = stringResource(R.string.share_to_friends),
                    onClick = { viewModel.onShareClick() }
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                        .height(1.dp)
                        .background(Color(0xFF3B465D))
                )

                SettingItem(
                    icon = R.drawable.ic_privacy_setting,
                    title = stringResource(R.string.privacy),
                    onClick = { viewModel.onPrivacyClick() }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            // Version
            Text(
                text = "Ver ${state.appVersion}",
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Normal,
                fontFamily = InterFontFamily
            )
        }

        // Rating Dialog
        if (state.showRatingDialog) {
            RatingDialog(
                onDismiss = { viewModel.hideRatingDialog() },
                onRateClick = { viewModel.onRateOnGooglePlay() },
                onFeedbackClick = {
                    viewModel.onSendFeedback()
                    navigator.navigate(FeedbackScreenDestination)
                }
            )
        }

        // Thanks for Feedback Dialog
        if (state.showThanksForFeedbackDialog) {
            ThanksForFeedbackDialog(
                onDismiss = { viewModel.hideThanksForFeedbackDialog() }
            )
        }
    }
}
