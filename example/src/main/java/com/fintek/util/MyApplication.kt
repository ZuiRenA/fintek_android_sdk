@file:JvmName("MyApplication")
package com.fintek.util

import android.app.Application
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2020/11/4
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        FintekUtils.init(this)
    }

    companion object {
        const val DEBUG_TAG = "FintekUtils"
    }
}
