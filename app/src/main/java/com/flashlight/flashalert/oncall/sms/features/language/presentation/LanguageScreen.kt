package com.flashlight.flashalert.oncall.sms.features.language.presentation

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.AdManager
import com.flashlight.flashalert.oncall.sms.ads.NativeAd
import com.flashlight.flashalert.oncall.sms.ads.NativeAdPreLoaded
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.language.model.Languages
import com.flashlight.flashalert.oncall.sms.features.language.presentation.components.LanguageComponent
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.LocaleHelper
import com.flashlight.flashalert.oncall.sms.utils.NetworkMonitor
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.IntroScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import kotlinx.coroutines.launch

@Composable
@Destination<RootGraph>()
fun LanguageScreen(
    isFromSetting: Boolean = false,
    navigator: DestinationsNavigator
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var languageSelected by remember { mutableStateOf(SharedPrefs.languageCode) }
    var englishExpanded by remember { mutableStateOf(false) }
    val isConnected by NetworkMonitor.observeNetwork(context).collectAsState(
        initial = NetworkMonitor.isNetworkConnected(context)
    )
    val languageSelectedCount = remember { mutableIntStateOf(0) }

    // Load additional language selected ad when user changes language
    LaunchedEffect(languageSelected) {
        if (languageSelected.isNotEmpty() && isConnected) {
            val adUnit = context.getString(R.string.native_language_selected)
            AdManager.loadAdditionalLanguageSelectedAd(context, adUnit)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Image(
            painter = painterResource(id = R.drawable.bg_flash_alert),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
            ) {
                // Header Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = stringResource(R.string.language),
                            fontSize = 18.sp,
                            lineHeight = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.W700
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = stringResource(R.string.extra_language),
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.W400,
                            fontSize = 12.sp,
                            color = Color.White,
                            maxLines = 2
                        )
                    }
                    if (languageSelected == "") {
                        Box(
                            modifier = Modifier
                                .background(
                                    color = Color(0xFFAAAAAA),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        ) {
                            Text(
                                text = stringResource(R.string.done),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W500,
                                fontFamily = InterFontFamily,
                                modifier = Modifier
                                    .padding(vertical = 2.dp, horizontal = 8.dp)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .background(
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFF12C0FC),
                                            Color(0xFF1264C8)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(
                                            Color(0xFF12C0FC),
                                            Color(0xFF1264C8)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickableWithoutIndication {
                                    coroutineScope.launch {
                                        SharedPrefs.languageCode = languageSelected
                                        LocaleHelper.setLocale(context, languageSelected)
                                        (context as? Activity)?.recreate()
                                        if (isFromSetting) {
                                            navigator.navigateUp()
                                        } else {
                                            navigator.navigate(IntroScreenDestination)
                                        }
                                    }
                                }
                        ) {
                            Text(
                                text = stringResource(R.string.done),
                                fontSize = 12.sp,
                                color = Color.White,
                                fontWeight = FontWeight.W500,
                                fontFamily = InterFontFamily,
                                modifier = Modifier
                                    .padding(vertical = 2.dp, horizontal = 8.dp)
                            )
                        }
                    }

                }
                Spacer(modifier = Modifier.height(20.dp))
            }
            Box(modifier = Modifier.weight(1f, fill = true)) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 16.dp)
                ) {
                    Languages.languages.forEach { group ->
                        if (group.code == "en") {
                            // English group
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .wrapContentSize()
                                    .border(
                                        1.dp, Brush.verticalGradient(
                                            listOf(Color.White.copy(0.25f), Color.White.copy(0.25f))
                                        ), RoundedCornerShape(16.dp)
                                    )
                                    .background(
                                        Color(0xFF2F3C55),
                                        RoundedCornerShape(16.dp)
                                    )
                                    .clickable { englishExpanded = !englishExpanded }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Image(
                                            painter = painterResource(id = group.flagRes),
                                            contentDescription = stringResource(group.displayName),
                                            modifier = Modifier.size(32.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = stringResource(group.displayName),
                                            fontSize = 14.sp,
                                            color = Color.White,
                                            fontWeight = FontWeight.W500
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        // Show all flags
                                        group.children.forEach {
                                            Image(
                                                painter = painterResource(id = it.flagRes),
                                                contentDescription = stringResource(it.displayName),
                                                modifier = Modifier
                                                    .size(20.dp)
                                                    .padding(end = 2.dp)
                                            )
                                        }
                                    }
                                    Image(
                                        painter = painterResource(id = if (englishExpanded) R.drawable.ic_language_expanded else R.drawable.ic_language_expand),
                                        contentDescription = "Expand",
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            if (englishExpanded) {
                                group.children.forEach { child ->
                                    LanguageComponent(
                                        flag = child.flagRes,
                                        display = child.displayName,
                                        checked = languageSelected == child.code,
                                        onCheckedChange = {
                                            languageSelected = child.code
                                            languageSelectedCount.intValue++
                                        },
                                        modifier = Modifier.padding(start = 16.dp)
                                    )
                                }
                            }
                        } else {
                            LanguageComponent(
                                flag = group.flagRes,
                                display = group.displayName,
                                checked = languageSelected == group.code,
                                onCheckedChange = {
                                    languageSelected = group.code
                                    languageSelectedCount.intValue++
                                    englishExpanded = false
                                }
                            )
                        }
                    }
                }
            }
            if (isConnected) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .background(color = Color(0xFF242C3B)),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    if (isFromSetting) {
                        NativeAd(adUnitId = stringResource(R.string.native_language_setting))
                    } else {
                        if (languageSelected.isEmpty()) {
                            // Show preloaded native_language ad
                            val preloadedAd =
                                AdManager.getPreloadedAd(context.getString(R.string.native_language))
                            NativeAdPreLoaded(
                                preloadedAd = preloadedAd
                            )
                        } else if (languageSelectedCount.intValue < 2) {
                            // Show preloaded native_language_selected ad
                            val preloadedAd =
                                AdManager.getPreloadedAd(context.getString(R.string.native_language_selected))
                            NativeAdPreLoaded(
                                preloadedAd = preloadedAd
                            )
                        } else {
                            NativeAd(
                                adUnitId = stringResource(R.string.native_language_selected),
                                languageSelectedCount = languageSelectedCount.intValue
                            )
                        }
                    }
                }
            }
        }
    }

}