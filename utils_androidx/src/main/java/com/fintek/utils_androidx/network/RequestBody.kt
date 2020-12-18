package com.fintek.utils_androidx.network

import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.UtilsBridge.checkOffsetAndCount
import com.fintek.utils_androidx.network.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets.UTF_8
import kotlin.jvm.Throws


/**
 * Come from OkHttp
 */
abstract class RequestBody {

    /** Returns the Content-Type header for this body. */
    abstract fun contentType(): MediaType?

    /**
     * Returns the number of bytes that will be written to sink in a call to [writeTo],
     * or -1 if that count is unknown.
     */
    @Throws(IOException::class)
    open fun contentLength() = -1L


    @Throws(IOException::class)
    abstract fun writeTo(outputStream: OutputStream)


    /**
     * A duplex request body is special in how it is **transmitted** on the network and
     * in the **API contract** between OkHttp and the application.
     *
     * This method returns false unless it is overridden by a subclass.
     *
     * ### Duplex Transmission
     *
     * With regular HTTP calls the request always completes sending before the response may begin
     * receiving. With duplex the request and response may be interleaved! That is, request body bytes
     * may be sent after response headers or body bytes have been received.
     *
     * Though any call may be initiated as a duplex call, only web servers that are specially
     * designed for this nonstandard interaction will use it. As of 2019-01, the only widely-used
     * implementation of this pattern is [gRPC][grpc].
     *
     * Because the encoding of interleaved data is not well-defined for HTTP/1, duplex request
     * bodies may only be used with HTTP/2. Calls to HTTP/1 servers will fail before the HTTP request
     * is transmitted. If you cannot ensure that your client and server both support HTTP/2, do not
     * use this feature.
     *
     * ### Duplex APIs
     *
     * With regular request bodies it is not legal to write bytes to the sink passed to
     * [RequestBody.writeTo] after that method returns. For duplex requests bodies that condition is
     * lifted. Such writes occur on an application-provided thread and may occur concurrently with
     * reads of the [ResponseBody]. For duplex request bodies, [writeTo] should return
     * quickly, possibly by handing off the provided request body to another thread to perform
     * writing.
     *
     * [grpc]: https://github.com/grpc/grpc/blob/master/doc/PROTOCOL-HTTP2.md
     */
    open fun isDuplex(): Boolean = false


    /**
     * Returns true if this body expects at most one call to [writeTo] and can be transmitted
     * at most once. This is typically used when writing the request body is destructive and it is not
     * possible to recreate the request body after it has been sent.
     *
     * This method returns false unless it is overridden by a subclass.
     *
     * By default OkHttp will attempt to retransmit request bodies when the original request fails
     * due to any of:
     *
     *  * A stale connection. The request was made on a reused connection and that reused connection
     *    has since been closed by the server.
     *  * A client timeout (HTTP 408).
     *  * A authorization challenge (HTTP 401 and 407) that is satisfied by the [Authenticator].
     *  * A retryable server failure (HTTP 503 with a `Retry-After: 0` response header).
     *  * A misdirected request (HTTP 421) on a coalesced connection.
     */
    open fun isOneShot(): Boolean = false

    open fun getBytes(): ByteArray = byteArrayOf()

    override fun toString(): String = "MediaType: ${contentType()}, contentLength: ${contentLength()}"

    companion object {
        /**
         * Returns a new request body that transmits this string. If [contentType] is non-null and lacks
         * a charset, this will use UTF-8.
         */
        @RequiresApi(Build.VERSION_CODES.KITKAT)
        @JvmStatic
        @JvmName("create")
        fun String.toRequestBody(contentType: MediaType? = null): RequestBody {
            var charset: Charset = UTF_8
            var finalContentType: MediaType? = contentType
            if (contentType != null) {
                val resolvedCharset = contentType.charset()
                if (resolvedCharset == null) {
                    charset = UTF_8
                    finalContentType = "$contentType; charset=utf-8".toMediaTypeOrNull()
                } else {
                    charset = resolvedCharset
                }
            }
            val bytes = toByteArray(charset)
            return bytes.toRequestBody(finalContentType, 0, bytes.size)
        }


        /** Returns a new request body that transmits this. */
        @JvmOverloads
        @JvmStatic
        @JvmName("create")
        fun ByteArray.toRequestBody(
            contentType: MediaType? = null,
            offset: Int = 0,
            byteCount: Int = size
        ): RequestBody {
            checkOffsetAndCount(size.toLong(), offset.toLong(), byteCount.toLong())
            return object : RequestBody() {
                override fun contentType() = contentType

                override fun contentLength() = byteCount.toLong()

                override fun writeTo(outputStream: OutputStream) {
                    outputStream.write(this@toRequestBody, offset, byteCount)
                }

                override fun getBytes(): ByteArray = this@toRequestBody
            }
        }

        /** Returns a new request body that transmits the content of this. */
        @JvmStatic
        @JvmName("create")
        fun File.asRequestBody(contentType: MediaType? = null): RequestBody {
            return object : RequestBody() {
                override fun contentType() = contentType

                override fun contentLength() = length()

                override fun writeTo(outputStream: OutputStream) {
                    inputStream().use {
                        outputStream.write(it.readBytes())
                    }
                }
            }
        }

        @RequiresApi(Build.VERSION_CODES.KITKAT)
        @JvmStatic
        fun create(contentType: MediaType?, content: String) = content.toRequestBody(contentType)

        @JvmOverloads
        @JvmStatic
        fun create(
            contentType: MediaType?,
            content: ByteArray,
            offset: Int = 0,
            byteCount: Int = content.size
        ) = content.toRequestBody(contentType, offset, byteCount)

        @JvmStatic
        fun create(contentType: MediaType?, file: File) = file.asRequestBody(contentType)
    }
}



