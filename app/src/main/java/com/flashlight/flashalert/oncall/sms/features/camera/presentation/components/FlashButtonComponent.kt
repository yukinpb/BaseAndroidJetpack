package com.flashlight.flashalert.oncall.sms.features.camera.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R

@Composable
fun FlashButtonComponent(
    isFlashOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isFlashOn) {
        // Flash ON state with gradient background
        Box(
            modifier = modifier
                .size(80.dp, 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(Color(0xFF12C0FC), Color(0xFF1264C8))
                    )
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_flash_camera),
                    contentDescription = "Flash",
                    modifier = Modifier.size(20.dp, 18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "ON",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600
                )
            }
        }
    } else {
        // Flash OFF state with solid color background and border
        Box(
            modifier = modifier
                .size(80.dp, 40.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(Color(0xFF242424))
                .border(
                    width = 1.dp,
                    color = Color(0xFF4B4B4B),
                    shape = RoundedCornerShape(20.dp)
                )
                .clickable { onToggle() },
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_flash_camera),
                    contentDescription = "Flash",
                    modifier = Modifier.size(20.dp, 18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "OFF",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W600
                )
            }
        }
    }
}
