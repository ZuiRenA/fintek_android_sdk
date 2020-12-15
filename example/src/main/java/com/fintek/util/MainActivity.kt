package com.fintek.util

import android.content.Intent
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fintek.util_example.R
import com.fintek.utils_androidx.location.LocationUtils
import com.fintek.utils_androidx.log.Timber
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.thread.SimpleTask
import com.fintek.utils_androidx.thread.Task
import com.fintek.utils_androidx.thread.ThreadUtils
import com.fintek.utils_androidx.upload.UploadUtils
import com.fintek.utils_androidx.upload.internal.StringElement
import com.google.gson.*
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonToken
import com.google.gson.stream.JsonWriter

class MainActivity : AppCompatActivity()  {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
            startActivity(Intent(this, MainJavaActivity::class.java))
        }

        val task = object : SimpleTask<String>() {
            private val KEY = "ABCDEFG"

            override fun doInBackground(): String {
                val sb = StringBuilder()
                for (i in 0..1000000) {
                    sb.append(KEY)
                }
                return sb.toString()
            }

            override fun onSuccess(result: String) {
                TimberUtil.e("onSuccess")
                val element = StringElement(result)
                TimberUtil.e("on")
            }
        }

        ThreadUtils.executeByCpu(task)
    }
}
