package com.flashlight.flashalert.oncall.sms.features.language.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R

@Composable
fun LanguageComponent(
    modifier: Modifier = Modifier,
    flag: Int,
    display: Int,
    checked: Boolean,
    onCheckedChange: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.dp, Brush.verticalGradient(
                    listOf(Color.White.copy(0.25f), Color.White.copy(0.25f))
                ), RoundedCornerShape(16.dp)
            )
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF2F3C55))
            .padding(12.dp)
            .clickable(
                onClick = onCheckedChange,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = flag),
                    contentDescription = stringResource(display),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = stringResource(display),
                    fontSize = 14.sp,
                    color = Color.White,
                    fontWeight = FontWeight.W500
                )
            }
            if (checked) {
                Image(
                    painter = painterResource(id = R.drawable.ic_language_selected),
                    contentDescription = "Icon Selected",
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_language_select),
                    contentDescription = "Icon Select",
                    modifier = Modifier
                        .size(24.dp)
                        .clickable(
                            onClick = onCheckedChange,
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        )
                )
            }
        }
    }
    Spacer(modifier = Modifier.height(16.dp))
}

@Preview(showBackground = true)
@Composable
fun PreviewLanguageComponent() {
    LanguageComponent(
        flag = R.drawable.chinna_flag, // Replace with an actual flag drawable
        display = R.string.language_china, // Replace with an actual string resource
        checked = true,
        onCheckedChange = {}
    )
}
