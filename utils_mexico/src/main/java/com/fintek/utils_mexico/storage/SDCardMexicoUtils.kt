package com.fintek.utils_mexico.storage

import android.os.Build
import android.os.Environment
import android.os.StatFs
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.catchOrBoolean

/**
 * Created by ChaoShen on 2021/4/16
 */
object SDCardMexicoUtils {

    @JvmStatic
    fun isContainSDCard(): String = if (catchOrBoolean { SDCardUtils.isSDCardEnableByEnvironment() }) "1" else "0"

    @JvmStatic
    fun isExtraSDCard(): String = if (catchOrBoolean { SDCardUtils.isSDCardExtra() }) "1" else "0"

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
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.blockCountLong * stat.blockSizeLong
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getSDCardFreeSize(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.freeBlocksLong * stat.blockSizeLong
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUsedSize(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.blockCountLong * stat.blockSizeLong - stat.availableBlocksLong * stat.blockSizeLong
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getAvailableSize(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBlocksLong * stat.blockSizeLong
    }
}