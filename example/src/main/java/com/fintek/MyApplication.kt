@file:JvmName("MyApplication")
package com.fintek

import android.app.Application
import android.util.Log
import com.fintek.ntl_utils.NtlUtils
import com.fintek.throwable.rxjava.RxjavaComponentBinder
import com.fintek.throwable.base.BaseGlobalComponentBinder
import com.fintek.throwable.base.DefaultExceptionHandler
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.ThrowableUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.stu.lon.lib.DeviceInfoHandler

/**
 * Created by ChaoShen on 2020/11/4
 */
class MyApplication : Application(), DefaultExceptionHandler {

    override fun onCreate() {
        super.onCreate()

        DeviceInfoHandler.init(this)
        FintekMexicoUtils.init(this).isDebugEnable(true)
        BaseGlobalComponentBinder.bindComponent()
        BaseGlobalComponentBinder.setDefaultExceptionHandler(this)
        RxjavaComponentBinder.setErrorHandler()

        NtlUtils.init(this)
            .setIdentify(object : NtlUtils.AbstractIdentify<String>() {
                override fun invoke(): String = "1006"
            })
            .setBaseUrl("url here")
            .setUploadApiPath("path here")
    }

    override fun exceptionHandler(thread: Thread?, throwable: Throwable?): Boolean {
        Log.e("Exception", ThrowableUtils.getFullStackTrace(throwable))
        return true
    }
}
