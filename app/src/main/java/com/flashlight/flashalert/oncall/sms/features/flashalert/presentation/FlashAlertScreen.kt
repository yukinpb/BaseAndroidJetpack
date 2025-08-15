package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.generated.destinations.IncomingCallScreenDestination
import com.ramcosta.composedestinations.generated.destinations.SmsMessageScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AppNotificationScreenDestination
import com.ramcosta.composedestinations.generated.destinations.AdvancedSettingsScreenDestination
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun FlashAlertScreen(
    modifier: Modifier = Modifier,
    navigator: DestinationsNavigator
) {
    Box(
        modifier = modifier
            .fillMaxSize()
    ) {
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
                .padding(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.flashlight),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )

                // Settings icon
                Image(
                    painter = painterResource(id = R.drawable.ic_setting),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Notification Settings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2F3C55))
            ) {
                Column {
                    // Incoming call row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                navigator.navigate(
                                    IncomingCallScreenDestination
                                )
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_incoming_call),
                            contentDescription = "Incoming call",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.incoming_call),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                text = stringResource(R.string.tap_to_change_flash_speed),
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = InterFontFamily
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_expand),
                            contentDescription = "Arrow",
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B465D))
                    )

                    // SMS Message row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                navigator.navigate(
                                    SmsMessageScreenDestination
                                )
                            }
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.ic_sms_message),
                                contentDescription = "SMS Message",
                                modifier = Modifier.size(40.dp)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = stringResource(R.string.sms_message),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                fontFamily = InterFontFamily
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_expand),
                            contentDescription = "Arrow",
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(1.dp)
                            .background(Color(0xFF3B465D))
                    )

                    // Notifications for Apps row
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { 
                                navigator.navigate(
                                    AppNotificationScreenDestination
                                )
                            }
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_notification_for_app),
                            contentDescription = "Notifications for Apps",
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.notifications_for_apps),
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.W600,
                                fontFamily = InterFontFamily
                            )
                            Text(
                                text = stringResource(R.string.tap_to_change_apps),
                                color = Color.White.copy(alpha = 0.6f),
                                fontSize = 12.sp,
                                fontWeight = FontWeight.W400,
                                fontFamily = InterFontFamily
                            )
                        }
                        Image(
                            painter = painterResource(id = R.drawable.ic_expand),
                            contentDescription = "Arrow",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Advanced Settings Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF2F3C55))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { 
                            navigator.navigate(AdvancedSettingsScreenDestination)
                        }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_advance_setting),
                        contentDescription = "Advanced settings",
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = stringResource(R.string.advanced_settings),
                        color = Color.White,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.W600,
                        modifier = Modifier.weight(1f),
                        fontFamily = InterFontFamily
                    )
                    Image(
                        painter = painterResource(id = R.drawable.ic_expand),
                        contentDescription = "Arrow",
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
} 