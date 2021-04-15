package com.fintek.utils_mexico

import android.annotation.SuppressLint
import android.app.Application
import android.media.Image
import android.os.Build
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.`package`.PackageUtils
import com.fintek.utils_androidx.app.AppUtils
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.hardware.HardwareUtils
import com.fintek.utils_androidx.mac.MacUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.phone.PhoneUtils
import com.fintek.utils_androidx.storage.RuntimeMemoryUtils
import com.fintek.utils_androidx.storage.StorageUtils
import com.fintek.utils_mexico.model.*
import com.fintek.utils_mexico.query.AudioQueryUtils
import com.fintek.utils_mexico.query.DownloadQueryUtils
import com.fintek.utils_mexico.query.ImageQueryUtils
import com.fintek.utils_mexico.query.VideoQueryUtils
import com.fintek.utils_mexico.storage.StorageMexicoUtils

/**
 * Created by ChaoShen on 2021/4/12
 * Init Fintek mexico utils
 */
object FintekMexicoUtils {
    private var application: Application? = null

    internal val requiredApplication: Application get() = requireNotNull(application) {
        "FintekMexicoUtils must init first, please use FintekMexicoUtils.init(Application application)"
    }

    @JvmStatic
    fun init(application: Application) = apply {
        this.application = application
        FintekUtils.init(application)
    }

    fun getTemp() = ExtensionModel(
        device = getTempDevice(),
        contacts = listOf(Contact("shen", "122121", 211212, 121212, 10)),
        apps = listOf(App("Mexico Test", "om.shen.test", "1"
            , 121212, "0", 1212121, 3789873, "20210413")),
        sms = listOf(Sms("12212", "test content", 1, 1, 122112, 0, "com.shen.test")),
        calendars = listOf(Calendar("title", 12, 128978327, 4389781971897, "des", listOf(
            Reminder(12, 0, 122112, 1)
        )))
    )

    @SuppressLint("MissingPermission", "NewApi")
    private fun getTempDevice() = DeviceInfo(
        albs = "",
        audioExternal = AudioQueryUtils.getExternalAudioCount(),
        audioInternal = AudioQueryUtils.getInternalAudioCount(),
        batteryStatus = getTempBatteryStatus(),
        generalData = getTempGeneralData(),
        hardware = getTempHardware(),
        network = getTempNetwork(),
        otherData = getTempOtherData(),
        newStorage = getTempStorage(),
        buildId = AppUtils.getAppVersionCode().toString(),
        buildName = AppUtils.getAppVersionName(),
        contactGroup = 1,
        createTime = "2021-04-15 11:57:00",
        downloadFiles = DownloadQueryUtils.getDownloadFileCount(),
        imagesExternal = ImageQueryUtils.getExternalImageCount(),
        imagesInternal = ImageQueryUtils.getInternalImageCount(),
        packageName = "com.shen.test",
        videoExternal = VideoQueryUtils.getExternalVideoCount(),
        videoInternal = VideoQueryUtils.getInternalVideoCount(),
        gpsAdid = "assda",
        deviceId = DeviceUtils.getDeviceIdentify(true).orEmpty(),
        deviceInfo = "",
        osType = "android",
        osVersion = Build.VERSION.SDK_INT.toString(),
        ip = NetworkUtils.getIpAddressByWifi(),
        memory = RuntimeMemoryUtils.getTotalMemory(),
        storage = StorageMexicoUtils.getTotalStorageSize(),
        unUseStorage = "3GB",
        gpsLatitude = "31.111",
        gpsLongitude = "120.322",
        gpsAddress = "Asia/SH",
        addressInfo = "ShangHai",
        isWifi = 1,
        wifiName = "WeWork",
        battery = 70,
        isRoot = 0,
        isSimulator = 0,
        lastLoginTime = System.currentTimeMillis(),
        picCount = 195,
        imsi = DeviceUtils.getImsi().orEmpty(),
        mac = MacUtils.getMacAddress(),
        sdCard = "2GB",
        unUseSdCard = "2GB",
        idfa = "",
        idfv = "",
        ime = DeviceUtils.getImei().orEmpty(),
        resolution = "1920*1080",
        brand = HardwareUtils.getBrand(),
    )

    private fun getTempBatteryStatus() = BatteryStatus(
        batteryLevel = "2700",
        batteryMax = "3600",
        batteryPercent = 75,
        isAcCharge = 0,
        isCharging = 1,
        isUsbCharge = 1
    )

    @SuppressLint("MissingPermission", "NewApi")
    private fun getTempGeneralData() = GeneralData(
        androidId = DeviceUtils.getAndroidId(),
        currentSystemTime = 123131,
        elapsedRealtime = 122112331,
        gaid = "",
        imei = DeviceUtils.getImei() ?: "",
        isUsbDebug = "0",
        isUsingProxyPort = "0",
        isUsingVpn = "0",
        language = "zho",
        localeDisplayLanguage = "zh",
        localeISO3Country = "cn",
        localeISO3Language = "zh",
        mac = MacUtils.getMacAddress(),
        networkOperatorName = NetworkUtils.getNetworkOperatorName(),
        networkType = "wifi",
        networkTypeNew = "wifi",
        phoneNumber = "1232112",
        phoneType = 0,
        sensor = listOf(
            Sensor("180", "0",
            "lala", "lala1", "resolution", "type", "vendor", "version")
        ),
        timeZoneId = PhoneUtils.getTimeZoneId().toIntOrNull() ?: 1,
        uptimeMillis = 1121212,
        uuid = DeviceUtils.getUniquePseudoId()
    )

    private fun getTempHardware() = Hardware(
        board = "",
        brand = HardwareUtils.getBrand(),
        cores = 4,
        deviceHeight = 1920,
        deviceName = "",
        deviceWidth = 1080,
        model = HardwareUtils.getModel(),
        physicalSize = "5.223113",
        productionDate = 127129812,
        release = HardwareUtils.getRelease(),
        sdkVersion = HardwareUtils.getSDKVersion().toString(),
        serialNumber = HardwareUtils.getSerialNumber()
    )

    @SuppressLint("MissingPermission")
    private fun getTempNetwork() = Network(
        ip = NetworkUtils.getIpAddressByWifi(),
        configuredWifi = listOf(Wifi("bssid", "mac", "name", "ssid")),
        currentWifi = Wifi("bssid", "mac", "name", "ssid"),
        wifiCount = 1
    )

    private fun getTempOtherData() = OtherData(
        dbm = "",
        keyboard = 1,
        lastBootTime = 1211212,
        isRoot = 0,
        isSimulator = 0
    )

    private fun getTempStorage() = Storage(
        appFreeMemory = "400000",
        appMaxMemory = "800000",
        appTotalMemory = "800000",
        containSd = "1",
        extraSd = "0",
        internalStorageTotal = 320000000,
        internalStorageUsable = 90000000,
        memoryCardSize = 1000000,
        memoryCardFreeSize = 4000000,
        memoryCardUsedSize = 5000000,
        memoryCardUsableSize = 9000000,
        ramTotalSize = "32000000",
        ramUsableSize = "25000000"
    )
}