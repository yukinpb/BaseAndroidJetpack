package com.flashlight.flashalert.oncall.sms.ads

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.flashlight.flashalert.oncall.sms.R

@SuppressLint("InflateParams")
@Composable
fun NativeAdsNoMediaShimmer() {
    AndroidView(
        factory = { ctx ->
            LayoutInflater.from(ctx)
                .inflate(R.layout.view_native_ad_no_media_container, null) as FrameLayout
        },
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
    )
}