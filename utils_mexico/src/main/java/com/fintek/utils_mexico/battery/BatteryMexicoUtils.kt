package com.fintek.utils_mexico.battery

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.battery.BatteryUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.catchOrEmpty
import com.fintek.utils_mexico.ext.catchOrZero
import com.fintek.utils_mexico.ext.catchOrZeroDouble

/**
 * Created by ChaoShen on 2021/4/15
 */
object BatteryMexicoUtils {

    fun getBatteryCapacity() = catchOrEmpty { getBatteryCapacityByHook().toString() }

    fun getBatteryRemainder() = catchOrEmpty {
        val percent = getPercent()
        if (percent == -1) return@catchOrEmpty ""
        (getBatteryCapacityByHook() * percent / 100).toString()
    }

    fun getPercent(): Int = catchOrZero(-1) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            BatteryUtils.getPercent()
        } else {
            -1
        }
    }

    fun isAcCharging(): Int = if (BatteryUtils.isAcCharging()) 1 else 0

    fun isCharging(): Int = if (BatteryUtils.isCharging()) 1 else 0

    fun isUsbCharging(): Int = if (BatteryUtils.isUsbCharging()) 1 else 0

    @SuppressLint("PrivateApi")
    private fun getBatteryCapacityByHook(): Double = catchOrZeroDouble {
        Class.forName("com.android.internal.os.PowerProfile").run {
            val profile = getConstructor(Context::class.java).newInstance(FintekMexicoUtils.requiredApplication)
            getMethod("getBatteryCapacity").invoke(profile) as Double
        }
    }
}