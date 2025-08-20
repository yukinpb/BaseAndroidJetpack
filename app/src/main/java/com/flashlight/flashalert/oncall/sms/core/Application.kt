package com.flashlight.flashalert.oncall.sms.core

import androidx.appcompat.app.AppCompatDelegate
import com.flashlight.flashalert.oncall.sms.BuildConfig
import com.flashlight.flashalert.oncall.sms.R
import com.flashlight.flashalert.oncall.sms.ads.AdManager
import com.flashlight.flashalert.oncall.sms.core.utils.SharedPrefs
import com.google.firebase.FirebaseApp
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.nlbn.ads.util.AdsApplication
import com.nlbn.ads.util.AppFlyer
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class MainApplication : AdsApplication() {

    override fun onCreate() {
        super.onCreate()

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initFirebase()
        SharedPrefs.init(applicationContext)
        initAppFlyer()
    }

    private fun initFirebase() {
        FirebaseApp.initializeApp(applicationContext)
        FBConfig.fetchRMCF()
        Firebase.crashlytics.isCrashlyticsCollectionEnabled = !BuildConfig.DEBUG
    }

    private fun initAppFlyer() {
        AppFlyer.getInstance().initAppFlyer(this, getString(R.string.app_flyer), true, false, true)
    }

    override fun enableAdsResume(): Boolean {
        return true
    }

    override fun getKeyRemoteIntervalShowInterstitial(): String = ""
    override fun getListTestDeviceId(): MutableList<String> =
        mutableListOf("61AFC09657EC976743A413E0ECC4CD30")

    override fun getResumeAdId(): String = getString(R.string.open_all)
    override fun buildDebug(): Boolean {
        return BuildConfig.DEBUG
    }

    override fun isForceShowFullAdsTest(): Boolean {
        return true
    }

    override fun isPurchased(): Boolean {
        return false
    }

    override fun onTerminate() {
        super.onTerminate()
        AdManager.clearAllAds()
    }
}