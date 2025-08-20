package com.flashlight.flashalert.oncall.sms.features.settings.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.CustomToggle
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

@Composable
fun SettingSection(
    title: String,
    subtitle: String? = null,
    icon: Int? = null,
    showToggle: Boolean = false,
    isToggleOn: Boolean = false,
    onToggleChanged: ((Boolean) -> Unit)? = null,
    content: @Composable (() -> Unit)? = null
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFF2F3C55),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                width = 1.dp,
                color = Color(0xFF2A3341),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        if (showToggle) {
            // Single setting with toggle
            Row(
                modifier = Modifier.fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    icon?.let {
                        Image(
                            painter = painterResource(id = it),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }

                    Column {
                        Text(
                            text = title,
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = InterFontFamily
                        )

                        subtitle?.let {
                            Text(
                                text = it,
                                color = Color(0xFFAAAAAA),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = InterFontFamily
                            )
                        }
                    }
                }

                onToggleChanged?.let {
                    CustomToggle(
                        checked = isToggleOn,
                        onCheckedChange = it
                    )
                }
            }
        } else {
            // Section with multiple items
            Column {
                content?.invoke()
            }
        }
    }
}
