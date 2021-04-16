package com.fintek.utils_androidx.storage

import android.app.ActivityManager
import android.content.Context
import android.text.format.Formatter
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2021/4/15
 */
object RuntimeMemoryUtils {

    private val activityManager by lazy {
        FintekUtils.requiredContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    }

    private val mi by lazy { ActivityManager.MemoryInfo() }

    /***
     * Return available runtime memory
     */
    @JvmStatic
    fun getAvailableMemory(): Long {
        activityManager.getMemoryInfo(mi)
        return mi.availMem
    }

    /***
     * Return total runtime memory
     */
    @JvmStatic
    fun getTotalMemory(): Long {
        activityManager.getMemoryInfo(mi)
        return mi.totalMem
    }

    /***
     * Return available runtime memory percent
     */
    @JvmStatic
    fun getAvailableMemoryPercent(): Double {
        activityManager.getMemoryInfo(mi)
        return mi.availMem.toDouble() / mi.totalMem.toDouble()
    }

    @JvmStatic
    fun getAppMaxMemory(): Long {
        return Runtime.getRuntime().maxMemory()
    }

    @JvmStatic
    fun getAppFreeMemory(): Long {
        return Runtime.getRuntime().freeMemory()
    }

    @JvmStatic
    fun getAppTotalMemory(): Long {
        return Runtime.getRuntime().totalMemory()
    }
}