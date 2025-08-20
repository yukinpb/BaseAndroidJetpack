package com.flashlight.flashalert.oncall.sms

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import com.flashlight.flashalert.oncall.sms.features.main.presentation.MainScreen
import com.flashlight.flashalert.oncall.sms.ui.theme.BaseProjectTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import com.nlbn.ads.util.AppOpenManager
import dagger.hilt.android.AndroidEntryPoint

sealed class AppScreen {
    companion object {
        const val FROM_UNINSTALL = "uninstall"
    }
}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val shortcutAction = intent.getStringExtra("shortcut_action")

        AppOpenManager.getInstance().disableAppResume()

        // Cấu hình status bar
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            BaseProjectTheme {
                val systemUiController = rememberSystemUiController()
                val darkIcons = false // false = light icons (white text)

                DisposableEffect(systemUiController, darkIcons) {
                    systemUiController.setSystemBarsColor(
                        color = Color.Transparent,
                        darkIcons = darkIcons
                    )
                    onDispose {}
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.Transparent
                ) {
                    MainScreen(shortcutAction)
                }
            }
        }
    }
}

