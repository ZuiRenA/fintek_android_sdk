package com.fintek.utils_androidx.phone

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import java.util.*

/**
 * Created by ChaoShen on 2020/11/18
 */
object PhoneUtils {

    private val telephonyManager: TelephonyManager?
        get() = FintekUtils.requiredContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager


    /**
     * @return the current phone type
     *
     * - [TelephonyManager.PHONE_TYPE_NONE]
     * - [TelephonyManager.PHONE_TYPE_GSM]
     * - [TelephonyManager.PHONE_TYPE_CDMA]
     * - [TelephonyManager.PHONE_TYPE_SIP]
     */
    @JvmStatic
    fun getPhoneType(): Int {
        return telephonyManager?.phoneType ?: TelephonyManager.PHONE_TYPE_NONE
    }

    /**
     * Return user phone number
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "InlinedApi")
    @RequiresPermission(
        anyOf = [
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.READ_SMS,
            Manifest.permission.READ_PHONE_NUMBERS
        ]
    )
    fun getPhoneNumber(): String = try {
        if (isHasSimCard()) telephonyManager?.line1Number ?: "" else ""
    } catch (e: Exception) {
        ""
    }

    /**
     * Required permission:
     * - Manifest.permission.READ_PHONE_STATE
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getImsi(): String = try {
        telephonyManager?.subscriberId ?: ""
    } catch (e: Exception) {
        ""
    }

    /**
     * Required permission:
     * - Manifest.permission.READ_PHONE_STATE
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getMCC(): String = try {
        var mcc = ""

        if (isHasSimCard()) {
            val imsi = telephonyManager?.subscriberId
            if (!imsi.isNullOrEmpty()) {
                mcc = imsi.take(3)
            }
        }

        mcc
    } catch (e: Exception) {
        ""
    }

    /**
     * Required permission:
     * - Manifest.permission.READ_PHONE_STATE
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getMNC(): String = try {
        var mnc = ""

        if (isHasSimCard()) {
            val imsi = telephonyManager?.subscriberId
            if (!imsi.isNullOrEmpty()) {
                mnc = imsi.substring(2, 5)
            }
        }

        mnc
    } catch (e: Exception) {
        ""
    }

    /**
     * Return is has sim card
     */
    @JvmStatic
    fun isHasSimCard(): Boolean = try {
        !telephonyManager?.simOperator.isNullOrEmpty()
    } catch (e: Exception) {
        false
    }

    /**
     * Return timezone id
     */
    @JvmStatic
    fun getTimeZoneId(): String = try {
        TimeZone.getDefault().id
    } catch (e: Exception) {
        ""
    }

    /**
     * Return cid
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getCID(): String = try {
        val location = telephonyManager?.cellLocation as? GsmCellLocation
        var cid = ""
        location?.let {
            cid = location.cid.toString()
        }
        cid
    } catch (e: Exception) {
        ""
    }
}