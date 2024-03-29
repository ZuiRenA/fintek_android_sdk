package com.fintek.utils_mexico.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.network.NetworkUtils.NetworkType.*
import com.fintek.utils_androidx.throwable.catchOrBoolean
import com.fintek.utils_androidx.throwable.catchOrEmpty
import com.fintek.utils_androidx.throwable.safely
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.model.Wifi
import kotlin.coroutines.Continuation
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


/**
 * Created by ChaoShen on 2021/4/15
 */
object NetworkMexicoUtils {
    private val wifiManager by lazy {
        FintekMexicoUtils.requiredApplication.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isWifiEnable(): Int = if (NetworkUtils.isWifiConnected()) 1 else 0

    @JvmStatic
    fun isWifiProxy(): String = if (catchOrBoolean { NetworkUtils.isWifiProxy() }) "1" else "0"

    @JvmStatic
    fun isEnableVpn(): String = if (catchOrBoolean { NetworkUtils.isEnableVpn() }) "1" else "0"

    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkType(): String = catchOrEmpty("none") { when(NetworkUtils.getNetworkType()) {
        NETWORK_WIFI -> "wifi"
        NETWORK_5G -> "5G"
        NETWORK_4G -> "4G"
        NETWORK_3G -> "3G"
        NETWORK_2G -> "2G"
        NETWORK_UNKNOWN, NETWORK_NO -> "none"
        else -> "other"
    } }


    @SuppressLint("HardwareIds")
    @JvmStatic
    fun getCurrentWifi(): Wifi = safely(Wifi("", "", "", "")) {
        val connectInfo = wifiManager.connectionInfo
        Wifi(
            bssid = connectInfo.bssid,
            mac = connectInfo.macAddress,
            name = connectInfo.ssid.replace("\"", ""),
            ssid = connectInfo.ssid.replace("\"", "")
        )
    }

    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE])
    suspend fun getIp(): String = suspendCoroutine {
        NetworkUtils.getIpAsync(object : FintekUtils.Consumer<String> {
            override fun accept(t: String) {
                it.resume(t)
            }
        })
    }

    suspend fun getConfiguredWifi(): List<Wifi> = suspendCoroutine { continuation ->
        safely {
            FintekMexicoUtils.requiredApplication.registerReceiver(WifiBroadcastReceiver(continuation), IntentFilter().apply {
                addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            })
        }
        safely {
            wifiManager.startScan()
        }
    }

    private class WifiBroadcastReceiver(private val continuation: Continuation<List<Wifi>>) : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val scanWifiList = wifiManager.scanResults
            continuation.resume(scanWifiList.mapNotNull {
                Wifi(
                    bssid = it.BSSID,
                    mac = it.BSSID,
                    name = it.SSID.replace("\"", ""),
                    ssid = it.SSID.replace("\"", "")
                )
            })
            FintekMexicoUtils.requiredApplication.unregisterReceiver(this)
        }
    }
}