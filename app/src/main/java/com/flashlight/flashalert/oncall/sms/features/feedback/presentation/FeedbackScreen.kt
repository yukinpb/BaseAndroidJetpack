package com.flashlight.flashalert.oncall.sms.features.feedback.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@OptIn(ExperimentalLayoutApi::class)
@Composable
@Destination<RootGraph>()
fun FeedbackScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator
) {
    val selectedProblems = remember { mutableStateListOf<String>() }
    var feedbackText by remember { mutableStateOf("") }

    val flashlightNotTurningOn = stringResource(R.string.flashlight_not_turning_on)
    val appCrashes = stringResource(R.string.app_crashes)
    val hardToUse = stringResource(R.string.hard_to_use)
    val slow = stringResource(R.string.slow)
    val tooManyNotifications = stringResource(R.string.too_many_notifications)
    val other = stringResource(R.string.other)

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        // Background
        Image(
            painter = painterResource(id = R.drawable.bg_flash_alert),
            contentDescription = "Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with back button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back_feedback),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(width = 38.dp, height = 22.dp)
                        .clickable { navigator.navigateUp() }
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Illustration
                Image(
                    painter = painterResource(id = R.drawable.img_feedback),
                    contentDescription = "Feedback Illustration",
                    modifier = Modifier.size(width = 157.dp, height = 125.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Main prompt
                Text(
                    text = stringResource(R.string.please_tell_us_some_of_the_problems_that_you_faced),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(20.dp))


                FlowRow(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    ProblemTag(
                        text = flashlightNotTurningOn,
                        isSelected = selectedProblems.contains(flashlightNotTurningOn),
                        onClick = {
                            if (selectedProblems.contains(flashlightNotTurningOn)) {
                                selectedProblems.remove(flashlightNotTurningOn)
                            } else {
                                selectedProblems.add(flashlightNotTurningOn)
                            }
                        }
                    )
                    ProblemTag(
                        text = appCrashes,
                        isSelected = selectedProblems.contains(appCrashes),
                        onClick = {
                            if (selectedProblems.contains(appCrashes)) {
                                selectedProblems.remove(appCrashes)
                            } else {
                                selectedProblems.add(appCrashes)
                            }
                        }
                    )
                    ProblemTag(
                        text = hardToUse,
                        isSelected = selectedProblems.contains(hardToUse),
                        onClick = {
                            if (selectedProblems.contains(hardToUse)) {
                                selectedProblems.remove(hardToUse)
                            } else {
                                selectedProblems.add(hardToUse)
                            }
                        }
                    )
                    ProblemTag(
                        text = slow,
                        isSelected = selectedProblems.contains(slow),
                        onClick = {
                            if (selectedProblems.contains(slow)) {
                                selectedProblems.remove(slow)
                            } else {
                                selectedProblems.add(slow)
                            }
                        }
                    )
                    ProblemTag(
                        text = tooManyNotifications,
                        isSelected = selectedProblems.contains(tooManyNotifications),
                        onClick = {
                            if (selectedProblems.contains(tooManyNotifications)) {
                                selectedProblems.remove(tooManyNotifications)
                            } else {
                                selectedProblems.add(tooManyNotifications)
                            }
                        }
                    )
                    ProblemTag(
                        text = other,
                        isSelected = selectedProblems.contains(other),
                        onClick = {
                            if (selectedProblems.contains(other)) {
                                selectedProblems.remove(other)
                            } else {
                                selectedProblems.add(other)
                            }
                        }
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Detailed feedback text area
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color(0xFF2A2A2A))
                        .border(
                            width = 1.dp,
                            color = Color(0xFF3B465D),
                            shape = RoundedCornerShape(12.dp)
                        )
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

            // Submit button
            Box(
                modifier = Modifier
                    .padding(bottom = 16.dp)
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
                    .clickableWithoutIndication {
                        SharedPrefs.isSubmitFeedback = true
                        navigator.navigateUp()
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.submit),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )
            }
        }
    }
}

@Composable
private fun ProblemTag(
    text: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (isSelected) {
        Box(
            modifier = modifier
                .padding(2.dp)
                .wrapContentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(0xFF12C0FC),
                            Color(0xFF1264C8)
                        )
                    )
                )
                .clickable { onClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = InterFontFamily,
                textAlign = TextAlign.Center
            )
        }
    } else {
        Box(
            modifier = modifier
                .padding(2.dp)
                .wrapContentSize()
                .clip(RoundedCornerShape(8.dp))
                .background(
                    Color(0xFF2F3C55)
                )
                .clickable { onClick() }
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                color = Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.W600,
                fontFamily = InterFontFamily,
                textAlign = TextAlign.Center
            )
        }
    }
}
