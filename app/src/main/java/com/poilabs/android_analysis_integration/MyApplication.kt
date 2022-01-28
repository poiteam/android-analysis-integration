package com.poilabs.android_analysis_integration

import android.app.Application
import android.content.Intent
import getpoi.com.poibeaconsdk.PoiAnalysis
import getpoi.com.poibeaconsdk.models.PoiAnalysisConfig
import getpoi.com.poibeaconsdk.models.PoiResponseCallback
import java.lang.Exception

class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        val config = PoiAnalysisConfig(
            appId = BuildConfig.APPID,
            secret = BuildConfig.APPSECRET,
            uniqueId = "this is a test unique id"
        )
        config.setOpenSystemBluetooth(false)
        config.setForegroundServiceIntent(Intent(this, MainActivity::class.java))
        config.enableForegroundService()
        config.setServiceNotificationTitle("Searching for campaigns...")
        config.setForegroundServiceNotificationIconResourceId(R.drawable.ic_baseline_cruelty_free_24)
        config.setForegroundServiceNotificationChannelProperties(
            "My Notification Name",
            "My Notification Channel Description"
        )
        PoiAnalysis.getInstance(this, config)

        PoiAnalysis.getInstance().enable()

        PoiAnalysis.getInstance().startScan(this)
    }

}