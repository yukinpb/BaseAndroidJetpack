package com.flashlight.flashalert.oncall.sms.ads

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.viewinterop.AndroidView
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.utils.AdsUtils
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.util.Admob

@SuppressLint("InflateParams")
@Composable
fun NativeAdPreLoaded(preloadedAd: NativeAd?) {
    if (preloadedAd == null) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            NativeAdShimmer()
        }
    } else {
        AndroidView(
            factory = { ctx ->
                LayoutInflater.from(ctx)
                    .inflate(R.layout.view_native_ads_media_container, null) as FrameLayout
            },
            update = { containerView ->
                val ctx = containerView.context
                val adLayout = R.layout.layout_native_ads

                val adView = LayoutInflater.from(ctx)
                    .inflate(adLayout, containerView, false) as NativeAdView

                AdsUtils.bindToView(preloadedAd, adView)

                containerView.removeAllViews()
                containerView.addView(adView)
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        )
    }
}

