@file:JvmName("MyApplication")
package com.fintek.util

import android.app.Application
import com.fintek.ntl_utils.NtlUtils
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.network.CoronetRequest
import com.fintek.utils_mexico.FintekMexicoUtils
import com.stu.lon.lib.DeviceInfoHandler

/**
 * Created by ChaoShen on 2020/11/4
 */
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        DeviceInfoHandler.init(this)
        FintekMexicoUtils.init(this)
    }

    companion object {
        const val DEBUG_TAG = "FintekUtils"
    }
}
