package com.fintek.utils_androidx.network

import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.model.BaseResponse
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.reflect.TypeToken
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.TimeUnit

/**
 * [java.net.HttpURLConnection]
 */
class CoronetRequest {

    companion object {
        @JvmField
        var isLogEnable: Boolean = true
    }

    private var baseUrl: String = ""
    private var headers: Headers? = null
    private var connectTimeout: Long = 60000 //default millisecond
    private var readTimeout: Long = 60000 //default millisecond

    fun <T> call(request: Request, typeToken: TypeToken<T>) : RequestTask<T> = RequestTask {
        val resultStr: String = call(request.url) {
            requestMethod = request.method
            if (requestMethod == "POST") {
                doInput = true
                doOutput = true
                useCaches = false
            }
            connectTimeout = this@CoronetRequest.connectTimeout.toInt()
            readTimeout = this@CoronetRequest.readTimeout.toInt()
            headers?.forEach { (key, value) ->
                addRequestProperty(key, value)
            }

            if (request.headers.size > 0) {
                request.headers.forEach { (key, value) ->
                    addRequestProperty(key, value)
                }
            }
            request.body?.writeTo(outputStream)
            if (isLogEnable) {
                TimberUtil.e(
                    "${request.method}: $baseUrl${request.url}",
                    request.body.toString(),
                    "body: ${request.body?.getBytes()?.decodeToString()}"
                )
            }
        }

        if (isLogEnable) {
            TimberUtil.e(
                "result: $baseUrl${request.url}",
                resultStr
            )
        }

        GsonBuilder().enableComplexMapKeySerialization()
            .create()
            .fromJson(resultStr, typeToken.type)
    }

    private fun call(url: String, init: HttpURLConnection.() -> Unit): String {
        val urlInternal = URL(baseUrl + url)
        with(urlInternal.openConnection() as HttpURLConnection) {
            init()

            try {
                BufferedReader(InputStreamReader(inputStream)).use {
                    return it.readText()
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    class Builder {
        private var baseUrl: String = ""
        private var connectTimeout: Long = 60000 //default millisecond
        private var readTimeout: Long = 60000 //default millisecond

        private var headers: Headers.Builder = Headers.Builder()

        fun setBaseUrl(baseUrl: String) = apply {
            this.baseUrl = baseUrl
        }

        fun setConnectTimeout(timeout: Long, unit: TimeUnit) = apply {
            connectTimeout = unit.toMillis(timeout)
        }

        fun setReadTimeout(timeout: Long, unit: TimeUnit) = apply {
            readTimeout = unit.toMillis(timeout)
        }

        fun addHeader(headerName: String, headerValue: String) = apply {
            headers[headerName] = headerValue
        }

        fun build(): CoronetRequest {
            return CoronetRequest().apply {
                baseUrl = this@Builder.baseUrl
                headers = this@Builder.headers.build()
                connectTimeout = this@Builder.connectTimeout
                readTimeout = this@Builder.readTimeout
            }
        }
    }
}