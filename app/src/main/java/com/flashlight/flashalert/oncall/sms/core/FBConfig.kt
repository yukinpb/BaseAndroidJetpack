package com.flashlight.flashalert.oncall.sms.core

import android.util.Log
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import java.util.concurrent.atomic.AtomicBoolean

object FBConfig {
    private const val KEY_FORCE_UPDATE = "is_force_update"
    val isForceUpdate = AtomicBoolean(false)

    fun fetchRMCF() {
        val remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 5
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.fetchAndActivate()
           .addOnCompleteListener { task ->
               if (task.isSuccessful) {
                   val forceUpdate = remoteConfig.getBoolean(KEY_FORCE_UPDATE)
                   isForceUpdate.getAndSet(forceUpdate)
               }
           }
           .addOnFailureListener { e ->
               Log.w("FBConfig", "Fetch and activate failed", e)
           }
    }

}

