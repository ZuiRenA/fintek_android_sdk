package com.fintek.utils_mexico.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.net.MacAddress
import android.net.NetworkRequest
import android.net.NetworkSpecifier
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.network.NetworkUtils.NetworkType.*
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.catchOrBoolean
import com.fintek.utils_mexico.ext.catchOrEmpty
import com.fintek.utils_mexico.model.Wifi

/**
 * Created by ChaoShen on 2021/4/15
 */
object NetworkMexicoUtils {
    internal val wifiManager by lazy {
        FintekMexicoUtils.requiredApplication.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    internal var configuredWifi: MutableList<Wifi> = mutableListOf()

    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun isWifiEnable(): Int = if (NetworkUtils.isWifiEnable()) 1 else 0

    @JvmStatic
    fun isWifiProxy(): String = if (catchOrBoolean { NetworkUtils.isWifiProxy() }) "1" else "0"

    @JvmStatic
    fun isEnableVpn(): String = if (catchOrBoolean { NetworkUtils.isEnableVpn() }) "1" else "0"

    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkType(): String = catchOrEmpty { when(NetworkUtils.getNetworkType()) {
        NETWORK_ETHERNET -> "ethernet"
        NETWORK_WIFI -> "wifi"
        NETWORK_5G -> "5g"
        NETWORK_4G -> "4g"
        NETWORK_3G -> "3g"
        NETWORK_2G -> "2g"
        NETWORK_UNKNOWN -> "unknown"
        NETWORK_NO -> ""
    } }

    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getConfiguredWifi(): List<Wifi> {
        val scanWifiList = wifiManager.scanResults
        return scanWifiList.mapNotNull {
            Wifi(
                bssid = it.BSSID,
                mac = it.BSSID,
                name = it.SSID.replace("\"", ""),
                ssid = it.SSID.replace("\"", "")
            )
        }
    }

    @SuppressLint("HardwareIds")
    @JvmStatic
    fun getCurrentWifi(): Wifi = try {
        val connectInfo = wifiManager.connectionInfo
        Wifi(
            bssid = connectInfo.bssid,
            mac = connectInfo.macAddress,
            name = connectInfo.ssid.replace("\"", ""),
            ssid = connectInfo.ssid.replace("\"", "")
        )
    } catch (e: Exception) {
        Wifi("", "", "", "")
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getIpAddressByWifi() = catchOrEmpty { NetworkUtils.getIpAddressByWifi() }

}