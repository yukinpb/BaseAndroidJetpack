package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

@Composable
fun FlashSpeedComponent(
    flashSpeed: Float,
    onFlashSpeedChange: (Float) -> Unit,
    onResetToDefault: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.flash_speed),
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = InterFontFamily
            )
            Box(
                modifier = Modifier
                    .wrapContentSize()
                    .clickable { onResetToDefault() }
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.White.copy(alpha = 0.2f))
                    .padding(horizontal = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.default_text),
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = InterFontFamily
                )
            }
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        // Flash speed slider
        MillisecondsSlider(
            value = flashSpeed,
            onValueChange = onFlashSpeedChange,
            modifier = Modifier
                .fillMaxWidth()
        )
    }
} 