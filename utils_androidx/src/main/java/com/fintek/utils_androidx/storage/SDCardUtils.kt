package com.fintek.utils_androidx.storage

import android.os.Environment

/**
 * Created by ChaoShen on 2020/11/10
 */
object SDCardUtils {
    /**
     * Return whether sdcard is enabled by environment.
     *
     * @return `true`: enabled  `false`: disabled
     */
    @JvmStatic
    fun isSDCardEnableByEnvironment(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }
}