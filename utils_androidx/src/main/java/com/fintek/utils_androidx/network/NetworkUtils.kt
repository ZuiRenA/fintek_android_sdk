package com.fintek.utils_androidx.network

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.NetworkInfo
import android.net.wifi.WifiManager
import android.os.Build
import android.provider.Settings
import android.telephony.TelephonyManager
import android.text.format.Formatter
import android.webkit.WebSettings
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.annotation.WorkerThread
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.thread.Task
import java.io.*
import java.net.*
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


object NetworkUtils {

    enum class NetworkType(val flag: Int) {
        NETWORK_ETHERNET(0),
        NETWORK_WIFI(1),
        NETWORK_5G(2),
        NETWORK_4G(3),
        NETWORK_3G(4),
        NETWORK_2G(5),
        NETWORK_UNKNOWN(6),
        NETWORK_NO(7)
    }

    private val connectivityManager: ConnectivityManager?
        get() = FintekUtils.requiredContext.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager

    private val wifiManager: WifiManager?
        get() = FintekUtils.requiredContext.applicationContext.getSystemService(Context.WIFI_SERVICE) as? WifiManager

    private val NETWORK_CALLBACK_STACK = Stack<ConnectivityManager.NetworkCallback>()

    private const val NETWORK_IP_DISABLE = "0.0.0.0"

    /**
     * Open the settings of wireless.
     */
    @JvmStatic
    @JvmOverloads
    fun openWirelessSettings(flag: Int = Intent.FLAG_ACTIVITY_NEW_TASK) {
        FintekUtils.requiredContext.startActivity(
            Intent(Settings.ACTION_WIRELESS_SETTINGS)
                .setFlags(flag)
        )
    }

    /**
     * Return userAgent
     * @return userAgent
     */
    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    fun getUserAgent(): String {
        val userAgent =
            try {
                WebSettings.getDefaultUserAgent(FintekUtils.requiredContext)
            } catch (e: Exception) {
                System.getProperty("http.agent")
            }
        val sb = StringBuffer()
        if (!userAgent.isNullOrEmpty()) {
            var i = 0
            val length = userAgent.length
            while (i < length) {
                val c = userAgent[i]
                if (c <= '\u001f' || c >= '\u007f') {
                    sb.append(String.format("\\u%04x", c.toInt()))
                } else {
                    sb.append(c)
                }
                i++
            }
        }
        return sb.toString()
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
    @RequiresApi(Build.VERSION_CODES.Q)
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


    /**
     * Return whether wifi is enabled.
     *
     * @return true: enable
     *
     * false: disable
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun isWifiEnable(): Boolean = wifiManager?.isWifiEnabled ?: false

    /**
     * Enable or disable wifi.
     *
     * @param enabled True to enabled, false otherwise.
     */
    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.CHANGE_WIFI_STATE, Manifest.permission.ACCESS_WIFI_STATE])
    fun setWifiEnable(enabled: Boolean) {
        if (wifiManager == null)  return
        if (enabled == isWifiEnable()) return
        wifiManager?.isWifiEnabled = enabled
    }

    /**
     * Return whether wifi is connected.
     *
     * @return true: connected
     *
     * false: disconnected
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun isWifiConnected(): Boolean {
        val info = connectivityManager?.activeNetworkInfo ?: return false
        return info.type == ConnectivityManager.TYPE_WIFI
    }

    /**
     * Return the name of network operate
     *
     * @return the name of network operate
     */
    @JvmStatic
    fun getNetworkOperatorName(): String {
        val tm = FintekUtils.requiredContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return tm?.networkOperatorName ?: ""
    }

    @JvmStatic
    fun getNetworkOperator(): String {
        val tm = FintekUtils.requiredContext.getSystemService(Context.TELEPHONY_SERVICE) as? TelephonyManager
        return tm?.networkOperator ?: ""
    }

    /**
     * Return type of network.
     *
     * @return type of network
     * - [NetworkType.NETWORK_ETHERNET]
     * - [NetworkType.NETWORK_WIFI]
     * - [NetworkType.NETWORK_4G]
     * - [NetworkType.NETWORK_3G]
     * - [NetworkType.NETWORK_2G]
     * - [NetworkType.NETWORK_UNKNOWN]
     * - [NetworkType.NETWORK_NO]
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getNetworkType(): NetworkType {
        if (isEthernet()) return NetworkType.NETWORK_ETHERNET

        val info = connectivityManager?.activeNetworkInfo
        if (info != null && info.isAvailable) {
            when (info.type) {
                ConnectivityManager.TYPE_WIFI -> return NetworkType.NETWORK_WIFI
                ConnectivityManager.TYPE_MOBILE -> return when (info.subtype) {
                    TelephonyManager.NETWORK_TYPE_GSM, TelephonyManager.NETWORK_TYPE_GPRS,
                    TelephonyManager.NETWORK_TYPE_CDMA, TelephonyManager.NETWORK_TYPE_EDGE,
                    TelephonyManager.NETWORK_TYPE_1xRTT, TelephonyManager.NETWORK_TYPE_IDEN -> {
                        NetworkType.NETWORK_2G
                    }

                    TelephonyManager.NETWORK_TYPE_TD_SCDMA, TelephonyManager.NETWORK_TYPE_EVDO_A,
                    TelephonyManager.NETWORK_TYPE_UMTS, TelephonyManager.NETWORK_TYPE_EVDO_0,
                    TelephonyManager.NETWORK_TYPE_HSDPA, TelephonyManager.NETWORK_TYPE_HSUPA,
                    TelephonyManager.NETWORK_TYPE_EVDO_B, TelephonyManager.NETWORK_TYPE_EHRPD,
                    TelephonyManager.NETWORK_TYPE_HSPAP -> {
                        NetworkType.NETWORK_3G
                    }

                    TelephonyManager.NETWORK_TYPE_IWLAN, TelephonyManager.NETWORK_TYPE_LTE -> {
                        NetworkType.NETWORK_4G
                    }

                    TelephonyManager.NETWORK_TYPE_NR -> NetworkType.NETWORK_5G

                    else -> {
                        val subTypeName = info.subtypeName
                        if (subTypeName.equals("TD-SCDMA", true)
                            || subTypeName.equals("WCDMA", true)
                            || subTypeName.equals("CDMA2000", true)
                        ) {
                            NetworkType.NETWORK_3G
                        } else {
                            NetworkType.NETWORK_UNKNOWN
                        }
                    }
                }
                else -> return NetworkType.NETWORK_UNKNOWN
            }

        }
        return NetworkType.NETWORK_NO
    }

    /**
     * Return dns async
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getDnsAsync(
        consumer: FintekUtils.Consumer<String>
    ) = UtilsBridge.executeByCached(object : FintekUtils.Task<String>(consumer) {
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        override fun doInBackground(): String {
            return getDns()
        }
    })


    /**
     * Return Dns
     *
     * @return dns
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    fun getDns(): String {
        /**
         * 获取dns
         */
        var dnsServers = getDnsFromCommand()
        if (dnsServers == null || dnsServers.isEmpty()) {
            dnsServers = getDnsFromConnectionManager()
        }
        /**
         * 组装
         */
        val sb = StringBuffer()
        if (dnsServers != null) {
            for (i in dnsServers.indices) {
                sb.append(dnsServers[i])
                sb.append(" / ")
            }
        }
        return sb.toString()
    }

    /**
     * Return the ip address.
     *
     * @param useIPv4 True to use ipv4, false otherwise.
     * @param consumer The consumer
     * @return the task
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.INTERNET)
    fun getIPAddressAsync(
        useIPv4: Boolean,
        consumer: FintekUtils.Consumer<String>
    ): Task<String> = UtilsBridge.executeByCached(object : FintekUtils.Task<String>(consumer) {

        @RequiresPermission(Manifest.permission.INTERNET)
        override fun doInBackground(): String {
            return getIPAddress(useIPv4)
        }
    })

    /**
     * Return the ip address.
     * @param useIPv4 True to use ipv4, false otherwise.
     * @return the ip address
     */
    @SuppressLint("DefaultLocale")
    @JvmStatic
    @RequiresPermission(Manifest.permission.INTERNET)
    fun getIPAddress(useIPv4: Boolean): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            val adds = LinkedList<InetAddress>()

            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                // To prevent phone of xiaomi return "10.0.2.15"
                if (!ni.isUp || ni.isLoopback) continue

                val address = ni.inetAddresses
                while (address.hasMoreElements()) {
                    adds.addFirst(address.nextElement())
                }
            }

            adds.forEach {
                if (!it.isLoopbackAddress) {
                    val hostAddress = it.hostAddress
                    val isIPv4 = hostAddress.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return hostAddress
                    } else {
                        if (!isIPv4) {
                            val index = hostAddress.indexOf('%')
                            return if (index < 0) {
                                hostAddress.toUpperCase()
                            } else {
                                hostAddress.substring(0, index).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * Return the ip address of broadcast.
     *
     * @return the ip address of broadcast
     */
    @JvmStatic
    fun getBroadcastIPAddress(): String {
        try {
            val nis = NetworkInterface.getNetworkInterfaces()
            while (nis.hasMoreElements()) {
                val ni = nis.nextElement()
                if (!ni.isUp || ni.isLoopback) continue
                val ias = ni.interfaceAddresses
                val iterator = ias.iterator()

                while (iterator.hasNext()) {
                    val ia = iterator.next()
                    val broadcast = ia.broadcast
                    if (broadcast != null) return broadcast.hostAddress
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }

        return ""
    }

    /**
     * Return the domain address.
     *
     * @param domain The name of domain.
     * @param consumer The consumer
     * @return the task
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.INTERNET)
    fun getDomainAsync(
        domain: String,
        consumer: FintekUtils.Consumer<String>
    ): Task<String> = UtilsBridge.executeByCached(object : FintekUtils.Task<String>(consumer) {
        @RequiresPermission(Manifest.permission.INTERNET)
        override fun doInBackground(): String {
            return getDomainAddress(domain)
        }
    })

    /**
     * Return the domain address.
     *
     * @param domain The name of domain.
     * @return the domain address
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.INTERNET)
    fun getDomainAddress(domain: String): String = try {
        val ia = InetAddress.getByName(domain)
        ia.hostAddress
    } catch (e: UnknownHostException) {
        e.printStackTrace()
        ""
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getIpAddressByWifi(): String {
        val wm = wifiManager ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.ipAddress)
    }

    /**
     * Return the ip address by wifi.
     *
     * @return the ip address by wifi
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getGatewayByWifi(): String {
        val wm = wifiManager ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.gateway)
    }

    /**
     * Return the net mask by wifi.
     *
     * @return the net mask by wifi
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getNetMaskByWifi(): String {
        val wm = wifiManager ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.netmask)
    }

    /**
     * Return the server address by wifi.
     *
     * @return the server address by wifi
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getServerAddressByWifi(): String {
        val wm = wifiManager ?: return ""
        return Formatter.formatIpAddress(wm.dhcpInfo.serverAddress)
    }

    /**
     * Return the bssid.
     *
     * e.g. dc:8c:37:e5:cd:ac
     *
     * @return the bssid.
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getBSSID(): String {
        val wm = wifiManager ?: return ""
        val wi = wm.connectionInfo ?: return ""

        val bssid = wi.bssid
        if (bssid.isNullOrEmpty()) return ""
        return bssid
    }

    /**
     * Return the bssid list.
     *
     * @return the bssid list.
     */
    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getConfiguredBSSID(): List<String> {
        val wm = wifiManager ?: return emptyList()
        return try {
            wm.configuredNetworks.mapNotNull { it.BSSID }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Return connecting wifi name, it is same with [getSSID]
     *
     * @return the name
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getConnectingWifiName(): String {
        return getSSID()
    }

    /**
     * Return the ssid.
     *
     * @return the ssid.
     */
    @JvmStatic
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getSSID(): String {
        val wm = wifiManager ?: return ""
        val wi = wm.connectionInfo ?: return ""

        val ssid = wi.ssid
        if (ssid.isNullOrEmpty()) return ""

        if (ssid.length > 2 && ssid[0] == '"' && ssid[ssid.lastIndex] == '"') {
            return ssid.substring(1, ssid.lastIndex)
        }
        return ssid
    }

    /**
     * Return the ssid list.
     *
     * @return the ssid list.
     */
    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getConfiguredSSID(): List<String> {
        val wm = wifiManager ?: return emptyList()

        return try {
            wm.configuredNetworks.map {
                if (it.SSID.length > 2 && it.SSID[0] == '"' && it.SSID[it.SSID.lastIndex] == '"') {
                    it.SSID.substring(1, it.SSID.lastIndex)
                } else {
                    it.SSID
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    /**
     * Return mac by wifi
     *
     * Sometimes it will be "02:00:00:00:00:00", this time please use
     * [com.fintek.utils_androidx.mac.MacUtils.getMacAddress]
     *
     * @return the wifi address
     */
    @JvmStatic
    @SuppressLint("HardwareIds")
    @RequiresPermission(Manifest.permission.ACCESS_WIFI_STATE)
    fun getMacByWifi(): String {
        val wm = wifiManager ?: return ""
        val wi = wm.connectionInfo ?: return ""

        val wifiAddress = wi.macAddress
        if (wifiAddress.isNullOrEmpty()) return ""
        return wifiAddress
    }

    /**
     * Return mac list by wifi
     *
     * It will return always emptyList() when SDK_INT < 29 [Build.VERSION_CODES.Q]
     * use reflect try to get it to ignore throwable/exception
     *
     * @return the wifi address list
     */
    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    fun getConfiguredMacByWifi(): List<String> {
        val wm = wifiManager ?: return emptyList()
        val configuredNetworks = wm.configuredNetworks
        return try {
            configuredNetworks.map {
                val method = it.javaClass.getDeclaredMethod("getRandomizedMacAddress")
                val macAddress: Any? = method.invoke(null)
                macAddress.toString()
            }
        } catch (e: Exception) {
            emptyList()
        } catch (t: Throwable) {
            emptyList()
        }
    }


    /**
     * Return wifiIp first, if wifiIp is empty return gprsIp
     *
     * @return ip address
     */
    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE])
    fun getIpIgnorePublicIp(): String {
        val wifiIp = getIpAddressByWifi()
        val gprsIp = getGPRSIp()

        return when {
            wifiIp.isNotEmpty() && wifiIp.isNotBlank() -> wifiIp
            gprsIp.isNotEmpty() && gprsIp.isNotBlank() -> gprsIp
            else -> NETWORK_IP_DISABLE
        }
    }

    /**
     * Return publicIp first, if publicIp is empty return wifiIp, otherwise return gprsIp
     */
    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE])
    fun getIpWithPublicIp(consumer: FintekUtils.Consumer<String>) {
        UtilsBridge.executeByCached(object : FintekUtils.Task<String>(consumer) {
            @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE])
            override fun doInBackground(): String {
                val publicIp = getPublicIp()
                val wifiIp = getIpAddressByWifi()
                val gprsIp = getGPRSIp()

                return when {
                    publicIp.isNotEmpty() && publicIp.isNotBlank() -> publicIp
                    wifiIp.isNotEmpty() && wifiIp.isNotBlank() -> wifiIp
                    gprsIp.isNotEmpty() && gprsIp.isNotBlank() -> gprsIp
                    else -> NETWORK_IP_DISABLE
                }
            }
        })
    }

    /**
     * Return whether using ethernet.
     * @return true: yes
     *
     * false: no
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun isEthernet(): Boolean {
        val info = connectivityManager?.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) ?: return false
        val state = info.state ?: return false
        return state == NetworkInfo.State.CONNECTED || state == NetworkInfo.State.CONNECTING
    }


    /**
     * Use getprop to get dns
     */
    private fun getDnsFromCommand(): Array<String?>? {
        val dnsServers: LinkedList<String> = LinkedList()
        try {
            val process = Runtime.getRuntime().exec("getprop")
            val inputStream: InputStream = process.inputStream
            val lnr = LineNumberReader(InputStreamReader(inputStream))
            var line: String? = null
            while (lnr.readLine().also { line = it } != null) {
                val split = line!!.indexOf("]: [")
                if (split == -1) continue
                val property = line!!.substring(1, split)
                var value = line!!.substring(split + 4, line!!.length - 1)
                if (property.endsWith(".dns")
                    || property.endsWith(".dns1")
                    || property.endsWith(".dns2")
                    || property.endsWith(".dns3")
                    || property.endsWith(".dns4")
                ) {
                    val ip: InetAddress = InetAddress.getByName(value) ?: continue
                    value = ip.hostAddress
                    if (value == null) continue
                    if (value.isEmpty()) continue
                    dnsServers.add(value)
                }
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return if (dnsServers.isEmpty()) arrayOfNulls(0) else dnsServers.toArray(
            arrayOfNulls<String>(
                dnsServers.size
            )
        )
    }

    /**
     * use connection
     */
    @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
    private fun getDnsFromConnectionManager(): Array<String?>? {
        val dnsServers: LinkedList<String> = LinkedList()
        if (Build.VERSION.SDK_INT >= 21) {
            connectivityManager?.let {
                val activeNetworkInfo: NetworkInfo? = it.activeNetworkInfo
                if (activeNetworkInfo != null) {
                    for (network in it.allNetworks) {
                        val networkInfo: NetworkInfo? = it.getNetworkInfo(network)
                        if (networkInfo != null && networkInfo.type == activeNetworkInfo.type) {
                            val lp: LinkProperties? = it.getLinkProperties(network)
                            lp?.let { properties ->
                                for (address in properties.dnsServers) {
                                    dnsServers.add(address.hostAddress)
                                }
                            }
                        }
                    }
                }
            }
        }
        return if (dnsServers.isEmpty()) arrayOfNulls(0) else dnsServers.toArray(
            arrayOfNulls<String>(
                dnsServers.size
            )
        )
    }

    /**
     * Return Public net ip，please don't used it in Ui Thread
     */
    @WorkerThread
    private fun getPublicIp(): String {
        var infoUrl: URL? = null
        var inStream: InputStream? = null
        var ipLine = ""
        var httpConnection: HttpURLConnection? = null
        try {
            infoUrl = URL("http://pv.sohu.com/cityjson?ie=utf-8")
            val connection = infoUrl.openConnection()
            httpConnection = connection as HttpURLConnection
            val responseCode = httpConnection.responseCode
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inStream = httpConnection.inputStream
                val reader = BufferedReader(
                    InputStreamReader(inStream, "utf-8")
                )
                val sb = StringBuilder()
                var line: String?
                while (reader.readLine().also { line = it } != null) {
                    sb.append(
                        """
                    $line
                    
                    """.trimIndent()
                    )
                }
                val pattern: Pattern = Pattern
                    .compile("((?:(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(?:25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d))))")
                val matcher: Matcher = pattern.matcher(sb.toString())
                if (matcher.find()) {
                    ipLine = matcher.group()
                }
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            try {
                inStream?.close()
                httpConnection?.disconnect()
            } catch (e: IOException) {
                e.printStackTrace()
            } catch (ex: java.lang.Exception) {
                ex.printStackTrace()
            }
        }

        return ipLine
    }

    /**
     * Return Intranet ip
     */
    private fun getGPRSIp(): String {
        try {
            val en = NetworkInterface.getNetworkInterfaces()
            while (en.hasMoreElements()) {
                val networkInterface = en.nextElement()
                val addresses =
                    networkInterface.inetAddresses
                while (addresses.hasMoreElements()) {
                    val inetAddress = addresses.nextElement()
                    if (!inetAddress.isLoopbackAddress) {
                        return inetAddress.hostAddress.toString()
                    }
                }
            }
        } catch (e: SocketException) {
            e.printStackTrace()
        }
        return ""
    }
}