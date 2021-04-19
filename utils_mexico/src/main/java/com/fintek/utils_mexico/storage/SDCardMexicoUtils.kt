package com.fintek.utils_mexico.storage

import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.storage.SDCardUtils

/**
 * Created by ChaoShen on 2021/4/16
 */
object SDCardMexicoUtils {

    @JvmStatic
    fun isContainSDCard(): String = if (SDCardUtils.isSDCardEnableByEnvironment()) "1" else "0"

    @JvmStatic
    fun isExtraSDCard(): String = if (SDCardUtils.isSDCardExtra()) "1" else "0"

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getSDCardTotalSize(): String {
        var sdCardTotalSizeStr = SDCardUtils.getTotalSizeString()
        if (sdCardTotalSizeStr.isEmpty()) {
            sdCardTotalSizeStr = "-1"
        }
        return sdCardTotalSizeStr
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getSDCardAvailableSize(): String {
        var sdCardAvailableSizeStr = SDCardUtils.getAvailableSizeString()
        if (sdCardAvailableSizeStr.isEmpty()) {
            sdCardAvailableSizeStr = "-1"
        }
        return sdCardAvailableSizeStr
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getTotalSize(): Long {
        var totalSize = SDCardUtils.getTotalSize()
        if (totalSize == 0L) {
            totalSize = -1
        }
        return totalSize
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getSDCardFreeSize(): Long {
        var freeSize = SDCardUtils.getAvailableSize() - SDCardUtils.getUsedSize()
        if (freeSize == 0L) {
            freeSize = -1
        }
        return freeSize
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUsedSize(): Long {
        var usedSize = SDCardUtils.getUsedSize()
        if (usedSize == 0L) {
            usedSize = -1
        }
        return usedSize
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getAvailableSize(): Long {
        var availableSize = SDCardUtils.getAvailableSize()
        if (availableSize == 0L) {
            availableSize = -1
        }
        return availableSize
    }
}