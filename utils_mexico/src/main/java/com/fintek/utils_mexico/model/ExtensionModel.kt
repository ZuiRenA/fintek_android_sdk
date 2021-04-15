package com.fintek.utils_mexico.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass


@JsonClass(generateAdapter = true)
data class ExtensionModel(
    /**Device info*/
    @field:Json(name = "device_info") val device: DeviceInfo,
    /**Contact info*/
    @field:Json(name = "address_book") val contacts: List<Contact>,
    /**App info*/
    @field:Json(name = "app_list") val apps: List<App>,
    /**Sms Info*/
    @field:Json(name = "sms") val sms: List<Sms>,
    /**Calendar info*/
    @field:Json(name = "calendar_list") val calendars: List<Calendar>,

    @field:Json(name = "merchantId") var merchant: String = "",

    @field:Json(name = "userId") var userId: Int = 0,
)

@JsonClass(generateAdapter = true)
data class DeviceInfo(
    /**轨迹跟踪记录, 抓取所有的图片，然后抓取图片内的信息*/
    @field:Json(name = "albs") val albs: String?,
    /**音频外部文件个数*/
    @field:Json(name = "audio_external") val audioExternal: Int,
    /**音频内部文件个数*/
    @field:Json(name = "audio_internal") val audioInternal: Int,
    /**电量信息*/
    @field:Json(name = "battery_status") val batteryStatus: BatteryStatus,
    /**设备基本信息*/
    @field:Json(name = "general_data") val generalData: GeneralData,
    /**设备硬件信息*/
    @field:Json(name = "hardware") val hardware: Hardware,
    /**网路信息*/
    @field:Json(name = "network") val network: Network,
    /**其他信息*/
    @field:Json(name = "other_data") val otherData: OtherData,
    /**存储信息*/
    @field:Json(name = "new_storage") val newStorage: Storage,
    /**应用版本号对应的技术编码*/
    @field:Json(name = "build_id") val buildId: String?,
    /**APP版本号*/
    @field:Json(name = "build_name") val buildName: String?,
    /**联系人小组个数*/
    @field:Json(name = "contact_group") val contactGroup: Int?,
    /**抓取时间*/
    @field:Json(name = "create_time") val createTime: String,
    /**下载的文件个数*/
    @field:Json(name = "download_files") val downloadFiles: Int,
    /**图片外部文件个数*/
    @field:Json(name = "images_external") val imagesExternal: Int,
    /**图片内部文件个数*/
    @field:Json(name = "images_internal") val imagesInternal: Int,
    /**包名*/
    @field:Json(name = "package_name") val packageName: String,
    /**视频外部文件个数*/
    @field:Json(name = "video_external") val videoExternal: Int,
    /**视频内部文件个数*/
    @field:Json(name = "video_internal") val videoInternal: Int,
    /**GPS_ADID*/
    @field:Json(name = "gps_adid") val gpsAdid: String?,
    /**设备ID 1.能取到imei传imei 2.取不到imei传安卓ID 3.都没有,传机构自己的设备id,请尽量保证顺序*/
    @field:Json(name = "device_id") val deviceId: String,
    /**设备信息*/
    @field:Json(name = "device_info") val deviceInfo: String,
    /**设备系统类型 仅支持 android/ios*/
    @field:Json(name = "os_type") val osType: String,
    /**设备系统版本*/
    @field:Json(name = "os_version") val osVersion: String,
    /**设备ip地址*/
    @field:Json(name = "ip") val ip: String,
    /**内存大小，获取不到传 -1*/
    @field:Json(name = "memory") val memory: String?,
    /**存储空间大小，获取不到传 -1*/
    @field:Json(name = "storage") val storage: String?,
    /**存储空间大小，获取不到传 -*/
    @field:Json(name = "unuse_storage") val unUseStorage: String?,
    /**经度*/
    @field:Json(name = "gps_longitude") val gpsLongitude: String?,
    /**纬度*/
    @field:Json(name = "gps_latitude") val gpsLatitude: String?,
    /**gps地址*/
    @field:Json(name = "gps_address") val gpsAddress: String?,
    /**详细地址*/
    @field:Json(name = "address_info") val addressInfo: String,
    /**是否wifi 0不是 1是*/
    @field:Json(name = "wifi") val isWifi: Int,
    /**wifi名称 获取不到传 -1*/
    @field:Json(name = "wifi_name") val wifiName: String?,
    /**电量*/
    @field:Json(name = "battery") val battery: Int?,
    /**是否root 0未root 1已root*/
    @field:Json(name = "is_root") val isRoot: Int,
    /**是否是模拟器 0不是模拟器 1是模拟器*/
    @field:Json(name = "is_simulator") val isSimulator: Int,
    /**上次活跃时间戳*/
    @field:Json(name = "last_login_time") val lastLoginTime: Long?,
    /**图片数量，等于外部图片加内部图片，获取不到传-1*/
    @field:Json(name = "pic_count") val picCount: Int,
    /**sim卡串号*/
    @field:Json(name = "imsi") val imsi: String,
    /**mac地址 获取不到传-1*/
    @field:Json(name = "mac") val mac: String,
    /**SD卡存储空间大小，获取不到传-1*/
    @field:Json(name = "sdcard") val sdCard: String?,
    /**SD卡未使用存储空间大小，获取不到传-1*/
    @field:Json(name = "unuse_sdcard") val unUseSdCard: String?,
    /**ios系统idfv*/
    @field:Json(name = "idfv") val idfv: String?,
    /**ios系统idfa*/
    @field:Json(name = "idfa") val idfa: String?,

    /**安卓系统imei，获取不到传-1*/
    @field:Json(name = "imei") val ime: String,
    /**屏幕分辨率*/
    @field:Json(name = "resolution") val resolution: String?,
    /**品牌*/
    @field:Json(name = "brand") val brand: String?
)

@JsonClass(generateAdapter = true)
data class Contact(
    @field:Json(name = "contact_display_name") val name: String,

    @field:Json(name = "number") val phoneNumber: String,

    @field:Json(name = "up_time") val updateTime: Long,

    @field:Json(name = "last_time_contacted") val lastTimeContacted: Long,

    @field:Json(name = "times_contacted") val timesContacted: Int,
)

@JsonClass(generateAdapter = true)
data class App(
    @field:Json(name = "app_name") val name: String,

    @field:Json(name = "package_name") val packageName: String,

    @field:Json(name = "version_code") val versionCode: String,

    @field:Json(name = "obtain_time") val obtainTime: Long,

    @field:Json(name = "app_type") val appType: String,

    @field:Json(name = "in_time") val installTime: Long,

    @field:Json(name = "up_time") val updateTime: Long,

    @field:Json(name = "app_version") val appVersion: String,
)

@JsonClass(generateAdapter = true)
data class Sms(
    @field:Json(name = "other_phone") val otherPhone: String,

    @field:Json(name = "content") val content: String,

    @field:Json(name = "seen") val seen: Int,

    @field:Json(name = "status") val status: Int,

    @field:Json(name = "time") val time: Long,

    @field:Json(name = "type") val type: Int,

    @field:Json(name = "package_name") val packageName: String,
)

@JsonClass(generateAdapter = true)
data class Calendar(
    @field:Json(name = "event_title") val eventTitle: String,

    @field:Json(name = "event_id") val eventId: Long,

    @field:Json(name = "end_time") val endTime: Long,

    @field:Json(name = "start_time") val startTime: Long,

    @field:Json(name = "description") val des: String,

    @field:Json(name = "reminders") val reminders: List<Reminder>
)

@JsonClass(generateAdapter = true)
data class BatteryStatus(
    /**电池电量 e.g. 2730(传的数据不用带单位)mAH*/
    @field:Json(name = "battery_level") val batteryLevel: String,
    /**电池总电量 e.g. 3900(传的数据不用带单位)mAH*/
    @field:Json(name = "battery_max") val batteryMax: String,
    /**电池百分比*/
    @field:Json(name = "battery_pct") val batteryPercent: Int,
    /**是否交流充电 0不在 1在*/
    @field:Json(name = "is_ac_charge") val isAcCharge: Int,
    /**是否正在充电 0不在 1在*/
    @field:Json(name = "is_charging") val isCharging: Int,
    /**是否usb充电 0不在 1在*/
    @field:Json(name = "is_usb_charge") val isUsbCharge: Int
)

@JsonClass(generateAdapter = true)
data class GeneralData(
    /**Android id*/
    @field:Json(name = "and_id") val androidId: String,
    /**设备当前时间*/
    @field:Json(name = "currentSystemTime") val currentSystemTime: Long,
    /**开机时间到现在的毫秒数（包括睡眠时间）*/
    @field:Json(name = "elapsedRealtime") val elapsedRealtime: Long,
    /**google advertising id(google 广告 id)*/
    @field:Json(name = "gaid") val gaid: String,
    /**imei*/
    @field:Json(name = "imei") val imei: String,
    /**是否开启debug调试*/
    @field:Json(name = "is_usb_debug") val isUsbDebug: String,
    /**是否使用代理*/
    @field:Json(name = "is_using_proxy_port") val isUsingProxyPort: String,

    @field:Json(name = "is_using_vpn") val isUsingVpn: String,

    @field:Json(name = "language") val language: String,

    @field:Json(name = "locale_display_language") val localeDisplayLanguage: String,

    @field:Json(name = "locale_iso_3_country") val localeISO3Country: String,

    @field:Json(name = "locale_iso_3_language") val localeISO3Language: String,

    @field:Json(name = "mac") val mac: String,

    @field:Json(name = "network_operator_name") val networkOperatorName: String,

    @field:Json(name = "network_type") val networkType: String,

    @field:Json(name = "network_type_new") val networkTypeNew: String,

    @field:Json(name = "phone_number") val phoneNumber: String,

    @field:Json(name = "phone_type") val phoneType: Int,

    @field:Json(name = "sensor_list") val sensor: List<Sensor>,

    @field:Json(name = "time_zone_id") val timeZoneId: Int,

    @field:Json(name = "uptimeMillis") val uptimeMillis: Int,

    @field:Json(name = "uuid") val uuid: String
)

@JsonClass(generateAdapter = true)
data class Sensor(
    @field:Json(name = "maxRange") val maxRange: String,

    @field:Json(name = "minDelay") val minDelay: String,

    @field:Json(name = "name") val name: String,

    @field:Json(name = "power") val power: String,

    @field:Json(name = "resolution") val resolution: String,

    @field:Json(name = "type") val type: String,

    @field:Json(name = "vendor") val vendor: String,

    @field:Json(name = "version") val version: String
)

@JsonClass(generateAdapter = true)
data class Hardware(
    @field:Json(name = "board") val board: String,

    @field:Json(name = "brand") val brand: String,

    @field:Json(name = "cores") val cores: Int,

    @field:Json(name = "device_height") val deviceHeight: Int,

    @field:Json(name = "device_name") val deviceName: String,

    @field:Json(name = "device_width") val deviceWidth: Int,

    @field:Json(name = "model") val model: String,

    @field:Json(name = "physical_size") val physicalSize: String,

    @field:Json(name = "production_date") val productionDate: Long,

    @field:Json(name = "release") val release: String,

    @field:Json(name = "sdk_version") val sdkVersion: String,

    @field:Json(name = "serial_number") val serialNumber: String,
)

@JsonClass(generateAdapter = true)
data class Network(
    @field:Json(name = "ip") val ip: String,

    @field:Json(name = "configured_wifi") val configuredWifi: List<Wifi>,

    @field:Json(name = "current_wifi") val currentWifi: Wifi,

    @field:Json(name = "wifi_count") val wifiCount: Int
)

@JsonClass(generateAdapter = true)
data class Wifi(
    @field:Json(name = "bssid") val bssid: String,

    @field:Json(name = "mac") val mac: String,

    @field:Json(name = "name") val name: String,

    @field:Json(name = "ssid") val ssid: String,
)

@JsonClass(generateAdapter = true)
data class OtherData(
    @field:Json(name = "dbm") val dbm: String,

    @field:Json(name = "keyboard") val keyboard: Int,

    @field:Json(name = "last_boot_time") val lastBootTime: Long,

    @field:Json(name = "root_jailbreak") val isRoot: Int,

    @field:Json(name = "simulator") val isSimulator: Int,
)

@JsonClass(generateAdapter = true)
data class Storage(
    @field:Json(name = "app_free_memory") val appFreeMemory: String,

    @field:Json(name = "app_max_memory") val appMaxMemory: String,

    @field:Json(name = "app_total_memory") val appTotalMemory: String,

    @field:Json(name = "contain_sd") val containSd: String,

    @field:Json(name = "extra_sd") val extraSd: String,

    @field:Json(name = "internal_storage_total") val internalStorageTotal: Long,

    @field:Json(name = "internal_storage_usable") val internalStorageUsable: Long,

    @field:Json(name = "memory_card_free_size") val memoryCardFreeSize: Long,

    @field:Json(name = "memory_card_size") val memoryCardSize: Long,

    @field:Json(name = "memory_card_size_use") val memoryCardUsedSize: Long,

    @field:Json(name = "memory_card_usable_size") val memoryCardUsableSize: Long,

    @field:Json(name = "ram_total_size") val ramTotalSize: String,

    @field:Json(name = "ram_usable_size") val ramUsableSize: String,
)

@JsonClass(generateAdapter = true)
data class Reminder(
    @field:Json(name = "eventId") val eventId: Int,

    @field:Json(name = "method") val method: Int,

    @field:Json(name = "minutes") val minutes: Int,

    @field:Json(name = "reminder_id") val reminderId: Int,
)

