package com.flashlight.flashalert.oncall.sms.ads

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.utils.NetworkMonitor
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView

@SuppressLint("InflateParams")
@Composable
fun NativeAdUninstall(
    adUnitId: String,
    onAdLoaded: () -> Unit = {}
) {
    val context = LocalContext.current
    val isConnected by NetworkMonitor.observeNetwork(context).collectAsState(
        initial = NetworkMonitor.isNetworkConnected(context)
    )

    var reloadTrigger by remember { mutableIntStateOf(0) }
    var lastConnectionState by remember { mutableStateOf(isConnected) }
    var currentAd by remember { mutableStateOf<NativeAd?>(null) }

    LaunchedEffect(isConnected) {
        if (!lastConnectionState && isConnected) {
            reloadTrigger++
        }
        lastConnectionState = isConnected
    }

    DisposableEffect(Unit) {
        onDispose {
            currentAd?.destroy()
        }
    }
    key(reloadTrigger) {
        AndroidView(
            factory = { ctx ->
                LayoutInflater.from(ctx)
                    .inflate(R.layout.view_native_ads_uninstall, null) as FrameLayout
            },
            update = { containerView ->
                val ctx = containerView.context

                val adLoader = AdLoader.Builder(ctx, adUnitId)
                    .forNativeAd { nativeAd ->
                        currentAd = nativeAd
                        Log.d("NativeAd", "✅ Native Ad loaded for adUnitId: $adUnitId")

                        val adView = LayoutInflater.from(ctx)
                            .inflate(
                                R.layout.layout_native_ad_uninstall,
                                containerView,
                                false
                            ) as NativeAdView

                        bindAdToView(nativeAd, adView)

                        containerView.removeAllViews()
                        containerView.addView(adView)

                        onAdLoaded()
                    }
                    .withAdListener(object : AdListener() {
                        override fun onAdFailedToLoad(error: LoadAdError) {
                            Log.e(
                                "NativeAd",
                                "❌ Ad failed to load for adUnitId: $adUnitId, error: ${error.message}"
                            )
                        }
                    })
                    .build()

                adLoader.loadAd(AdRequest.Builder().build())
            },
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .padding(8.dp)
        )
    }
}

fun bindAdToView(nativeAd: NativeAd, adView: NativeAdView) {
    adView.headlineView = adView.findViewById<TextView>(R.id.ad_headline).apply {
        text = nativeAd.headline
    }

    adView.bodyView = adView.findViewById<TextView>(R.id.ad_body).apply {
        nativeAd.body?.let {
            text = it
            visibility = View.VISIBLE
        } ?: run {
            visibility = View.GONE
        }
    }

    adView.iconView = adView.findViewById<ImageView>(R.id.ad_app_icon).apply {
        nativeAd.icon?.let {
            setImageDrawable(it.drawable)
            visibility = View.VISIBLE
        } ?: run {
            visibility = View.GONE
        }
    }

    adView.callToActionView = adView.findViewById<TextView>(R.id.ad_call_to_action).apply {
        nativeAd.callToAction?.let {
            text = it
            visibility = View.VISIBLE
        } ?: run {
            visibility = View.GONE
        }
    }

    adView.setNativeAd(nativeAd)
}
