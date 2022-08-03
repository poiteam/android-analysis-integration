package com.poilabs.android_analysis_integration

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.app.ActivityCompat
import getpoi.com.poibeaconsdk.PoiAnalysis
import getpoi.com.poibeaconsdk.models.PoiResponseCallback

class MainActivity : AppCompatActivity(), PoiResponseCallback {
    private var nodeId: AppCompatTextView? = null
    private var update: AppCompatButton? = null
    private var uniqueId: AppCompatEditText? = null
    private var isBackgroundPermissionDenied:Boolean = false

    companion object {
        private const val REQUEST_FOREGROUND_LOCATION_REQUEST_CODE = 56
        private const val REQUEST_BACKGROUND_LOCATION_REQUEST_CODE = 57
        private const val REQUEST_COARSE_LOCATION = 58
        private const val REQUEST_BLUETOOTH_PERMISSION = 59
        private const val TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        nodeId = findViewById(R.id.nodeId)
        update = findViewById(R.id.update)
        uniqueId = findViewById(R.id.uniqueId)
        PoiAnalysis.getInstance().setPoiResponseListener(this)
        askRuntimePermissionsIfNeeded()
        update?.setOnClickListener {
            PoiAnalysis.getInstance().updateUniqueId(uniqueId?.text.toString())
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
            if (hasFineLocation != PackageManager.PERMISSION_GRANTED && !shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION) ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ),
                    REQUEST_FOREGROUND_LOCATION_REQUEST_CODE
                )
                return
            }
            if (hasBackgroundLocation != PackageManager.PERMISSION_GRANTED
                && hasFineLocation == PackageManager.PERMISSION_GRANTED
                &&  !isBackgroundPermissionDenied) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(
                        Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ),
                    REQUEST_BACKGROUND_LOCATION_REQUEST_CODE
                )
                return
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                checkBluetoothPermission()
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

    @RequiresApi(Build.VERSION_CODES.S)
    fun checkBluetoothPermission() {
        val hasBluetoothPermission = ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.BLUETOOTH_CONNECT
        ) == PackageManager.PERMISSION_GRANTED
        if (!hasBluetoothPermission && !shouldShowRequestPermissionRationale(Manifest.permission.BLUETOOTH_CONNECT)) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.BLUETOOTH_CONNECT, Manifest.permission.BLUETOOTH_SCAN),
                REQUEST_BLUETOOTH_PERMISSION
            )
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
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                isBackgroundPermissionDenied = true
            }
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
              askRuntimePermissionsIfNeeded()
              return
          }
            if (PackageManager.PERMISSION_GRANTED == grantResults[0]) { // Permission Granted
                startPoiSdk()
            }
        } else if( requestCode == REQUEST_FOREGROUND_LOCATION_REQUEST_CODE) {
            askRuntimePermissionsIfNeeded()
      } else if (requestCode == REQUEST_BLUETOOTH_PERMISSION) {
          if (PackageManager.PERMISSION_GRANTED == grantResults[0]) { // Permission Granted
              startPoiSdk()
          }
      }
    }


    private fun startPoiSdk() {
        PoiAnalysis.getInstance().enable()
        PoiAnalysis.getInstance().startScan(applicationContext)
    }

    override fun onResponse(p0: MutableList<String>?) {
        runOnUiThread {
            nodeId?.text = p0?.joinToString(",")
        }
    }

    override fun onFail(p0: Exception?) {
        Log.e(TAG, "onFail: ${p0?.localizedMessage}")
        Toast.makeText(this, p0?.localizedMessage, Toast.LENGTH_SHORT).show()
        p0?.printStackTrace()
    }
}

