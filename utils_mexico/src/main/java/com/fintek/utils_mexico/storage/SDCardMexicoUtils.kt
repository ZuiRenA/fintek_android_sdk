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

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @JvmStatic
    fun getSDCardFreeSize(): Long = SDCardUtils.getAvailableSize() - SDCardUtils.getUsedSize()
}