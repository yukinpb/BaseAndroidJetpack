package com.flashlight.flashalert.oncall.sms.features.main.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.navigation.compose.rememberNavController
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.ui.components.bottombar.CustomBottomNavigation
import com.ramcosta.composedestinations.DestinationsNavHost
import com.ramcosta.composedestinations.generated.NavGraphs
import com.ramcosta.composedestinations.generated.destinations.CompassScreenDestination
import com.ramcosta.composedestinations.utils.currentDestinationAsState

@Composable
fun MainScreen(
    modifier: Modifier = Modifier
) {
    val navController = rememberNavController()
    val currentDestination by navController.currentDestinationAsState()

    Box {
        DestinationsNavHost(
            navController = navController,
            navGraph = NavGraphs.root
        )

        AnimatedVisibility(
            visible = currentDestination != CompassScreenDestination,
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

                Image(
                    painter = painterResource(id = R.drawable.img_ads),
                    contentDescription = "Logo",
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}