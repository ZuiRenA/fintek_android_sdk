package com.fintek.utils_androidx.device

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.common.GooglePlayServicesRepairableException
import java.io.File
import java.io.IOException
import java.util.*


@SuppressLint("HardwareIds", "MissingPermission")
object DeviceUtils {

    private val ROOT_FLAG = arrayOf(
        "/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/",
        "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/",
        "/system/sbin/", "/usr/bin/", "/vendor/bin/"
    )

    private val telephonyManager: TelephonyManager? by lazy {
        FintekUtils.requiredContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
    }

    /**
     * - if SDK_INT < 29 use [getDeviceId] it will return deviceId, but sometimes will ***null***
     * - if SDK_INT >= 29 if [isCustomIdentify] will use [getUniquePseudoId] else use [getImei].
     * Their different is [getImei] will return different result when different packageName,
     * But the [getUniquePseudoId] will be the same in most cases
     *
     * @return the IMEI (International Mobile Equipment Identity). Return null if IMEI is not
     * available.
     */
    @JvmStatic
    @JvmOverloads
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDeviceIdentify(isCustomIdentify: Boolean = false): String? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            if (isCustomIdentify) getUniquePseudoId() else getImei()
        } else { // when sdk version < 29 use getDeviceId
            getDeviceId()
        }
    }

    /**
     * If the calling app's target SDK is API level 28 or lower and the app has the
     * READ_PHONE_STATE permission then null is returned.
     *
     * If the calling app's target SDK is API level 28 or lower and the app does not have
     * the READ_PHONE_STATE permission, or if the calling app is targeting API level 29 or
     * higher, then a SecurityException is thrown.
     *
     * @return the device id. Return null if deviceId is not available
     */
    @JvmStatic // No support for device / profile owner or carrier privileges (b/72967236).
    @Deprecated("Use getImei which returns IMEI for GSM or getMeid which returns MEID for CDMA.")
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getDeviceId(): String? = try {
        telephonyManager?.deviceId
    } catch (e: Exception) {
        null
    }

    /**
     * This API requires one of the following:
     * - The caller holds the READ_PRIVILEGED_PHONE_STATE permission.
     * - If the caller is the device or profile owner, the caller holds the [Manifest.permission.READ_PHONE_STATE]] permission.
     * - The caller has carrier privileges [TelephonyManager.hasCarrierPrivileges]
     * - The caller is the default SMS app for the device
     *
     * The profile owner is an app that owns a managed profile on the device; for more details
     * see [Work profiles](https://developer.android.com/work/managed-profiles)
     * Access by profile owners is deprecated and will be removed in a future release.
     *
     * If the calling app does not meet one of these requirements then this method will behave
     * as follows:
     * - If the calling app's target SDK is API level 28 or lower and the app has the
     * READ_PHONE_STATE permission then null is returned.
     * - If the calling app's target SDK is API level 28 or lower and the app does not have
     * the READ_PHONE_STATE permission, or if the calling app is targeting API level 29 or
     * higher, then a SecurityException is thrown
     *
     * @return the IMEI (International Mobile Equipment Identity). Return null if IMEI is not
     * available.
     */
    @JvmStatic // No support for device / profile owner or carrier privileges (b/72967236).
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getImei(): String? = try {
        telephonyManager?.imei
    } catch (e: Exception) {
        null
    }


    /**
     * Custom imei, pieced together using device information
     * It is recommended to use with DeviceId, gaid
     *
     * @return the custom IMEI(International Mobile Equipment Identity)
     */
    @JvmStatic
    fun getUniquePseudoId(): String {
        val szDevIdShort = "35" + Build.BOARD.length % 10 + Build.BRAND.length % 10 + Build.CPU_ABI.length % 10 +
                Build.DEVICE.length % 10 + Build.DISPLAY.length % 10 + Build.HOST.length % 10 +
                Build.ID.length % 10 + Build.MANUFACTURER.length % 10 + Build.MODEL.length % 10 +
                Build.PRODUCT.length % 10 + Build.TAGS.length % 10 + Build.TYPE.length % 10 +
                Build.USER.length % 10

        val androidId = Settings.System.getString(
            FintekUtils.requiredContext.contentResolver,
            Settings.System.ANDROID_ID
        )

        val id = if (Build.SERIAL != Build.UNKNOWN) androidId + Build.SERIAL else androidId
        val serial: String? = try {
            // api >= 9 reflect to get serial
            Build::class.java.getField("SERIAL").get(null)?.toString()
        } catch (e: Exception) {
            // must init serial, use itself
            "serial"
        }

        return UUID(szDevIdShort.hashCode().toLong(), serial.hashCode().toLong()).toString() + id
    }

    /**
     * The gaid use single thread pool, This API requires one of the following:
     * - `implementation 'com.google.android.gms:play-services-analytics:17.0.0'` in gradle
     * - `apply plugin: 'com.google.gms.google-services'` in used module gradle
     *
     * @param consumer [com.fintek.utils_androidx.FintekUtils.Task]
     * @return the gaid(Google advertising id), this function is async
     */
    @JvmStatic
    fun getGaid(consumer: FintekUtils.Consumer<String>) {
        UtilsBridge.executeByCached(object : FintekUtils.Task<String>(consumer) {
            override fun doInBackground(): String {
                return try {
                    AdvertisingIdClient.getAdvertisingIdInfo(FintekUtils.requiredContext).id
                } catch (e: GooglePlayServicesNotAvailableException) {
                    ""
                } catch (e: GooglePlayServicesRepairableException) {
                    ""
                } catch (e: IOException) {
                    ""
                } catch (e: IllegalStateException) {
                    ""
                }
            }
        })
    }

    /**
     * On Android 8.0 (API level 26) and higher versions of the platform,
     * a 64-bit number (expressed as a hexadecimal string), unique to
     * each combination of app-signing key, user, and device.
     * Values of [getAndroidId] are scoped by signing key and user.
     * The value may change if a factory reset is performed on the
     * device or if an APK signing key changes.
     * ---------------------------------------------------------------------------------------------
     *
     * Note: For apps that were installed
     * prior to updating the device to a version of Android 8.0
     * (API level 26) or higher, the value of [getAndroidId] changes
     * if the app is uninstalled and then reinstalled after the OTA.
     * To preserve values across uninstalls after an OTA to Android 8.0
     * or higher.
     * ---------------------------------------------------------------------------------------------
     *
     * In versions of the platform lower than Android 8.0 (API level 26),
     * a 64-bit number (expressed as a hexadecimal string) that is randomly
     * generated when the user first sets up the device and should remain
     * constant for the lifetime of the user's device.
     * ---------------------------------------------------------------------------------------------
     *
     * Note: If the caller is an Instant App the ID is scoped
     * to the Instant App, it is generated when the Instant App is first installed and reset if
     * the user clears the Instant App.
     */
    @JvmStatic
    fun getAndroidId(): String = Settings.Secure.getString(
        FintekUtils.requiredContext.contentResolver, Settings.Secure.ANDROID_ID
    )

    /**
     * Returns the unique subscriber ID, for example, the IMSI for a GSM phone.
     * Return null if it is unavailable.
     *
     *
     * Requires Permission: READ_PRIVILEGED_PHONE_STATE, for the calling app to be the device or
     * profile owner and have the READ_PHONE_STATE permission, or that the calling app has carrier
     * privileges (see {@link #hasCarrierPrivileges}). The profile owner is an app that owns a
     * managed profile on the device; for more details @see
     * [Work profiles](https://developer.android.com/work/managed-profiles). Profile owner
     * access is deprecated and will be removed in a future release.
     *
     *
     * If the calling app does not meet one of these requirements then this method will behave
     * as follows:
     * - If the calling app's target SDK is API level 28 or lower and the app has the
     * READ_PHONE_STATE permission then null is returned.
     * - if the calling app's target SDK is API level 28 or lower and the app does not have
     * the READ_PHONE_STATE permission, or if the calling app is targeting API level 29 or
     * higher, then a SecurityException is thrown.
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.READ_PHONE_STATE)
    fun getImsi(): String? = try {
        telephonyManager?.subscriberId
    } catch (e: Exception) {
        null
    }

    /**
     * Return whether to root
     */
    @JvmStatic
    fun isRoot(): Boolean {
        val su = "su"
        for (flag in ROOT_FLAG) {
            if (File(flag + su).exists()) {
                return true
            }
        }
        return false
    }
}