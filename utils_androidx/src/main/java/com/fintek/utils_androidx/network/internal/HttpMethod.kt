package com.fintek.utils_androidx.network.internal

internal object HttpMethod {
    fun invalidatesCache(method: String): Boolean = (method == "POST" ||
            method == "PATCH" ||
            method == "PUT" ||
            method == "DELETE" ||
            method == "MOVE") // WebDAV

    @JvmStatic // Despite being 'internal', this method is called by popular 3rd party SDKs.
    fun requiresRequestBody(method: String): Boolean = (method == "POST" ||
            method == "PUT" ||
            method == "PATCH" ||
            method == "PROPPATCH" || // WebDAV
            method == "REPORT") // CalDAV/CardDAV (defined in WebDAV Versioning)

    @JvmStatic // Despite being 'internal', this method is called by popular 3rd party SDKs.
    fun permitsRequestBody(method: String): Boolean = !(method == "GET" || method == "HEAD")

    fun redirectsWithBody(method: String): Boolean =
        // (WebDAV) redirects should also maintain the request body
        method == "PROPFIND"

    fun redirectsToGet(method: String): Boolean =
        // All requests but PROPFIND should redirect to a GET request.
        method != "PROPFIND"
}
