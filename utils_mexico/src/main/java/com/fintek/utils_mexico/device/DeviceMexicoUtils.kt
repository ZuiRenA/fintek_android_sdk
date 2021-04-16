package com.fintek.utils_mexico.device

import com.fintek.utils_androidx.device.DeviceUtils

/**
 * Created by ChaoShen on 2021/4/15
 */
object DeviceMexicoUtils {

    @JvmStatic
    fun isEnableAdb(): String = if (DeviceUtils.isEnableAdb()) "1" else "0"

    @JvmStatic
    fun isRoot(): Int = if (DeviceUtils.isRoot()) 1 else 0

    @JvmStatic
    fun isSimulator(): Int = if (DeviceUtils.isProbablyRunningOnEmulator()) 1 else 0
}