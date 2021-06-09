package com.fintek.utils_mexico.hardware

import android.content.Context
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import com.fintek.utils_androidx.hardware.HardwareUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import kotlin.math.pow
import kotlin.math.sqrt


/**
 * Created by ChaoShen on 2021/6/9
 */
object HardwareMexicoUtils {

    @JvmStatic
    fun getSerialNumber(): String {
        var serialNumber = HardwareUtils.getSerialNumber()
        if (serialNumber == Build.UNKNOWN) {
            serialNumber = ""
        }
        return serialNumber
    }

    @JvmStatic
    fun getScreenPhysicalInch(): String {
        val display =
            (FintekMexicoUtils.requiredApplication.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        val displayMetrics = DisplayMetrics()
        display.getMetrics(displayMetrics)
        return sqrt(
            (displayMetrics.heightPixels.toFloat() / displayMetrics.ydpi).toDouble().pow(2.0) +
                    (displayMetrics.widthPixels.toFloat() / displayMetrics.xdpi).toDouble().pow(2.0)
        ).toString()
    }

}