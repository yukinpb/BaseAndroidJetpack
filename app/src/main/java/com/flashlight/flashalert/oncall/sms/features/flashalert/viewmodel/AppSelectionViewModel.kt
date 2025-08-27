package com.flashlight.flashalert.oncall.sms.features.flashalert.viewmodel

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.os.Build
import android.util.LruCache
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext

data class AppInfo(
    val packageName: String,
    val name: String
)

data class AppSelectionState(
    val suggestedApps: List<AppInfo> = emptyList(),
    val otherApps: List<AppInfo> = emptyList(),
    val selectedPackages: Set<String> = emptySet(),
    val isLoading: Boolean = false
)

class AppSelectionViewModel : ViewModel() {
    private val _state = MutableStateFlow(AppSelectionState())
    val state: StateFlow<AppSelectionState> = _state.asStateFlow()

    init {
        loadSelectedPackages()
    }

    private fun loadSelectedPackages() {
        _state.value = _state.value.copy(
            selectedPackages = SharedPrefs.selectedAppPackages
        )
    }

    fun loadInstalledApps(context: Context) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)

            try {
                val installedApps = getLaunchableApps(context)
                val messagingPackages = setOf(
                    "com.zing.zalo",                   // Zalo (Vietnam)
                    "com.facebook.orca",               // Messenger (Global)
                    "com.viber.voip",                  // Viber (Global)
                    "com.whatsapp",                    // WhatsApp (Global)
                    "org.telegram.messenger",          // Telegram (Global)
                    "jp.naver.line.android",           // LINE (Japan, Taiwan, Thailand)
                    "com.kakao.talk",                  // KakaoTalk (Korea)
                    "com.tencent.mm",                  // WeChat (China)
                    "org.thoughtcrime.securesms",      // Signal (Global)
                    "com.bbm",                         // BBM (Indonesia, legacy)
                    "com.skype.raider",                // Skype (Global)
                    "com.snapchat.android",            // Snapchat (Global)
                    "com.truecaller",                  // Truecaller (India, Global)
                    "com.imo.android.imoim",           // imo (Middle East, South Asia)
                    "com.hike.chat.stickers",          // Hike (India, legacy)
                    "com.vkontakte.android",           // VK (Russia)
                    "com.yandex.messenger"             // Yandex Messenger (Russia)
                )

                val suggestedApps = installedApps.filter { it.packageName in messagingPackages }

                val otherApps = installedApps.filter { app ->
                    !suggestedApps.any { it.packageName == app.packageName }
                }

                _state.value = _state.value.copy(
                    suggestedApps = suggestedApps,
                    otherApps = otherApps,
                    isLoading = false
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(isLoading = false)
            }
        }
    }

    suspend fun getLaunchableApps(context: Context): List<AppInfo> = withContext(Dispatchers.IO) {
        val pm = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER)

        val resolveInfos = if (Build.VERSION.SDK_INT >= 33) {
            pm.queryIntentActivities(intent, PackageManager.ResolveInfoFlags.of(0))
        } else {
            @Suppress("DEPRECATION")
            pm.queryIntentActivities(intent, 0)
        }

        resolveInfos.asSequence()
            .filter { it.activityInfo.packageName != context.packageName }
            .distinctBy { it.activityInfo.packageName }
            .map { ri ->
                val name = ri.loadLabel(pm).toString()
                AppInfo(packageName = ri.activityInfo.packageName, name = name)
            }
            .sortedBy { it.name.lowercase() }
            .toList()
    }


    fun toggleAppSelection(packageName: String, selected: Boolean) {
        val currentSelected = _state.value.selectedPackages.toMutableSet()

        if (selected) {
            currentSelected.add(packageName)
        } else {
            currentSelected.remove(packageName)
        }

        _state.value = _state.value.copy(selectedPackages = currentSelected)

        SharedPrefs.selectedAppPackages = currentSelected
    }
}

object AppIconCache {
    private val cache = object : LruCache<String, Drawable>(64) {}
    private val sem = kotlinx.coroutines.sync.Semaphore(permits = 6)

    suspend fun getOrLoad(pm: PackageManager, pkg: String): Drawable? {
        cache.get(pkg)?.let { return it }
        return withContext(Dispatchers.IO) {
            sem.withPermit {
                runCatching { pm.getApplicationIcon(pkg) }.getOrNull()?.also {
                    cache.put(pkg, it)
                }
            }
        }
    }
}