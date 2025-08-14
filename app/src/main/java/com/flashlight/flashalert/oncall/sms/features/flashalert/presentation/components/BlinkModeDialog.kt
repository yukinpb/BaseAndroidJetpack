package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.BlinkMode

@Composable
fun BlinkModeDialog(
    currentMode: BlinkMode,
    onModeSelected: (BlinkMode) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMode by remember { mutableStateOf(currentMode) }

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
                    .padding(24.dp)
                    .align(Alignment.Center)
            ) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Title
                    Text(
                        text = stringResource(R.string.blink_mode),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Rhythm option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = { selectedMode = BlinkMode.RHYTHM },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (selectedMode == BlinkMode.RHYTHM)
                                    R.drawable.radio_button_selected
                                else
                                    R.drawable.radio_button_unselect
                            ),
                            contentDescription = if (selectedMode == BlinkMode.RHYTHM) "Selected" else "Unselected",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.rhythm),
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = if (selectedMode == BlinkMode.RHYTHM) FontWeight.W600 else FontWeight.W500,
                            fontFamily = InterFontFamily
                        )
                    }

                    // Continuous option
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                onClick = { selectedMode = BlinkMode.CONTINUOUS },
                                indication = null,
                                interactionSource = remember { MutableInteractionSource() }
                            )
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                id = if (selectedMode == BlinkMode.CONTINUOUS)
                                    R.drawable.radio_button_selected
                                else
                                    R.drawable.radio_button_unselect
                            ),
                            contentDescription = if (selectedMode == BlinkMode.CONTINUOUS) "Selected" else "Unselected",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = stringResource(R.string.continuous),
                            color = Color.Black,
                            fontSize = 16.sp,
                            fontWeight = if (selectedMode == BlinkMode.CONTINUOUS) FontWeight.W600 else FontWeight.W500,
                            fontFamily = InterFontFamily
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
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
                                .clickableWithoutIndication {
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp)
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

                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .wrapContentHeight()
                                .fillMaxWidth()
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF12C0FC), Color(0xFF1264C8))
                                    ),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickableWithoutIndication {
                                    onModeSelected(selectedMode)
                                    onDismiss()
                                }
                                .padding(vertical = 12.dp)
                        ) {
                            Text(
                                text = stringResource(R.string.save),
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