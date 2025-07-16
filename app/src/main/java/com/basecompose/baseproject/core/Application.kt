package com.basecompose.baseproject.core

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any app-level configurations here
    }
} 