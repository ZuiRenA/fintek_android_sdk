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
    fun getAvailableMemory(): String {
        activityManager.getMemoryInfo(mi)
        return Formatter.formatFileSize(FintekUtils.requiredContext, mi.availMem)
    }

    /***
     * Return total runtime memory
     */
    @JvmStatic
    fun getTotalMemory(): String {
        activityManager.getMemoryInfo(mi)
        return Formatter.formatFileSize(FintekUtils.requiredContext, mi.totalMem)
    }

    /***
     * Return available runtime memory percent
     */
    @JvmStatic
    fun getAvailableMemoryPercent(): Double {
        activityManager.getMemoryInfo(mi)
        return mi.availMem.toDouble() / mi.totalMem.toDouble()
    }
}