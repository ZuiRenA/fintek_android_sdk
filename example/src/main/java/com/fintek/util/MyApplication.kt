@file:JvmName("MyApplication")
package com.fintek.util

import android.app.Application
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.network.CoronetRequest

/**
 * Created by ChaoShen on 2020/11/4
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FintekUtils.init(this)
            .setIdentify(object : FintekUtils.AbstractIdentify<String>() {
                override fun invoke(): String = "1000"
            })
    }

    companion object {
        const val DEBUG_TAG = "FintekUtils"
    }
}
