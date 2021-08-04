package com.fintek.utils_androidx.phone

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.telephony.TelephonyManager
import android.telephony.gsm.GsmCellLocation
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.catchOrBoolean
import com.fintek.utils_androidx.throwable.catchOrEmpty
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
    fun getPhoneNumber(): String = catchOrEmpty {
        if (isHasSimCard()) telephonyManager?.line1Number ?: "" else ""
    }

    /**
     * Required permission:
     * - Manifest.permission.READ_PHONE_STATE
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getImsi(): String = catchOrEmpty {
        telephonyManager?.subscriberId ?: ""
    }

    /**
     * Required permission:
     * - Manifest.permission.READ_PHONE_STATE
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getMCC(): String = catchOrEmpty {
        var mcc = ""

        if (isHasSimCard()) {
            val imsi = telephonyManager?.subscriberId
            if (!imsi.isNullOrEmpty()) {
                mcc = imsi.take(3)
            }
        }

        mcc
    }

    /**
     * Required permission:
     * - Manifest.permission.READ_PHONE_STATE
     * - Manifest.permission.READ_PRIVILEGED_PHONE_STATE
     */
    @JvmStatic
    @SuppressLint("HardwareIds", "MissingPermission")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getMNC(): String = catchOrEmpty {
        var mnc = ""

        if (isHasSimCard()) {
            val imsi = telephonyManager?.subscriberId
            if (!imsi.isNullOrEmpty()) {
                mnc = imsi.substring(2, 5)
            }
        }

        mnc
    }

    /**
     * Return is has sim card
     */
    @JvmStatic
    fun isHasSimCard(): Boolean = catchOrBoolean {
        !telephonyManager?.simOperator.isNullOrEmpty()
    }
    /**
     * Return timezone id
     */
    @JvmStatic
    fun getTimeZoneId(): String = catchOrEmpty {
        TimeZone.getDefault().id
    }

    /**
     * Return timezone displayName
     */
    @JvmStatic
    fun getTimeZoneDisplayName(): String = catchOrEmpty {
        TimeZone.getDefault().getDisplayName(false, TimeZone.SHORT)
    }

    /**
     * Return cid
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_FINE_LOCATION)
    fun getCID(): String = catchOrEmpty {
        val location = telephonyManager?.cellLocation as? GsmCellLocation
        var cid = ""
        location?.let {
            cid = location.cid.toString()
        }
        cid
    }
}