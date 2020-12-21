package com.fintek.util

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.fintek.util_example.R
import com.fintek.utils_androidx.encrypt.RSA
import com.fintek.utils_androidx.encrypt.RSAUtil
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.upload.UploadUtils

class MainActivity : AppCompatActivity()  {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.button).setOnClickListener {
          UploadUtils.upload()
        }
    }
}
