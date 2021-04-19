package com.fintek.utils_mexico.device

import android.Manifest
import androidx.annotation.RequiresPermission
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

    @JvmStatic
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getImsi(): String {
        var imsi = DeviceUtils.getImsi()
        if (imsi == null || imsi.isEmpty()) {
            imsi = "-1"
        }

        return imsi
    }
}