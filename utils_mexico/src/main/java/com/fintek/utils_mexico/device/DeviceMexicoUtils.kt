package com.fintek.utils_mexico.device

import android.Manifest
import android.os.Build
import android.os.Build.VERSION_CODES.*
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.throwable.catchOrBoolean
import com.fintek.utils_androidx.throwable.catchOrEmpty
import com.fintek.utils_androidx.throwable.catchOrZero

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
    fun getDeviceIdentify(): String = catchOrEmpty { DeviceUtils.getDeviceIdentify(false).orEmpty() }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @JvmStatic
    fun getMobileDbm(): Int = catchOrZero { DeviceUtils.getMobileDbm() }

    @JvmStatic
    fun getCurrentKeyboardType(): Int = catchOrZero { DeviceUtils.getCurrentKeyboardType() }

    @JvmStatic
    fun getDeviceOsVersion(): String {
        return when(val sdkInt = Build.VERSION.SDK_INT) {
            BASE -> "1.0"
            BASE_1_1 -> "1.1"
            CUPCAKE -> "1.5"
            DONUT -> "1.6"
            ECLAIR -> "2.0"
            ECLAIR_0_1 -> "2.0.1"
            ECLAIR_MR1 -> "2.1"
            FROYO -> "2.2"
            GINGERBREAD -> "2.3"
            GINGERBREAD_MR1 -> "2.3.3"
            HONEYCOMB -> "3.0"
            HONEYCOMB_MR1 -> "3.1"
            HONEYCOMB_MR2 -> "3.2"
            ICE_CREAM_SANDWICH -> "4.0"
            ICE_CREAM_SANDWICH_MR1 -> "4.0.3"
            JELLY_BEAN -> "4.1"
            JELLY_BEAN_MR1 -> "4.2"
            JELLY_BEAN_MR2 -> "4.3"
            KITKAT -> "4.4"
            KITKAT_WATCH -> "4.4W"
            LOLLIPOP -> "5.0"
            LOLLIPOP_MR1 -> "5.1"
            M -> "6.0"
            N -> "7.0"
            N_MR1 -> "7.1"
            O -> "8.0"
            O_MR1 -> "8.1"
            P -> "9"
            Q -> "10"
            R -> "11"
            else -> sdkInt.toString()
        }
    }
}