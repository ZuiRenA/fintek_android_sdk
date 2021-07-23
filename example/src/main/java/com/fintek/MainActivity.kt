package com.fintek

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fintek.coroutine.catchLaunch
import com.fintek.ocr_camera.camera.IDCardCamera
import com.fintek.rxjava.RxjavaComponentBinder
import com.fintek.util_example.R
import com.fintek.utils_androidx.image.ImageUtils.getSize
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
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Observer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.File
import java.math.BigDecimal

class MainActivity : AppCompatActivity(), CoroutineScope by MainScope() {

    private val etUserId: EditText by lazy { findViewById(R.id.etInputUserId) }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            FintekMexicoUtils.getSms()
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

    }

}