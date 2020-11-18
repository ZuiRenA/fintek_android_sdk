package com.fintek.utils_androidx.hardware

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Point
import android.os.Build
import android.view.WindowManager
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2020/11/18
 */
object HardwareUtils {


    /**
     * Return the model of device.
     *
     * e.g. OC105
     * @return the model of device
     */
    @JvmStatic
    fun getModel(): String = Build.MODEL.trim { it <= ' ' }.replace("\\s*".toRegex(), "")


    /**
     * Return the brand of device.
     *
     * e.g. SMARTISAN
     * @return the brand of device
     */
    @JvmStatic
    fun getBrand(): String = if (Build.BRAND != Build.UNKNOWN) Build.BRAND else ""


    /**
     * Return the name of the industrial design.
     *
     * e.g. oscar
     * @return the name of the industrial design.
     */
    @JvmStatic
    fun getDevice(): String = if (Build.DEVICE != Build.UNKNOWN) Build.DEVICE else ""

    /**
     * Return the name of the overall product, sometimes same of [getDevice]
     *
     * e.g. oscar
     * @return the name of the overall product
     */
    @JvmStatic
    fun getProduct(): String = if (Build.PRODUCT != Build.UNKNOWN) Build.PRODUCT else ""

    /**
     * The user-visible version string.  E.g., "1.0" or "3.4b5" or "bananas".
     *
     * This field is an opaque string. Do not assume that its value
     * has any particular structure or that values of RELEASE from
     * different releases can be somehow ordered.
     *
     * @return
     */
    @JvmStatic
    fun getSystemVersion(): String = if (Build.VERSION.RELEASE != Build.UNKNOWN) Build.VERSION.RELEASE else ""

    /**
     * This is same [getSystemVersion]
     */
    @JvmStatic
    fun getRelease(): String = getSystemVersion()

    /**
     * Return version code of device's system.
     *
     * @return version code of device's system
     */
    @JvmStatic
    fun getSDKVersion(): Int = Build.VERSION.SDK_INT

    /**
     * Return the application's width*height of screen, in pixel.
     *
     * e.g. 1080*2160
     *
     * @return the application's width*height of screen, in pixel
     */
    @JvmStatic
    fun getPhysicalSize(): String = "${getPhysicalWidth()}*${getPhysicalHeight()}"


    /**
     * Return the application's width of screen, in pixel.
     *
     * @return the application's width of screen, in pixel
     */
    @JvmStatic
    fun getPhysicalWidth(): Int {
        val point = getPhysicalPoint() ?: return -1
        return point.x
    }

    /**
     * Return the application's height of screen, in pixel.
     *
     * @return the application's height of screen, in pixel
     */
    @JvmStatic
    fun getPhysicalHeight(): Int {
        val point = getPhysicalPoint() ?: return -1
        return point.y
    }

    /**
     * - if SDK_INT < 26 it use [Build.SERIAL]
     * - if SDK_INT >= 26 it use [Build.getSerial]
     *
     * Required permissions:
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     * - Manifest.permission.READ_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("MissingPermission")
    fun getSerialNumber(): String = try {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) Build.getSerial() else Build.SERIAL
    } catch (e: Exception) {
        Build.SERIAL
    }

    private fun getPhysicalPoint(): Point? {
        val wm = FintekUtils.requiredContext
            .getSystemService(Context.WINDOW_SERVICE) as? WindowManager ?: return null

        val point = Point()
        wm.defaultDisplay.getSize(point)
        return point
    }
}