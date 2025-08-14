package com.flashlight.flashalert.oncall.sms.features.flashalert.presentation

import android.graphics.drawable.Drawable
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.core.utils.clickableWithoutIndication
import com.flashlight.flashalert.oncall.sms.features.flashalert.presentation.components.CustomToggle
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.AppIconCache
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.AppInfo
import com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel.AppSelectionViewModel
import com.flashlight.flashalert.oncall.sms.ui.theme.InterFontFamily
import com.flashlight.flashalert.oncall.sms.utils.rememberDrawablePainter
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator

@Composable
@Destination<RootGraph>()
fun AppSelectionScreen(
    modifier: Modifier = Modifier,
    viewModel: AppSelectionViewModel = viewModel(),
    navigator: DestinationsNavigator
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }

    Box(
        modifier = modifier.fillMaxSize()
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
            // Header with back arrow and title
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(24.dp)
                        .clickableWithoutIndication { navigator.navigateUp() }
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = stringResource(R.string.select_applications),
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.W600,
                    fontFamily = InterFontFamily
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (state.isLoading) {
                // Loading indicator
                Box(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = stringResource(R.string.loading_applications),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W500,
                            fontFamily = InterFontFamily
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    // Suggestions Section
                    if (state.suggestedApps.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.suggestions),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2F3C55))
                        ) {
                            state.suggestedApps.forEachIndexed { index, app ->
                                AppItem(
                                    app = app,
                                    isSelected = state.selectedPackages.contains(app.packageName),
                                    onToggle = { selected ->
                                        viewModel.toggleAppSelection(app.packageName, selected)
                                    }
                                )
                                if (index < state.suggestedApps.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color(0xFF3B465D))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    if (state.otherApps.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.other_applications),
                            color = Color.White,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.W600,
                            fontFamily = InterFontFamily,
                            modifier = Modifier.padding(bottom = 16.dp)
                        )

                        Column(
                            modifier = modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF2F3C55))
                        ) {
                            state.otherApps.forEachIndexed { index, app ->
                                AppItem(
                                    app = app,
                                    isSelected = state.selectedPackages.contains(app.packageName),
                                    onToggle = { selected ->
                                        viewModel.toggleAppSelection(app.packageName, selected)
                                    }
                                )
                                if (index < state.otherApps.lastIndex) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(1.dp)
                                            .background(Color(0xFF3B465D))
                                    )
                                } else {
                                    Spacer(modifier = Modifier.height(8.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
private fun AppItem(
    app: AppInfo,
    isSelected: Boolean,
    onToggle: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var icon by remember { mutableStateOf<Drawable?>(null) }
    val context = LocalContext.current

    LaunchedEffect(app.packageName) {
        icon = AppIconCache.getOrLoad(context.packageManager, app.packageName)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            if (icon != null) {
                val painter = rememberDrawablePainter(icon)
                if (painter != null) {
                    Image(
                        painter = painter,
                        contentDescription = "App Icon",
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(8.dp))
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Gray),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = app.name.firstOrNull()?.uppercase() ?: "?",
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // App Name
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = app.name,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.W400,
                    fontFamily = InterFontFamily
                )
            }

            // Toggle
            CustomToggle(
                checked = isSelected,
                onCheckedChange = onToggle
            )
        }
    }
} 