package com.flashlight.flashalert.oncall.sms.ads

import android.content.Context
import android.util.Log
import com.flashlight.flashalert.oncall.sms.R
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

object AdManager {
    private val preloadedAds = mutableMapOf<String, NativeAd?>()
    private val adLoadedMap = mutableMapOf<String, Boolean>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main)

    fun preloadLanguageAds(context: Context, isConnected: Boolean) {
        if (!isConnected) {
            adLoadedMap["native_language"] = true
            adLoadedMap["native_language_selected"] = true
            return
        }

        val adUnits = listOf(
            context.getString(R.string.native_language),
            context.getString(R.string.native_language_selected)
        )

        adUnits.forEach { adUnit ->
            preloadAd(context, adUnit)
        }
    }

    private fun preloadAd(context: Context, adUnit: String) {
        if (preloadedAds[adUnit] == null && adLoadedMap[adUnit] != true) {
            val adLoader = AdLoader.Builder(context, adUnit)
                .forNativeAd { nativeAd ->
                    preloadedAds[adUnit] = nativeAd
                    adLoadedMap[adUnit] = true
                    Log.d("AdManager", "✅ Ad preloaded for adUnit: $adUnit")
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        preloadedAds[adUnit] = null
                        adLoadedMap[adUnit] = false
                        Log.e("AdManager", "❌ Failed to load ad for adUnit: $adUnit, error: ${error.message}")
                        coroutineScope.launch {
                            delay(3000)
                            preloadAd(context, adUnit)
                        }
                    }
                })
                .build()

            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun getPreloadedAd(adUnit: String): NativeAd? {
        return preloadedAds[adUnit]
    }

    fun isAdLoaded(adUnit: String): Boolean {
        return adLoadedMap[adUnit] == true
    }

    fun loadAdditionalLanguageSelectedAd(context: Context, adUnit: String) {
        if (preloadedAds[adUnit] == null) {
            preloadAd(context, adUnit)
        }
    }

    fun clearLanguageAds() {
        preloadedAds.values.forEach { it?.destroy() }
        preloadedAds.clear()
        adLoadedMap.clear()
    }

    fun clearAllAds() {
        preloadedAds.values.forEach { it?.destroy() }
        preloadedAds.clear()
        adLoadedMap.clear()
    }
} 