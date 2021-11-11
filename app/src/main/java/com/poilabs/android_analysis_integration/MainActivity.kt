package com.poilabs.android_analysis_integration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.app.ActivityCompat
import getpoi.com.poibeaconsdk.PoiAnalysis
import getpoi.com.poibeaconsdk.models.PoiResponseCallback
import java.lang.Exception

class MainActivity : AppCompatActivity(), PoiResponseCallback {
    companion object {
        private const val REQUEST_FOREGROUND_LOCATION_REQUEST_CODE = 56
        private const val REQUEST_BACKGROUND_LOCATION_REQUEST_CODE = 57
        private const val REQUEST_COARSE_LOCATION = 58
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        askRuntimePermissionsIfNeeded()
        findViewById<AppCompatButton>(R.id.update).setOnClickListener {
            val et = findViewById<AppCompatEditText>(R.id.uniqueId)
            PoiAnalysis.getInstance().updateUniqueId(et.text.toString())
        }
    }

    private fun askRuntimePermissionsIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val hasFineLocation: Int =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            val hasBackgroundLocation: Int = ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_BACKGROUND_LOCATION
            )
            if (hasFineLocation != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_FOREGROUND_LOCATION_REQUEST_CODE
                )
            }
            if (hasBackgroundLocation != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUND_LOCATION_REQUEST_CODE
                )
            }
        } else {
            val hasLocalPermission: Int =
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
            if (hasLocalPermission != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                    REQUEST_COARSE_LOCATION
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty()) {
            return
        }
      if (requestCode == REQUEST_COARSE_LOCATION) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) { // Permission Granted
             startPoiSdk()
            }
        } else if (requestCode == REQUEST_BACKGROUND_LOCATION_REQUEST_CODE) {
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) { // Permission Granted
                startPoiSdk()
            }
        } else if( requestCode == REQUEST_FOREGROUND_LOCATION_REQUEST_CODE) {
            askRuntimePermissionsIfNeeded()
      }
    }


    private fun startPoiSdk() {
        PoiAnalysis.getInstance().enable()
        PoiAnalysis.getInstance().startScan(applicationContext)
        PoiAnalysis.getInstance().setPoiResponseListener(this)
    }

    override fun onResponse(p0: String?) {
        Log.i(TAG, "onResponse: $p0")
    }

    override fun onFail(p0: Exception?) {
        Log.e(TAG, "onFail: ${p0?.localizedMessage}")
        p0?.printStackTrace()
    }
}

