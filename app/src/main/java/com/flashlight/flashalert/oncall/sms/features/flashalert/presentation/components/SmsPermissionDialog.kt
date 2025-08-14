package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily

@Composable
fun SmsPermissionDialog(
    onDismiss: () -> Unit,
    onOpenSettings: () -> Unit
) {
    var currentStep by remember { mutableIntStateOf(1) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White)
                    .padding(16.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = stringResource(R.string.grant_access),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Content based on current step
                    when (currentStep) {
                        1 -> {
                            // Step 1: SMS Access Permission Request
                            Text(
                                text = AnnotatedString.Builder().apply {
                                    append(stringResource(R.string.sms_permission_step1_part1) + " ")
                                    withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                                        append(stringResource(R.string.sms_permission_step1_part2) + " ")
                                    }
                                    append(stringResource(R.string.sms_permission_step1_part3))
                                }.toAnnotatedString(),
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = InterFontFamily
                            )
                        }

                        2 -> {
                            // Step 2: Instruction for Manual Enablement
                            Text(
                                text = stringResource(R.string.sms_manual_enable_instruction),
                                color = Color.Black,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = InterFontFamily,
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        when (currentStep) {
                            1 -> {
                                // Step 1: Cancel and OK buttons
                                // Cancel button
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .border(
                                            width = 1.dp,
                                            color = Color(0xFFE0E2E7),
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .background(
                                            color = Color.White,
                                            shape = RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 12.dp)
                                        .clickableWithoutIndication { onDismiss() }
                                ) {
                                    Text(
                                        text = stringResource(R.string.cancel),
                                        color = Color.Black,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W700,
                                        fontFamily = InterFontFamily,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }

                                // OK button with gradient
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .wrapContentHeight()
                                        .fillMaxWidth()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF12C0FC),
                                                    Color(0xFF1264C8)
                                                )
                                            ),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 12.dp)
                                        .clickableWithoutIndication { currentStep = 2 }
                                ) {
                                    Text(
                                        text = stringResource(R.string.ok),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W700,
                                        fontFamily = InterFontFamily,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }

                            2 -> {
                                // Step 2: Single OK button
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .wrapContentHeight()
                                        .background(
                                            Brush.linearGradient(
                                                colors = listOf(
                                                    Color(0xFF12C0FC),
                                                    Color(0xFF1264C8)
                                                )
                                            ),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(vertical = 12.dp)
                                        .clickableWithoutIndication { onOpenSettings() }
                                ) {
                                    Text(
                                        text = stringResource(R.string.ok),
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.W700,
                                        fontFamily = InterFontFamily,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
} 