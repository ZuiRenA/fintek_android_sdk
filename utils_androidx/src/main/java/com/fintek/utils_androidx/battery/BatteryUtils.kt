package com.fintek.utils_androidx.battery

import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.BatteryManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2020/11/18
 */
object BatteryUtils {
    private val batteryManager: BatteryManager?
        get() = FintekUtils.requiredContext.getSystemService(Context.BATTERY_SERVICE) as? BatteryManager


    /**
     * Return battery percent
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    fun getPercent(): Int {
        return batteryManager?.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY) ?: -1
    }

    /**
     * Return battery is charging
     *
     * **if api < 23 will always false**
     */
    @JvmStatic
    fun isCharging(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            batteryManager?.isCharging ?: false
        else
            false
    }

    /**
     * Return battery is usb charging
     */
    @JvmStatic
    fun isUsbCharging(): Boolean {
        val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            FintekUtils.requiredContext.registerReceiver(null, it)
        }

        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == BatteryManager.BATTERY_PLUGGED_USB
    }

    /**
     * Return batter is ac charging
     */
    @JvmStatic
    fun isAcCharging(): Boolean {
        val batteryStatus = IntentFilter(Intent.ACTION_BATTERY_CHANGED).let {
            FintekUtils.requiredContext.registerReceiver(null, it)
        }

        return batteryStatus?.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1) == BatteryManager.BATTERY_PLUGGED_AC
    }
}