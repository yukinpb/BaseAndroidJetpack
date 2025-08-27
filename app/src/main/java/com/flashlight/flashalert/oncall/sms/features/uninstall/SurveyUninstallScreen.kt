package com.flashlight.flashalert.oncall.sms.features.uninstall

import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import androidx.core.net.toUri
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
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun SurveyUninstallScreen(navigator: DestinationsNavigator) {
    val context = LocalContext.current
    var indexSelected by remember { mutableIntStateOf(0) }
    val isConnected by NetworkMonitor.observeNetwork(context).collectAsState(
        initial = NetworkMonitor.isNetworkConnected(context)
    )
    val scrollState = rememberScrollState()
    var feedbackText by remember { mutableStateOf("") }

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
                        text = stringResource(R.string.question_uninstall_2),
                        fontWeight = FontWeight.W600,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(325.dp),
                        color = Color.White
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                SurveyReasonItem(
                    text = stringResource(R.string.reason_1),
                    selected = indexSelected == 1,
                    onClick = { indexSelected = 1 }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SurveyReasonItem(
                    text = stringResource(R.string.reason_2),
                    selected = indexSelected == 2,
                    onClick = { indexSelected = 2 }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SurveyReasonItem(
                    text = stringResource(R.string.reason_3),
                    selected = indexSelected == 3,
                    onClick = { indexSelected = 3 }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SurveyReasonItem(
                    text = stringResource(R.string.reason_4),
                    selected = indexSelected == 4,
                    onClick = { indexSelected = 4 }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SurveyReasonItem(
                    text = stringResource(R.string.reason_5),
                    selected = indexSelected == 5,
                    onClick = { indexSelected = 5 }
                )
                if (indexSelected == 5) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color(0xFF2F3C55))
                            .padding(16.dp)
                    ) {
                        BasicTextField(
                            value = feedbackText,
                            onValueChange = { feedbackText = it },
                            textStyle = androidx.compose.ui.text.TextStyle(
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Normal,
                                fontFamily = InterFontFamily
                            ),
                            modifier = Modifier.fillMaxSize(),
                            decorationBox = { innerTextField ->
                                if (feedbackText.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.please_provide_more_details_so_we_can_identify_and_resolve_your_issue_faster),
                                        color = Color.Gray,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Normal,
                                        fontFamily = InterFontFamily
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                }

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
                        NativeAdUninstall(context.getString(R.string.native_survey_user)) {}
                    }
                    Spacer(modifier = Modifier.height(24.dp))
                }

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
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
                            openAppSettings(context)
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.uninstall),
                        color = Color.White,
                        fontWeight = FontWeight.W800,
                        fontSize = 16.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickableWithoutIndication {
                            navigator.navigate(SplashScreenDestination(null))
                        },
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        color = Color(0xFF696C7B),
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        fontFamily = InterFontFamily
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
fun SurveyReasonItem(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    if (selected) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(4.dp, RoundedCornerShape(12.dp))
                .background(
                    Color(0xFF15223B),
                    RoundedCornerShape(12.dp)
                )
                .border(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        listOf(
                            Color(0xFF12C0FC),
                            Color(0xFF1264C8)
                        )
                    ),
                    shape = RoundedCornerShape(12.dp)
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
                    fontWeight = FontWeight.W500,
                    fontFamily = InterFontFamily,
                    color = Color.White
                )
                Image(
                    painterResource(R.drawable.ic_language_selected),
                    contentDescription = "Unselect checkbox",
                    modifier = Modifier
                        .size(18.dp)
                        .clickableWithoutIndication { onClick() }
                )
            }
        }
    } else {
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
                    fontWeight = FontWeight.W500,
                    fontFamily = InterFontFamily,
                    color = Color.White
                )
                Image(
                    painterResource(R.drawable.ic_language_select),
                    contentDescription = "Unselect checkbox",
                    modifier = Modifier
                        .size(18.dp)
                        .clickableWithoutIndication { onClick() }
                )
            }
        }
    }
}

private fun openAppSettings(context: Context) {
    try {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = "package:${context.packageName}".toUri()
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        context.startActivity(intent)
    } catch (e: Exception) {
        e.printStackTrace()
        try {
            val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(fallbackIntent)
        } catch (ex: Exception) {
            ex.printStackTrace()
            Toast.makeText(
                context,
                "Unable to open Settings. Please open the Settings tool.",
                Toast.LENGTH_LONG
            ).show()
        }
    }
}
