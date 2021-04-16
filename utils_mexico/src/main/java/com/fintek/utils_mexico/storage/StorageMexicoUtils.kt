package com.fintek.utils_mexico.storage

import android.os.Build
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.storage.StorageUtils
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
}