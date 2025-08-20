package com.flashlight.flashalert.oncall.sms.features.intro.viewmodel

import android.content.Context
import android.util.Log
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.features.intro.model.Intro
import com.flashlight.flashalert.oncall.sms.features.intro.model.IntroItem
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.nlbn.ads.util.Admob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class IntroPagerPage {
    @Immutable
    data class Intro(val introItem: IntroItem, val pageIndex: Int) : IntroPagerPage()
}

class IntroViewModel : ViewModel() {
    private val _pages = MutableStateFlow<List<IntroPagerPage>>(emptyList())
    val pages: StateFlow<List<IntroPagerPage>> = _pages.asStateFlow()

    private val _preloadedAds = MutableStateFlow<Map<Int, NativeAd?>>(emptyMap())
    val preloadedAds: StateFlow<Map<Int, NativeAd?>> = _preloadedAds.asStateFlow()

    private val _adLoadedMap = MutableStateFlow<Map<Int, Boolean>>(emptyMap())
    val adLoadedMap: StateFlow<Map<Int, Boolean>> = _adLoadedMap.asStateFlow()

    init {
        updatePages()
    }

    private fun updatePages() {
        val introList = Intro.intros
        val result = mutableListOf<IntroPagerPage>()
        result.add(IntroPagerPage.Intro(introList[0], 0))
        result.add(IntroPagerPage.Intro(introList[1], 1))
        result.add(IntroPagerPage.Intro(introList[2], 2))
        _pages.value = result
        _adLoadedMap.value = result.indices.associateWith { false }
    }

    fun preloadAllAds(context: Context, isConnected: Boolean) {
        updatePages()
        if (!isConnected) {
            _adLoadedMap.value = _pages.value.indices.associateWith { true }
            return
        }
        viewModelScope.launch {
            val adUnitMap = mutableMapOf<Int, String>()

            val isFullAdsMode = Admob.getInstance().isLoadFullAds
            
            val intro1AdUnit = context.getString(R.string.native_intro_1)
            val intro2AdUnit = context.getString(R.string.native_intro_2)
            val intro3AdUnit = context.getString(R.string.native_intro_3)

            _pages.value.forEachIndexed { idx, page ->
                when (page) {
                    is IntroPagerPage.Intro -> {
                        when (page.introItem.index) {
                            0 -> if (isFullAdsMode) adUnitMap[idx] = intro1AdUnit
                            1 -> adUnitMap[idx] = intro2AdUnit // Màn 2 luôn có ads
                            2 -> if (isFullAdsMode) adUnitMap[idx] = intro3AdUnit
                        }
                    }
                }
            }

            // Preload tất cả ads
            adUnitMap.forEach { (index, adUnit) ->
                preloadAd(context, index, adUnit)
            }
            
            // Cập nhật adLoadedMap cho các màn không có ads
            _pages.value.forEachIndexed { idx, page ->
                when (page) {
                    is IntroPagerPage.Intro -> {
                        when (page.introItem.index) {
                            0 -> if (!isFullAdsMode) _adLoadedMap.value += (idx to true)
                            1 -> _adLoadedMap.value += (idx to true) // Màn 2 luôn có ads
                            2 -> if (!isFullAdsMode) _adLoadedMap.value += (idx to true)
                        }
                    }
                }
            }
        }
    }

    private fun preloadAd(context: Context, index: Int, adUnit: String) {
        if (_preloadedAds.value[index] == null && _adLoadedMap.value[index] != true) {
            val adLoader = AdLoader.Builder(context, adUnit)
                .forNativeAd { nativeAd ->
                    _preloadedAds.value += (index to nativeAd)
                    _adLoadedMap.value += (index to true)
                    Log.d("Ad", "✅ Ad preloaded for index $index")
                }
                .withAdListener(object : AdListener() {
                    override fun onAdFailedToLoad(error: LoadAdError) {
                        _preloadedAds.value += (index to null)
                        _adLoadedMap.value += (index to false)
                        Log.e("Ad", "❌ Failed to load ad for index $index: ${error.message}")
                        viewModelScope.launch {
                            delay(3000)
                            preloadAd(context, index, adUnit)
                        }
                    }
                })
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    fun clearAds() {
        _preloadedAds.value.values.forEach { it?.destroy() }
        _preloadedAds.value = emptyMap()
        _adLoadedMap.value = emptyMap()
    }

    override fun onCleared() {
        super.onCleared()
        clearAds()
    }
}