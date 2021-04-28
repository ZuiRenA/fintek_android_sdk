package com.fintek.utils_mexico.device

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_mexico.ext.catchOrBoolean
import com.fintek.utils_mexico.ext.catchOrEmpty
import com.fintek.utils_mexico.ext.catchOrZero

/**
 * Created by ChaoShen on 2021/4/15
 */
object DeviceMexicoUtils {

    @JvmStatic
    fun isEnableAdb(): String = if (catchOrBoolean { DeviceUtils.isEnableAdb() }) "1" else "0"

    @JvmStatic
    fun isRoot(): Int = if (catchOrBoolean { DeviceUtils.isRoot() }) 1 else 0

    @JvmStatic
    fun isSimulator(): Int = if (catchOrBoolean { DeviceUtils.isProbablyRunningOnEmulator() }) 1 else 0

    @JvmStatic
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getImsi(): String {
        var imsi = DeviceUtils.getImsi()
        if (imsi == null || imsi.isEmpty()) {
            imsi = "-1"
        }

        return imsi
    }

    @JvmStatic
    fun getAndroidId(): String = catchOrEmpty { DeviceUtils.getAndroidId() }

    @JvmStatic
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDeviceIdentify(): String = catchOrEmpty { DeviceUtils.getDeviceIdentify(true).orEmpty() }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @JvmStatic
    fun getMobileDbm(): Int = catchOrZero { DeviceUtils.getMobileDbm() }

    @JvmStatic
    fun getCurrentKeyboardType(): Int = catchOrZero { DeviceUtils.getCurrentKeyboardType() }
}