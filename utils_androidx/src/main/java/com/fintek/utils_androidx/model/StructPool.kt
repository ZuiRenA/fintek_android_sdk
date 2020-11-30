package com.fintek.utils_androidx.model

import android.util.Log
import com.fintek.utils_androidx.network.NetworkUtils
import java.util.*

/**
 * Created by ChaoShen on 2020/11/26
 */
data class StructPool(
    val imageList: List<ImageInfo?>,
    val smsList: List<Sms>,
    val callLogList: List<CallLog>,
    val contactList: List<Contact>,
    val deviceInfo: DeviceInfo,
    val hardwareInfo: HardwareInfo,
    val phoneInfo: PhoneInfo,
    val batteryInfo: BatteryInfo,
    val networkInfo: NetworkInfo,
    val storageInfo: StorageInfo
)

data class DeviceInfo(
    val imei: String?,
    val gaid: String,
    val androidId: String,
    val imsi: String?,
    val isRoot: Boolean,
    val isLocationServiceEnable: Boolean
)

data class HardwareInfo(
    val model: String,
    val brand: String,
    val deviceName: String,
    val product: String,
    val systemVersion: String,
    val release: String,
    val sdkVersion: Int,
    val physicalSize: String,
    val serialNumber: String
)

data class PhoneInfo(
    val phoneType: Int,
    val phoneNumber: String,
    val mcc: String,
    val mnc: String,
    val localeIso3Language: String,
    val localeIso3Country: String,
    val timeZoneID: String,
    val cid: String,
)

data class BatteryInfo(
    val percent: Int,
    val isCharging: Boolean,
    val isUsbCharging: Boolean,
    val isAcCharging: Boolean
)

data class NetworkInfo(
    val networkType: Int,
    val isNetwork: Boolean,
    val networkOperatorName: String,
    val networkOperator: String,
    val userAgent: String,
    val ip: String,
    val name: String,
    val bssid: String,
    val ssid: String,
    val mac: String,
    val configuredBSSID: List<String>,
    val configuredSSID: List<String>,
    val configuredMac: List<String>,
)

data class StorageInfo(
    val mainStorage: String,
    val externalStorage: String,
    val storageTotalSize: Long,
    val storageAvailableSize: Long,
    val sdCardTotalSize: Long,
    val sdCardAvailableSize: Long
)