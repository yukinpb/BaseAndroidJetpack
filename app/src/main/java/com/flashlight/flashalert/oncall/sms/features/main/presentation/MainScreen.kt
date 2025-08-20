package com.flashlight.flashalert.oncall.sms.features.main.presentation

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.navigation.compose.rememberNavController
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.NativeAdNoMedia
import com.flashlight.flashalert.oncall.sms.core.ui.components.bottombar.CustomBottomNavigation
import com.nlbn.ads.util.Admob
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.FlashAlertScreenDestination
import com.ramcosta.composedestinations.generated.destinations.FlashlightScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SplashScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState

@Composable
fun MainScreen(
    shortcutAction: String? = null
) {
    val navController = rememberNavController()
    val currentDestination by navController.currentDestinationAsState()

    val listDestinationVisibleBottomBar = listOf(
        FlashlightScreenDestination,
        FlashAlertScreenDestination
    )

    Box {
        DestinationsNavHost(
            navController = navController,
            navGraph = NavGraphs.root,
            start = SplashScreenDestination(shortcutAction)
        )

        AnimatedVisibility(
            visible = currentDestination in listDestinationVisibleBottomBar,
            exit = fadeOut(animationSpec = tween(300)) + slideOutVertically(
                animationSpec = tween(300),
                targetOffsetY = { it }
            ),
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Column {
                CustomBottomNavigation(
                    navController = navController
                )

                if (Admob.getInstance().isLoadFullAds) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFF242C3B))
                    )
                    {
                        NativeAdNoMedia(stringResource(R.string.native_navbar)) { }
                    }
                }
            }
        }
    }
}