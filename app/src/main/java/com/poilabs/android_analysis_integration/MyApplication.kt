package com.poilabs.android_analysis_integration

import android.app.Application
import android.content.Intent
import getpoi.com.poibeaconsdk.PoiAnalysis
import getpoi.com.poibeaconsdk.models.PoiAnalysisConfig

class MyApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val config = PoiAnalysisConfig(BuildConfig.APPSECRET, "this is a test unique id", BuildConfig.APPID)
        config.setOpenSystemBluetooth(true)
        config.setForegroundServiceIntent(Intent(this, MainActivity::class.java))
        config.enableForegroundService()
        config.setServiceNotificationTitle("Searching for campaigns...")
        config.setForegroundServiceNotificationChannelProperties(
            "My Notification Name",
            "My Notification Channel Description"
        )
        PoiAnalysis.getInstance(this, config)

        PoiAnalysis.getInstance().enable()

        PoiAnalysis.getInstance().startScan(this)
    }

}