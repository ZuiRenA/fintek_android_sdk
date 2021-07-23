package com.fintek.utils_mexico.mac

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.fintek.utils_androidx.mac.MacUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.safely
import com.fintek.utils_mexico.ext.safelyVoid
import java.io.InputStreamReader
import java.io.LineNumberReader
import java.net.NetworkInterface
import java.util.*

/**
 * Created by ChaoShen on 2021/6/9
 */
object MacMexicoUtils {

    @JvmStatic
    fun getMacAddress(): String {
        var mac = getMacAddress1()
        if (mac.isNullOrEmpty()) {
            mac = getMacFromHardware()
        }
        return mac ?: MacUtils.INVALID_MAC_ADDRESS
    }

    @SuppressLint("HardwareIds")
    private fun getMacAddress1() = safely {
        val wifiManager = FintekMexicoUtils.requiredApplication.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val connectInfo = wifiManager.connectionInfo
        var mac = connectInfo.macAddress
        if (mac.isEmpty() || mac == MacUtils.INVALID_MAC_ADDRESS) {
            mac = getMacAddress2()
        }
        mac
    }

    @SuppressLint("MissingPermission")
    private fun getMacAddress2(): String {
        if (isOnline && NetworkUtils.getNetworkType() == NetworkUtils.NetworkType.NETWORK_WIFI) {
            var macSerial: String? = null
            var str: String? = ""

            safelyVoid {
                val pp = Runtime.getRuntime().exec("cat /sys/class/net/wlan0/address ")
                val ir = InputStreamReader(pp.inputStream)
                val input = LineNumberReader(ir)

                while (str != null) {
                    str = input.readLine()
                    if (str != null) {
                        macSerial = str?.trim()
                        break
                    }
                }
            }

            return macSerial.orEmpty()
        } else {
            return ""
        }
    }

    private val isOnline: Boolean
        @SuppressLint("MissingPermission")
        get() {
            val manager = FintekMexicoUtils.requiredApplication.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val info = manager.activeNetworkInfo
            return info != null && info.isConnected
        }


    private fun getMacFromHardware(): String? {
        safelyVoid {
            val all: List<NetworkInterface> =
                Collections.list(NetworkInterface.getNetworkInterfaces())
            val var1: Iterator<*> = all.iterator()
            while (var1.hasNext()) {
                val nif: NetworkInterface = var1.next() as NetworkInterface
                if (nif.name.equals("wlan0", true)) {
                    val macBytes: ByteArray = nif.hardwareAddress ?: return null
                    val mac = StringBuilder()
                    val var6 = macBytes.size
                    for (var7 in 0 until var6) {
                        val b = macBytes[var7]
                        mac.append(String.format("%02X:", b))
                    }
                    if (mac.isNotEmpty()) {
                        mac.deleteCharAt(mac.length - 1)
                    }
                    return mac.toString()
                }
            }
        }
        return null
    }
}