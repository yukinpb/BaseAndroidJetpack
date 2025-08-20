package com.flashlight.flashalert.oncall.sms.features.intro.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.airbnb.lottie.LottieAnimationView
import com.airbnb.lottie.LottieDrawable
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

@Composable
fun IntroComponent(
    image: Int,
    title: Int,
    text: Int,
    bottomAd: (@Composable () -> Unit)? = null,
    showIndicator: Boolean = false,
    totalPages: Int = 0,
    currentPage: Int = 0,
    onNextClick: (() -> Unit)? = null,
    isLastPage: Boolean = false
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = image),
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            alignment = Alignment.TopCenter,
            modifier = Modifier
                .fillMaxSize()
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = stringResource(title),
                        color = Color.White,
                        fontWeight = FontWeight.W700,
                        fontSize = 20.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = stringResource(text),
                        color = Color.White,
                        fontWeight = FontWeight.W400,
                        fontSize = 14.sp,
                        fontFamily = InterFontFamily,
                        modifier = Modifier.padding(vertical = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (showIndicator) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row {
                        (0 until totalPages).forEach { i ->
                            Image(
                                painter = painterResource(
                                    if (currentPage == i) R.drawable.img_index_selected else R.drawable.img_index
                                ),
                                contentDescription = null,
                                modifier = Modifier.padding(end = 4.dp)
                            )
                        }
                    }
                    Text(
                        text = if (!isLastPage) stringResource(R.string.next) else stringResource(R.string.start),
                        fontSize = 14.sp,
                        color = Color.White,
                        fontWeight = FontWeight.W600,
                        fontFamily = InterFontFamily,
                        modifier = Modifier
                            .clickableWithoutIndication {
                                onNextClick?.invoke()
                            }
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(Color(0xFF12C0FC), Color(0xFF1264C8))
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(vertical = 4.dp, horizontal = 12.dp)
                    )
                }
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.6f),
                contentAlignment = Alignment.Center
            ) {
                if (bottomAd != null) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                color = Color(0xFF242C3B),
                                shape = RoundedCornerShape(16.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        bottomAd.invoke()
                    }
                } else {
                    AndroidView(
                        factory = { context ->
                            LottieAnimationView(context).apply {
                                setAnimation(R.raw.anim_swipe_right)
                                repeatCount = LottieDrawable.INFINITE
                                playAnimation()
                            }
                        }
                    )
                }
            }
        }
    }
}