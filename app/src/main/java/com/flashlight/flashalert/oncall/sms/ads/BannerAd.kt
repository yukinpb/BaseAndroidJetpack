package com.flashlight.flashalert.oncall.sms.ads

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flashlight.flashalert.oncall.sms.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun BannerAd(idAd: String) {
    val context = LocalContext.current
    var isAdLoaded by remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val adSize = remember(configuration) {
        AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(
            context,
            configuration.screenWidthDp
        )
    }
    val adView = remember { AdView(context) }

    DisposableEffect(Unit) {
        onDispose { adView.destroy() }
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(adSize.height.dp)
    ) {
        if (!isAdLoaded) {
            BannerAdShimmer(adSize.height)
        }

        // Banner Ad
        AndroidView(
            modifier = Modifier
                .fillMaxWidth()
                .height(adSize.height.dp)
                .alpha(if (isAdLoaded) 1f else 0f),
            factory = {
                adView.apply {
                    setAdSize(adSize)
                    adUnitId = idAd

                    adListener = object : AdListener() {
                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            isAdLoaded = true
                        }

                        override fun onAdFailedToLoad(error: LoadAdError) {
                            super.onAdFailedToLoad(error)
                            isAdLoaded = true
                        }
                    }

                    loadAd(AdRequest.Builder().build())
                }
            }
        )
    }
}

@SuppressLint("InflateParams")
@Composable
fun BannerAdShimmer(numberHeight: Int) {
    AndroidView(
        factory = { ctx ->
            LayoutInflater.from(ctx).inflate(R.layout.layout_shimmer_banner, null) as FrameLayout
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(numberHeight.dp)
    )
}