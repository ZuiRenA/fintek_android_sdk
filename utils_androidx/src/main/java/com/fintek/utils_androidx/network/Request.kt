package com.fintek.utils_androidx.network

import com.fintek.utils_androidx.network.internal.EMPTY_REQUEST
import com.fintek.utils_androidx.network.internal.HttpMethod
import com.fintek.utils_androidx.network.internal.toImmutableMap
import java.net.URL

/**
 * Created by ChaoShen on 2020/12/2
 */
class Request internal constructor(
    @get:JvmName("url") val url: String,
    @get:JvmName("method") val method: String,
    @get:JvmName("headers") val headers: Headers,
    @get:JvmName("body") val body: RequestBody?,
    internal val tags: Map<Class<*>, Any>
) {

    fun header(name: String): String? = headers[name]

    fun headers(name: String): List<String> = headers.values(name)

    /**
     * Returns the tag attached with `Object.class` as a key, or null if no tag is attached with
     * that key.
     *
     * Prior to OkHttp 3.11, this method never returned null if no tag was attached. Instead it
     * returned either this request, or the request upon which this request was derived with
     * [newBuilder].
     */
    fun tag(): Any? = tag(Any::class.java)

    /**
     * Returns the tag attached with [type] as a key, or null if no tag is attached with that
     * key.
     */
    fun <T> tag(type: Class<out T>): T? = type.cast(tags[type])


    override fun toString() = buildString {
        append("Request{method=")
        append(method)
        append(", url=")
        append(url)
        if (headers.size != 0) {
            append(", headers=[")
            headers.forEachIndexed { index, (name, value) ->
                if (index > 0) {
                    append(", ")
                }
                append(name)
                append(':')
                append(value)
            }
            append(']')
        }
        if (tags.isNotEmpty()) {
            append(", tags=")
            append(tags)
        }
        append('}')
    }

    open class Builder {
        internal var url: String? = null
        internal var method: String
        internal var headers: Headers.Builder
        internal var body: RequestBody? = null

        /** A mutable map of tags, or an immutable empty map if we don't have any. */
        internal var tags: MutableMap<Class<*>, Any> = mutableMapOf()

        constructor() {
            this.method = "GET"
            this.headers = Headers.Builder()
        }

        internal constructor(request: Request) {
            this.url = request.url
            this.method = request.method
            this.body = request.body
            this.tags = if (request.tags.isEmpty()) {
                mutableMapOf()
            } else {
                request.tags.toMutableMap()
            }
            this.headers = request.headers.newBuilder()
        }

        open fun url(url: String): Builder = apply {
            // Silently replace web socket URLs with HTTP URLs.
            val finalUrl: String = when {
                url.startsWith("ws:", ignoreCase = true) -> {
                    "http:${url.substring(3)}"
                }
                url.startsWith("wss:", ignoreCase = true) -> {
                    "https:${url.substring(4)}"
                }
                else -> url
            }

            this.url = finalUrl
        }

        /**
         * Sets the URL target of this request.
         *
         * @throws IllegalArgumentException if the scheme of [url] is not `http` or `https`.
         */
        open fun url(url: URL) = url(url.toString())

        /**
         * Sets the header named [name] to [value]. If this request already has any headers
         * with that name, they are all replaced.
         */
        open fun header(name: String, value: String) = apply {
            headers[name] = value
        }

        /**
         * Adds a header with [name] and [value]. Prefer this method for multiply-valued
         * headers like "Cookie".
         *
         * Note that for some headers including `Content-Length` and `Content-Encoding`,
         * OkHttp may replace [value] with a header derived from the request body.
         */
        open fun addHeader(name: String, value: String) = apply {
            headers.add(name, value)
        }

        /** Removes all headers named [name] on this builder. */
        open fun removeHeader(name: String) = apply {
            headers.removeAll(name)
        }

        /** Removes all headers on this builder and adds [headers]. */
        open fun headers(headers: Headers) = apply {
            this.headers = headers.newBuilder()
        }

        open fun get() = method("GET", null)

        open fun head() = method("HEAD", null)

        open fun post(body: RequestBody) = method("POST", body)

        @JvmOverloads
        open fun delete(body: RequestBody? = EMPTY_REQUEST) = method("DELETE", body)

        open fun put(body: RequestBody) = method("PUT", body)

        open fun patch(body: RequestBody) = method("PATCH", body)

        open fun method(method: String, body: RequestBody?): Builder = apply {
            require(method.isNotEmpty()) {
                "method.isEmpty() == true"
            }
            if (body == null) {
                require(!HttpMethod.requiresRequestBody(method)) {
                    "method $method must have a request body."
                }
            } else {
                require(HttpMethod.permitsRequestBody(method)) {
                    "method $method must not have a request body."
                }
            }
            this.method = method
            this.body = body
        }

        /** Attaches [tag] to the request using `Object.class` as a key. */
        open fun tag(tag: Any?): Builder = tag(Any::class.java, tag)

        /**
         * Attaches [tag] to the request using [type] as a key. Tags can be read from a
         * request using [Request.tag]. Use null to remove any existing tag assigned for [type].
         *
         * Use this API to attach timing, debugging, or other application data to a request so that
         * you may read it in interceptors, event listeners, or callbacks.
         */
        open fun <T> tag(type: Class<in T>, tag: T?) = apply {
            if (tag == null) {
                tags.remove(type)
            } else {
                if (tags.isEmpty()) {
                    tags = mutableMapOf()
                }
                tags[type] = type.cast(tag)!! // Force-unwrap due to lack of contracts on Class#cast()
            }
        }

        open fun build(): Request {
            return Request(
                checkNotNull(url) { "url == null" },
                method,
                headers.build(),
                body,
                tags.toImmutableMap()
            )
        }
    }
}