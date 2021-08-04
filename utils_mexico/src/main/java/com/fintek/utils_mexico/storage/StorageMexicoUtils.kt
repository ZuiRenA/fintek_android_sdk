package com.fintek.utils_mexico.storage

import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.storage.StorageUtils
import com.fintek.utils_androidx.throwable.catchOrEmpty
import com.fintek.utils_mexico.FintekMexicoUtils

/**
 * Created by ChaoShen on 2021/4/15
 */
object StorageMexicoUtils {

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getTotalStorageSize(): String {
        val totalSize = StorageUtils.getTotalSizeRepaired()
        return Formatter.formatFileSize(FintekMexicoUtils.requiredApplication, totalSize)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @JvmStatic
    fun getUnuseStorageSize(): String {
        val unuseStorageSize = StorageUtils.getTotalSizeRepaired() - StorageUtils.getUsedSizeRepaired()
        return Formatter.formatFileSize(FintekMexicoUtils.requiredApplication, unuseStorageSize)
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun queryWithStatFs(): String {
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)

        //存储块
        val blockCount = statFs.blockCountLong
        //块大小
        val blockSize = statFs.blockSizeLong
        //可用块数量
        val availableCount = statFs.availableBlocksLong
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        val freeBlocks = statFs.freeBlocksLong

        return (blockSize * blockCount).toString() + + blockSize * availableCount
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getExternalTotalSize(): String = catchOrEmpty("-1") {
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)

        //存储块
        val blockCount = statFs.blockCountLong
        //块大小
        val blockSize = statFs.blockSizeLong
        Formatter.formatFileSize(FintekMexicoUtils.requiredApplication, blockSize * blockCount)
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getExternalUnusedSize(): String = catchOrEmpty("-1") {
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)

        //存储块
        val blockCount = statFs.freeBlocksLong
        //块大小
        val blockSize = statFs.blockSizeLong
        Formatter.formatFileSize(FintekMexicoUtils.requiredApplication, blockSize * blockCount)
    }
}