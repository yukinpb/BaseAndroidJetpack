package com.flashlight.flashalert.oncall.sms

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import java.lang.Runnable
import android.view.WindowInsets
import android.view.WindowInsetsController
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
        
        // Cấu hình edge-to-edge trước khi set content
        WindowCompat.setDecorFitsSystemWindows(window, false)
        
        enableEdgeToEdge()

        val shortcutAction = intent.getStringExtra("shortcut_action")

        AppOpenManager.getInstance().disableAppResume()

        hideNavigationBar(this)

        setContent {
            BaseProjectTheme {
                val systemUiController = rememberSystemUiController()
                val darkIcons = false // false = light icons (white text)

                DisposableEffect(systemUiController, darkIcons) {
                    // Cấu hình system bars cho Android 15+
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = darkIcons
                        )
                        systemUiController.setNavigationBarColor(
                            color = Color.Transparent,
                            darkIcons = darkIcons
                        )
                    } else {
                        systemUiController.setSystemBarsColor(
                            color = Color.Transparent,
                            darkIcons = darkIcons
                        )
                    }
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

fun hideNavigationBar(activity: Activity) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        activity.window.setDecorFitsSystemWindows(false)
        activity.window.insetsController?.let { controller ->
            controller.hide(WindowInsets.Type.navigationBars())
            controller.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            
            val handler = Handler(Looper.getMainLooper())
            var hideRunnable: Runnable? = null
            
            activity.window.decorView.setOnApplyWindowInsetsListener { _, insets ->
                if (insets.isVisible(WindowInsets.Type.navigationBars())) {
                    hideRunnable?.let { handler.removeCallbacks(it) }
                    
                    hideRunnable = Runnable {
                        controller.hide(WindowInsets.Type.navigationBars())
                    }
                    handler.postDelayed(hideRunnable, 3000)
                }
                insets
            }
        }
    } else {
        @Suppress("DEPRECATION")
        activity.window.decorView.systemUiVisibility =
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                    View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
    }
}

