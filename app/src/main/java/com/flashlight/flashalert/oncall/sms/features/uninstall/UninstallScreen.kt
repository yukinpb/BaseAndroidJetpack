package com.callapp.callerid.truephone.sms.spamcallblocker.ui.uninstall

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.AppScreen
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.NativeAdUninstall
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.NetworkMonitor
import com.nlbn.ads.util.Admob
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.SplashScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SurveyUninstallScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun UninstallScreen(navigator: DestinationsNavigator) {

    val context = LocalContext.current
    val isConnected by NetworkMonitor.observeNetwork(context).collectAsState(
        initial = NetworkMonitor.isNetworkConnected(context)
    )
    val scrollState = rememberScrollState()

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.bg_flash_alert),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(top = 24.dp, start = 16.dp, end = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.question_uninstall_1),
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(40.dp))
                UninstallReasonItem(
                    text = stringResource(R.string.reason_2),
                    buttonText = stringResource(R.string.restart),
                    onButtonClick = {
                        navigator.navigate(SplashScreenDestination(null))
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
                UninstallReasonItem(
                    text = stringResource(R.string.reason_3),
                    buttonText = stringResource(R.string.explore),
                    onButtonClick = {
                        navigator.navigate(SplashScreenDestination(null))
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                if (isConnected && Admob.getInstance().isLoadFullAds) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .wrapContentSize()
                            .shadow(8.dp, RoundedCornerShape(12.dp), clip = false)
                            .background(
                                Color(0xFF242C3B),
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        NativeAdUninstall(context.getString(R.string.native_keep_user)) {}
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            brush = Brush.linearGradient(
                                listOf(
                                    Color(0xFF12C0FC),
                                    Color(0xFF1264C8)
                                )
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        .clickableWithoutIndication {
                            navigator.navigate(SplashScreenDestination(null))
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.do_not_uninstall),
                        color = Color.White,
                        fontWeight = FontWeight.W800,
                        fontSize = 16.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableWithoutIndication {
                            navigator.navigate(SurveyUninstallScreenDestination)
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.still_uninstall),
                        color = Color(0xFF696C7B),
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        fontFamily = InterFontFamily
                    )
                }
            }
        }
    }
}

@Composable
fun UninstallReasonItem(
    text: String,
    buttonText: String,
    onButtonClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(12.dp))
            .background(
                Color(0xFF2F3C55),
                RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = text,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                color = Color.White
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(
                        brush = Brush.linearGradient(
                            listOf(
                                Color(0xFF12C0FC),
                                Color(0xFF1264C8)
                            )
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) { onButtonClick() },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = buttonText,
                    fontWeight = FontWeight.W700,
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(8.dp)
                )
            }
        }
    }
}
