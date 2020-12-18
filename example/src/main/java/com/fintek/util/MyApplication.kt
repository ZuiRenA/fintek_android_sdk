@file:JvmName("MyApplication")
package com.fintek.util

import android.app.Application
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.log.TimberUtil

/**
 * Created by ChaoShen on 2020/11/4
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FintekUtils.init(this)
            .setIdentifyAsync(object : FintekUtils.AbstractIdentify<String>() {
                override fun invoke(): String {
                    // Time-consuming operation
                    return "672"
                }
            })
    }

    companion object {
        const val DEBUG_TAG = "FintekUtils"
    }
}
