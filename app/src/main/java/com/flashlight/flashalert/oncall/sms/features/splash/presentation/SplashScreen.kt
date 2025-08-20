package com.flashlight.flashalert.oncall.sms.features.splash.presentation

import android.app.Activity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.LottieAnimationView
import com.flashlight.flashalert.oncall.sms.AppScreen
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.AdManager
import com.flashlight.flashalert.oncall.sms.ads.BannerAd
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.features.splash.viewmodel.SplashViewModel
import com.flashlight.flashalert.oncall.sms.utils.NetworkMonitor
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.LoadAdError
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.AppOpenManager
import com.nlbn.ads.util.ConsentHelper
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.IntroScreenDestination
import com.ramcosta.composedestinations.generated.destinations.LanguageScreenDestination
import com.ramcosta.composedestinations.generated.destinations.UninstallScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.delay

@Composable
@Destination<RootGraph>(start = true)
fun SplashScreen(
    from: String? = null,
    navigator: DestinationsNavigator,
    viewModel: SplashViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val isConnected by NetworkMonitor.observeNetwork(context).collectAsState(
        initial = NetworkMonitor.isNetworkConnected(context)
    )

    val isFlashOn by viewModel.isFlashOn.collectAsStateWithLifecycle()

    // Flash tự động khi vào splash
    DisposableEffect(Unit) {
        viewModel.turnOnFlashlightIfEnabled()

        onDispose {
            // Tắt flash khi rời khỏi splash
            viewModel.turnOffFlashlight()
        }
    }

    val openAdId = if (from == AppScreen.FROM_UNINSTALL) {
        context.getString(R.string.open_splash_uninstall)
    } else {
        context.getString(R.string.open_splash)
    }

    fun navigateToNext() {
        if (from == AppScreen.FROM_UNINSTALL) {
            navigator.navigate(UninstallScreenDestination)
        } else if (SharedPrefs.languageCode == "") {
            navigator.navigate(LanguageScreenDestination(false))
        } else {
            navigator.navigate(IntroScreenDestination)
        }
    }

    LaunchedEffect(isConnected) {
        if (isConnected) {
            // Preload language ads
            AdManager.preloadLanguageAds(context, isConnected)

            Admob.getInstance().setOpenActivityAfterShowInterAds(false)
            val consentHelper = ConsentHelper.getInstance(context)
            if (!consentHelper.canLoadAndShowAds()) {
                consentHelper.reset()
            }
            consentHelper.obtainConsentAndShow(context as Activity?) {
                // Load interstitial ad
                val adCallback = object : AdCallback() {
                    override fun onNextAction() {
                        super.onNextAction()
                        navigateToNext()
                    }

                    override fun onAdFailedToLoad(p0: LoadAdError?) {
                        super.onAdFailedToLoad(p0)
                        navigateToNext()
                    }

                    override fun onAdFailedToShow(p0: AdError?) {
                        super.onAdFailedToShow(p0)
                        navigateToNext()
                    }
                }
                try {
                    AppOpenManager.getInstance().loadOpenAppAdSplash(
                        context,
                        openAdId,
                        0,
                        2000,
                        true,
                        adCallback
                    )
                } catch (e: Exception) {
                    navigateToNext()
                }
            }
        } else {
            delay(2000)
            navigateToNext()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { context ->
                LottieAnimationView(context).apply {
                    setAnimation(R.raw.splash_screen)
                    playAnimation()
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AndroidView(
                factory = { context ->
                    LottieAnimationView(context).apply {
                        setAnimation(R.raw.anim_loading)
                        playAnimation()
                    }
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = stringResource(id = R.string.splash_des),
                fontSize = 12.sp,
                fontWeight = FontWeight.W400,
                lineHeight = 16.sp,
                color = Color.White
            )

            Spacer(modifier = Modifier.height(8.dp))

            if (Admob.getInstance().isLoadFullAds) {
                BannerAd(context.getString(R.string.banner_splash_uninstall))
            }
        }
    }
}


