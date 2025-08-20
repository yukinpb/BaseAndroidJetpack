package com.flashlight.flashalert.oncall.sms.features.intro.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flashlight.flashalert.oncall.sms.ads.NativeAdPreLoaded
import com.flashlight.flashalert.oncall.sms.ads.NativeAdShimmer
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.features.intro.presentation.components.IntroComponent
import com.flashlight.flashalert.oncall.sms.features.intro.viewmodel.IntroPagerPage
import com.flashlight.flashalert.oncall.sms.features.intro.viewmodel.IntroViewModel
import com.flashlight.flashalert.oncall.sms.utils.NetworkMonitor
import com.nlbn.ads.util.Admob
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.FlashlightScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Composable
@Destination<RootGraph>()
fun IntroScreen(
    viewModel: IntroViewModel = viewModel(),
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val pages by viewModel.pages.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { pages.size })
    val currentPage by remember { derivedStateOf { pagerState.currentPage } }

    val preloadedAds by viewModel.preloadedAds.collectAsStateWithLifecycle()
    val adLoadedMap by viewModel.adLoadedMap.collectAsStateWithLifecycle()

    val introPagesCount = pages.count { it is IntroPagerPage.Intro }
    val currentIntroPage = pages.take(currentPage + 1).count { it is IntroPagerPage.Intro } - 1

    LaunchedEffect(Unit) {
        viewModel.preloadAllAds(context, NetworkMonitor.isNetworkConnected(context))
    }

    val handleClick = {
        if (adLoadedMap[currentPage] == true) {
            scope.launch {
                if (currentPage == pages.lastIndex) {
                    SharedPrefs.isFirstInstall = true
                    viewModel.clearAds()
                    navigator.navigate(FlashlightScreenDestination)
                } else {
                    pagerState.animateScrollToPage(currentPage + 1)
                }
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = true
        ) { pageIdx ->
            when (val page = pages[pageIdx]) {
                is IntroPagerPage.Intro -> {
                    val adComposable: @Composable (() -> Unit)? =
                        if (page.pageIndex == 1 || (Admob.getInstance().isLoadFullAds && (page.pageIndex == 0 || page.pageIndex == 2))) {
                            {
                                val ad = preloadedAds[pageIdx]
                                if (ad != null) {
                                    NativeAdPreLoaded(preloadedAd = ad)
                                } else {
                                    NativeAdShimmer()
                                }
                            }
                        } else null

                    IntroComponent(
                        image = page.introItem.image,
                        title = page.introItem.title,
                        text = page.introItem.text,
                        bottomAd = adComposable,
                        showIndicator = true,
                        totalPages = introPagesCount,
                        currentPage = currentIntroPage,
                        onNextClick = handleClick,
                        isLastPage = (pageIdx == pages.lastIndex)
                    )
                }
            }
        }
    }
}

