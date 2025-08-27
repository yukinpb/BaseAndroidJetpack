package com.flashlight.flashalert.oncall.sms.features.settings.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThanksForFeedbackDialog(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color.Transparent,
        dragHandle = null
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
        ) {
            Column(
                modifier = modifier
                    .padding(top = 56.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(56.dp))

                // Title
                Text(
                    text = stringResource(R.string.thanks_for_your_feedback),
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                Text(
                    text = stringResource(R.string.we_strive_to_deliver_a_better_experience),
                    color = Color(0xFFA6A6A6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // OK Button
                Box(
                    modifier = Modifier
                        .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    Color(0xFF12C0FC),
                                    Color(0xFF1264C8)
                                )
                            )
                        )
                        .clickableWithoutIndication { onDismiss() },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.ok),
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.W600,
                        fontFamily = InterFontFamily
                    )
                }
            }

            // Illustration
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_thanks_for_feedback),
                    contentDescription = "Thanks for Feedback Illustration",
                    modifier = Modifier.size(width = 100.dp, height = 100.dp)
                )
            }
        }
    }
}
