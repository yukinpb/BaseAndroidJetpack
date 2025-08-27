package com.flashlight.flashalert.oncall.sms.features.settings.presentation.components

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

data class RatingDialogState(
    val rating: Int = 0,
    val isRated: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RatingDialog(
    onDismiss: () -> Unit,
    onRateClick: () -> Unit,
    onFeedbackClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var ratingState by remember { mutableStateOf(RatingDialogState()) }

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
                    .padding(top = 74.dp)
                    .fillMaxWidth()
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(20.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(74.dp))

                // Title
                val title = when {
                    ratingState.rating == 0 -> stringResource(R.string.thank_you_for_your_support)
                    ratingState.rating <= 2 -> stringResource(R.string.oh_no_we_re_really_sorry)
                    ratingState.rating == 3 -> stringResource(R.string.oh_no_what_a_pity)
                    ratingState.rating == 4 -> stringResource(R.string.truly_grateful)
                    ratingState.rating == 5 -> stringResource(R.string.wow_thank_you_so_much)
                    else -> stringResource(R.string.thank_you_for_your_support)
                }

                Text(
                    text = title,
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W700,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Subtitle
                val subtitle = when {
                    ratingState.rating == 0 -> stringResource(R.string.a_quick_app_review_would_mean_a_lot_to_us)
                    ratingState.rating <= 3 -> stringResource(R.string.we_re_always_ready_to_hear_your_feedback)
                    else -> stringResource(R.string.your_support_is_our_greatest_motivation)
                }

                Text(
                    text = subtitle,
                    color = Color(0xFFA6A6A6),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Stars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .wrapContentHeight(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    for (i in 1..5) {
                        val starRes = if (i <= ratingState.rating) {
                            R.drawable.ic_star_full
                        } else {
                            if (i == 5) {
                                R.drawable.ic_5_star_empty
                            } else {
                                R.drawable.ic_star_empty
                            }
                        }

                        Image(
                            painter = painterResource(id = starRes),
                            contentDescription = "Star $i",
                            modifier = Modifier
                                .size(32.dp)
                                .clickable {
                                    ratingState = ratingState.copy(
                                        rating = i,
                                        isRated = true
                                    )
                                }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Button
                val buttonText = when {
                    ratingState.rating == 0 -> stringResource(R.string.rate)
                    ratingState.rating <= 4 -> stringResource(R.string.send_feedback)
                    else -> stringResource(R.string.rate_on_google_play)
                }

                val buttonTextColor = if (ratingState.isRated) Color.White else Color.Black

                if (ratingState.isRated) {
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
                            .clickable {
                                if (ratingState.isRated) {
                                    if (ratingState.rating <= 4) {
                                        onFeedbackClick()
                                    } else {
                                        onRateClick()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buttonText,
                            color = buttonTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = InterFontFamily
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(bottom = 24.dp, start = 12.dp, end = 12.dp)
                            .fillMaxWidth()
                            .height(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Color(0xFFE0E2E7)
                            )
                            .clickable {
                                if (ratingState.isRated) {
                                    if (ratingState.rating <= 3) {
                                        onFeedbackClick()
                                    } else {
                                        onRateClick()
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = buttonText,
                            color = buttonTextColor,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            }

            // Emoji
            Box(
                modifier = Modifier
                    .fillMaxWidth(),
                contentAlignment = Alignment.TopCenter
            ) {
                val emojiRes = when (ratingState.rating) {
                    0 -> R.drawable.img_0_star
                    1 -> R.drawable.img_1_star
                    2 -> R.drawable.img_2_star
                    3 -> R.drawable.img_3_star
                    4 -> R.drawable.img_4_star
                    5 -> R.drawable.img_5_star
                    else -> R.drawable.img_0_star
                }

                Image(
                    painter = painterResource(id = emojiRes),
                    contentDescription = "Rating Emoji",
                    modifier = Modifier.size(width = 198.dp, height = 154.dp)
                )
            }
        }
    }
}
