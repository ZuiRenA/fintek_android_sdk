@file:JvmName("MyApplication")
package com.fintek

import android.app.Application
import com.fintek.rxjava.RxjavaComponent
import com.fintek.rxjava.RxjavaComponentBinder
import com.fintek.throwable.base.BaseGlobalComponentBinder
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
        BaseGlobalComponentBinder.bindComponent()
        RxjavaComponentBinder.setErrorHandler()
    }

    companion object {
        const val DEBUG_TAG = "FintekUtils"
    }
}
