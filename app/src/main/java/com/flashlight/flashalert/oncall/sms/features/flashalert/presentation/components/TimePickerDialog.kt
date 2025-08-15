package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import java.util.Locale
import kotlin.math.max

@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onTimeSelected: (String) -> Unit,
    currentTime: String = "00:00"
) {
    val currentHour = currentTime.split(":")[0].toIntOrNull() ?: 0
    val currentMinute = currentTime.split(":")[1].toIntOrNull() ?: 0

    var selectedHour by remember { mutableIntStateOf(currentHour) }
    var selectedMinute by remember { mutableIntStateOf(currentMinute) }

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
                .fillMaxWidth()
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
                        text = stringResource(R.string.select_time),
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = InterFontFamily,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Cupertino Time Picker
                    CupertinoTimePicker(
                        initialHour = currentHour,
                        initialMinute = currentMinute,
                        is24Hour = true,
                        onTimeChange = { hour, minute ->
                            selectedHour = hour
                            selectedMinute = minute
                        }
                    )

                    Spacer(modifier = Modifier.height(32.dp))

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
                                .clickableWithoutIndication {
                                    val selectedTime =
                                        String.format(
                                            Locale.US,
                                            "%02d:%02d",
                                            selectedHour,
                                            selectedMinute
                                        )
                                    onTimeSelected(selectedTime)
                                    onDismiss()
                                }
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

@Composable
fun CupertinoTimePicker(
    modifier: Modifier = Modifier,
    initialHour: Int = 6,
    initialMinute: Int = 28,
    is24Hour: Boolean = false,
    onTimeChange: (hour: Int, minute: Int) -> Unit = { _, _ -> }
) {
    val hourItems = if (is24Hour) (0..23).map { it.toString().padStart(2, '0') }
    else (1..12).map { it.toString().padStart(2, '0') }
    val minuteItems = (0..59).map { it.toString().padStart(2, '0') }

    var selHour by remember {
        mutableIntStateOf(
            if (is24Hour) initialHour.coerceIn(
                0,
                23
            ) else (if (initialHour % 12 == 0) 12 else initialHour % 12)
        )
    }
    var selMinute by remember { mutableIntStateOf(initialMinute.coerceIn(0, 59)) }

    LaunchedEffect(selHour, selMinute) {
        val hour24 = if (is24Hour) selHour else ((selHour % 12) + if (selHour < 12) 0 else 12) % 24
        onTimeChange(hour24, selMinute)
    }

    val itemHeight = 44.dp
    val visibleCount = 5

    Box(modifier) {
        Row(
            modifier = Modifier
                .height(itemHeight * visibleCount)
                .clipToBounds(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(Modifier.width(4.dp))

            WheelPicker(
                items = hourItems,
                initialIndex = hourItems.indexOf(selHour.toString().padStart(2, '0'))
                    .coerceAtLeast(0),
                itemHeight = itemHeight,
                visibleCount = visibleCount,
                onSelected = { idx -> selHour = hourItems[idx].toInt() },
                modifier = Modifier.weight(1f)
            )

            Colon()

            WheelPicker(
                items = minuteItems,
                initialIndex = selMinute,
                itemHeight = itemHeight,
                visibleCount = visibleCount,
                onSelected = { idx -> selMinute = idx },
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(4.dp))
        }

        CenterHighlight(height = itemHeight, modifier = Modifier.align(Alignment.Center))

        EdgeFades(height = itemHeight * visibleCount)
    }
}

@Composable
private fun Colon() {
    Text(
        text = " : ",
        fontSize = 22.sp,
        modifier = Modifier.padding(horizontal = 2.dp),
        color = Color(0xFF444444),
        textAlign = TextAlign.Center
    )
}

@Composable
fun WheelPicker(
    items: List<String>,
    initialIndex: Int,
    itemHeight: Dp,
    visibleCount: Int,
    onSelected: (index: Int) -> Unit,
    minWidth: Dp = 0.dp,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState(
        initialFirstVisibleItemIndex = max(initialIndex, 0)
    )
    val fling = rememberSnapFlingBehavior(lazyListState = listState)

    LaunchedEffect(listState.isScrollInProgress) {
        if (!listState.isScrollInProgress) {
            val center = listState.firstVisibleItemIndex
            val idx = center.coerceIn(0, items.lastIndex)
            onSelected(idx)
        }
    }

    Box(
        modifier = modifier
            .widthIn(min = minWidth),
        contentAlignment = Alignment.Center
    ) {
        LazyColumn(
            state = listState,
            flingBehavior = fling,
            contentPadding = PaddingValues(vertical = itemHeight * (visibleCount / 2)),
            modifier = Modifier.fillMaxWidth()
        ) {
            itemsIndexed(items) { index, item ->
                val isSelected by remember {
                    derivedStateOf {
                        val center = listState.firstVisibleItemIndex
                        center == index
                    }
                }
                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = item,
                        fontSize = 20.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) Color.Black else Color(0xFFBEBEBE),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun CenterHighlight(height: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .background(Color.Transparent)
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.TopCenter)
                .background(Color(0x22000000))
        )
        Box(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .align(Alignment.BottomCenter)
                .background(Color(0x22000000))
        )
    }
}

@Composable
private fun EdgeFades(height: Dp) {
    val fade = Brush.verticalGradient(
        0f to Color.White,
        0.15f to Color.White.copy(alpha = 0.6f),
        0.5f to Color.Transparent,
        0.85f to Color.White.copy(alpha = 0.6f),
        1f to Color.White
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(fade)
    )
} 