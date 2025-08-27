package com.flashlight.flashalert.oncall.sms.utils

import android.app.Activity
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.flashlight.flashalert.oncall.sms.R
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdView
import com.nlbn.ads.callback.AdCallback
import com.nlbn.ads.util.Admob
import com.nlbn.ads.util.ConsentHelper

object AdsUtils {
    fun bindToView(nativeAd: NativeAd, adView: NativeAdView) {
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

        adView.callToActionView = adView.findViewById<TextView>(R.id.ad_call_to_action).apply {
            nativeAd.callToAction?.let {
                text = it
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        adView.iconView = adView.findViewById<ImageView>(R.id.ad_app_icon)?.apply {
            nativeAd.icon?.let {
                setImageDrawable(it.drawable)
                visibility = View.VISIBLE
            } ?: run {
                visibility = View.GONE
            }
        }

        adView.setNativeAd(nativeAd)
    }

    fun loadAndDisplayInter(
        context: Activity,
        adUnitId: String,
        enabled: Boolean = Admob.getInstance().isLoadFullAds,
        onNextAction: () -> Unit
    ) {
        if (!ConsentHelper.getInstance(context).canRequestAds()) {
            onNextAction.invoke()
            return
        }

        if (!enabled) {
            onNextAction.invoke()
            return
        }

        val callback = object : AdCallback() {
            override fun onNextAction() {
                super.onNextAction()
                onNextAction.invoke()
            }

            override fun onAdFailedToLoad(p0: LoadAdError?) {
                super.onAdFailedToLoad(p0)
                onNextAction.invoke()
            }
        }
        Admob.getInstance().loadAndShowInter(context, adUnitId, 100, 100, callback)
    }
}