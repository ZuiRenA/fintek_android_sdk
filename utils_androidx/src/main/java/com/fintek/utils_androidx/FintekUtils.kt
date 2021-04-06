package com.fintek.utils_androidx

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.`package`.PackageUtils
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
import com.fintek.utils_androidx.thread.SimpleTask

/**
 * Created by ChaoShen on 2020/11/4
 */
@SuppressLint("StaticFieldLeak")
@TargetApi(Build.VERSION_CODES.R)
object FintekUtils {
    /* Utils global context */
    private var context: Context? = null

    /* debug tag */
    internal const val TAG = "FintekUtils"

    /* Whether to enable debug log */
    var isDebugEnable = true

    /**
     * Utils used context
     * @see init
     */
    internal val requiredContext: Context get() = checkNotNull(context) {
        "Please use FintekUtils.init(Context context) first"
    }

    /**
     * init by context
     * @param context please use [android.app.Application] context
     */
    @JvmStatic
    fun init(context: Context) = apply {
        this.context = context
    }

    interface Consumer<T> {
        fun accept(t: T)
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    abstract class Task<Result> @JvmOverloads constructor (
        private val consumer: Consumer<Result>? = null
    ) : SimpleTask<Result>() {

        override fun onSuccess(result: Result) {
            consumer?.accept(result)
        }
    }
}