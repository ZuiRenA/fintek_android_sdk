package com.fintek.utils_mexico.storage

import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.catchOrBoolean
import com.fintek.utils_mexico.ext.catchOrEmpty

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
    fun getSDCardTotalSize(): String = catchOrEmpty("-1") {
        val sdCardTotalSize = getTotalSize()
        if (sdCardTotalSize == 0L) {
            return@catchOrEmpty "-1"
        }
        return@catchOrEmpty Formatter.formatFileSize(FintekMexicoUtils.requiredApplication, sdCardTotalSize)
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getSDCardAvailableSize(): String = catchOrEmpty("-1") {
        val sdCardAvailableSize = getAvailableSize()
        if (sdCardAvailableSize == 0L) {
            return@catchOrEmpty "-1"
        }

        return@catchOrEmpty Formatter.formatFileSize(FintekMexicoUtils.requiredApplication, sdCardAvailableSize)
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