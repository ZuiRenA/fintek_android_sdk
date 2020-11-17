package com.fintek.utils_androidx.network

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.device.DeviceUtils
import java.util.*


object NetworkUtils {

    enum class NetworkType {
        NETWORK_ETHERNET,
        NETWORK_WIFI,
        NETWORK_5G,
        NETWORK_4G,
        NETWORK_3G,
        NETWORK_2G,
        NETWORK_UNKNOWN,
        NETWORK_NO
    }

    private val connectivityManager: ConnectivityManager? by lazy {
        FintekUtils.requiredContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
    }

    private val NETWORK_CALLBACK_STACK = Stack<ConnectivityManager.NetworkCallback>()

    /**
     * Open the settings of wireless.
     */
    @JvmStatic
    @JvmOverloads
    fun openWirelessSettings(flag: Int = Intent.FLAG_ACTIVITY_NEW_TASK) {
        FintekUtils.requiredContext.startActivity(Intent(Settings.ACTION_WIRELESS_SETTINGS)
            .setFlags(flag))
    }

    /**
     * Return whether to enable network
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isNetworkEnable(): Boolean {
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.isAvailable ?: false
    }

    /**
     * Return whether network is connected.
     * @return true: connected
     *
     * false: disconnected
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isConnected(): Boolean {
        val networkInfo = connectivityManager?.activeNetworkInfo
        return networkInfo?.isConnected ?: false
    }

    /**
     * Register callback to listen network state
     *
     * @param callback callback invoke network state change
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @RequiresApi(Build.VERSION_CODES.N)
    fun registerNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        NETWORK_CALLBACK_STACK.push(callback)
        connectivityManager?.registerDefaultNetworkCallback(callback)
    }

    /**
     * Unregister callback
     *
     * @param callback listening callback
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @RequiresApi(Build.VERSION_CODES.N)
    fun unregisterNetworkCallback(callback: ConnectivityManager.NetworkCallback) {
        connectivityManager?.unregisterNetworkCallback(callback)
    }

    /**
     * Remove the stack last callback from [ConnectivityManager]
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @RequiresApi(Build.VERSION_CODES.N)
    fun removeLastNetworkCallback() {
        val callback = NETWORK_CALLBACK_STACK.pop()
        unregisterNetworkCallback(callback)
    }

    /**
     * Remove the stack all callback from [ConnectivityManager]
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    @RequiresApi(Build.VERSION_CODES.N)
    fun removeAllNetworkCallback() {
        for (networkCallback in NETWORK_CALLBACK_STACK) {
            unregisterNetworkCallback(networkCallback)
        }

        NETWORK_CALLBACK_STACK.clear()
    }

    /**
     * Return whether mobile data is enabled.
     *
     * Required permissions:
     * - [Manifest.permission.ACCESS_NETWORK_STATE]
     *
     * Optional permissions:
     * - [Manifest.permission.MODIFY_PHONE_STATE], Of course you can not add this permission
     * [why need it](https://developer.android.com/reference/android/telephony/TelephonyManager#isDataEnabled())
     *
     * @return true enable
     *
     * false: disable
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isMobileDataEnable(): Boolean {
        try {
            val tm = FintekUtils.requiredContext.getSystemService(Context.TELEPHONY_SERVICE)
                    as? TelephonyManager ?: return false

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                return tm.isDataEnabled
            }

            val getMobileDataEnableMethod = tm.javaClass.getDeclaredMethod("getDataEnabled")
            return getMobileDataEnableMethod.invoke(tm) as Boolean
        } catch (e: Exception) {
            UtilsBridge.e("NetworkUtils.isMobileDataEnable", e)
        }

        return false
    }

    /**
     * Return whether using 5G.
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun is5G(): Boolean {
        val info = connectivityManager?.activeNetworkInfo ?: return false
        return info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_NR
    }
    

    /**
     * Return whether using 4G.
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun is4G(): Boolean {
        val info = connectivityManager?.activeNetworkInfo ?: return false
        return info.isAvailable && info.subtype == TelephonyManager.NETWORK_TYPE_LTE
    }
}