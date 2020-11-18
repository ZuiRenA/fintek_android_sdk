package com.fintek.utils_androidx.mac

import com.fintek.utils_androidx.UtilsBridge
import java.net.NetworkInterface
import kotlin.experimental.and

/**
 * Created by ChaoShen on 2020/11/17
 */
object MacUtils {
    private val HEX_DIGITS = byteArrayOf(
        '0'.toByte(), '1'.toByte(), '2'.toByte(), '3'.toByte(), '4'.toByte(), '5'.toByte(),
        '6'.toByte(), '7'.toByte(), '8'.toByte(), '9'.toByte(), 'A'.toByte(), 'B'.toByte(),
        'C'.toByte(), 'D'.toByte(), 'E'.toByte(), 'F'.toByte()
    )

    private const val INVALID_MAC_ADDRESS = "02:00:00:00:00:00"

    /**
     * Return long mac
     * e.g. 58098334275761
     *
     * @return long mac address
     */
    @JvmStatic
    fun getLongMac(): Long {
        val bytes = getMacByteArray()
        if (bytes == null || bytes.size != 6) return 0L

        var mac = 0L
        for (i in 0 until 6) {
            mac = mac or (bytes[i].toInt() and 0xFF).toLong()
            if (i != 5) {
                mac = mac shl 8
            }
        }

        return mac
    }

    /**
     * Return string mac
     * e.g. 34:D7:12:93:98:B1
     *
     * @return string mac address
     */
    @JvmStatic
    fun getMacAddress(): String {
        val mac = getMacByteArray().formatMac()
        if (mac.isEmpty() || mac == INVALID_MAC_ADDRESS) {
            return ""
        }
        return mac
    }

    private fun getMacByteArray(): ByteArray? {
        try {
            val enumeration = NetworkInterface.getNetworkInterfaces() ?: return null
            while (enumeration.hasMoreElements()) {
                val netInterface = enumeration.nextElement()
                if (netInterface.name == "wlan0") {
                    return netInterface.hardwareAddress
                }
            }
        } catch (e: Exception) {
            UtilsBridge.e(e.message, e)
        }
        return null
    }

    private fun ByteArray?.formatMac(): String {
        if (this == null || size != 6) return ""
        val mac = ByteArray(17)
        var p = 0

        for (i in 0 until 6) {
            val b = get(i)
            mac[p] = HEX_DIGITS[b.toInt() and 0xF0 shr 4]
            mac[p + 1] = HEX_DIGITS[b.toInt() and 0xF]

            if (i != 5) {
                mac[p + 2] = ':'.toByte()
                p += 3
            }
        }

        return String(mac)
    }
}