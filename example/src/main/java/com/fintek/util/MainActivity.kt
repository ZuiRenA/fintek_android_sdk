package com.fintek.util

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fintek.util_example.R
import com.fintek.utils_androidx.location.LocationUtils
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.thread.SimpleTask
import com.fintek.utils_androidx.thread.ThreadUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.FintekMexicoUtils.getExtension
import com.fintek.utils_mexico.albs.AlbsUtils
import com.fintek.utils_mexico.model.ExtensionModel
import com.fintek.utils_mexico.model.ExtensionModelJsonAdapter
import com.squareup.moshi.Moshi
import com.stu.lon.lib.DeviceInfoHandler

class MainActivity : AppCompatActivity() {

    private val etUserId: EditText by lazy { findViewById(R.id.etInputUserId) }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            ActivityCompat.requestPermissions(
                this, arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                    Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
                    Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.INTERNET, Manifest.permission.READ_CALENDAR,
                ), 1000
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CALL_LOG
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_PHONE_STATE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "No permissions", Toast.LENGTH_SHORT).show()
            return
        }



        ThreadUtils.executeByCpu(object : SimpleTask<String>() {
            @SuppressLint("MissingPermission")
            override fun doInBackground(): String {
                val deviceMexico = getExtension()
                val adapter = ExtensionModelJsonAdapter(Moshi.Builder().build())
                return adapter.toJson(deviceMexico)
            }

            override fun onSuccess(result: String) {
                TimberUtil.i(result)
            }
        })
    }
}
