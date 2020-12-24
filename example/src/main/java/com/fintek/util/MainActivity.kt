package com.fintek.util

import android.Manifest
import android.content.Intent
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
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.encrypt.RSA
import com.fintek.utils_androidx.encrypt.RSAUtil
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.upload.UploadUtils

class MainActivity : AppCompatActivity()  {

    private val etUserId: EditText by lazy { findViewById(R.id.etInputUserId) }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            ActivityCompat.requestPermissions(this, arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
            ), 1000)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
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
            FintekUtils.setIdentify(object : FintekUtils.AbstractIdentify<String>() {
                override fun invoke(): String {
                    val etText = etUserId.text.toString()
                    if (etText.isBlank() || etText.isEmpty()) {
                        return "1000"
                    }
                    return etText
                }
            })
            UploadUtils.upload()
        }
    }
}
