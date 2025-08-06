package com.flashlight.flashalert.oncall.sms.core

import android.app.Application
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-level configurations here

        SharedPrefs.init(this)
    }
} 