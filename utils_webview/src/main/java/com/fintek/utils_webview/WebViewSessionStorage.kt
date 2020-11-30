package com.fintek.utils_webview

import android.os.Bundle
import android.os.Parcel
import android.util.Log
import android.util.LruCache
import android.webkit.WebView

/**
 * Created by ChaoShen on 2020/9/22
 */
interface WebViewSessionStorage {
    fun saveSession(view: WebView?, tabId: String)
    fun restoreSession(webView: WebView?, tabId: String): Boolean
    fun deleteSession(tabId: String)
    fun deleteAllSessions()
}

class WebViewSessionInMemoryStorage : WebViewSessionStorage {
    private val cache = object : LruCache<String, Bundle>(CACHE_SIZE_BYTES) {
        override fun sizeOf(key: String?, value: Bundle): Int = value.sizeOfBytes()
    }

    override fun saveSession(view: WebView?, tabId: String) {
        if (view == null) {
            return
        }

        val webViewBundle = Bundle().also { view.saveState(it) }
        val bundle = Bundle()
        bundle.putBundle(CACHE_KEY_WEBVIEW, webViewBundle)
        bundle.putInt(CACHE_KEY_SCROLL_POSITION, view.scrollY)
        cache.put(tabId, bundle)

        logCacheSize()
    }

    override fun restoreSession(webView: WebView?, tabId: String): Boolean {
        if (webView == null) {
            return false
        }

        val bundle = cache[tabId] ?: return false

        val webViewBundle = bundle.getBundle(CACHE_KEY_WEBVIEW) ?: return false
        webView.restoreState(webViewBundle)
        webView.scrollY = bundle.getInt(CACHE_KEY_SCROLL_POSITION)
        cache.remove(tabId)

        logCacheSize()
        return true
    }

    override fun deleteSession(tabId: String) {
        cache.remove(tabId)
        logCacheSize()
    }

    override fun deleteAllSessions() {
        cache.evictAll()
        logCacheSize()
    }

    private fun logCacheSize() {
        Log.v("WebViewSessionStorage", "Cache size is now ~${cache.size()} bytes out of a max size of ${cache.maxSize()} bytes in $this")
    }

    private fun Bundle.sizeOfBytes(): Int {
        val parcel = Parcel.obtain()
        parcel.writeValue(this)

        val bytes = parcel.marshall()
        parcel.recycle() //recycle this
        return bytes.size
    }

    companion object {
        private const val CACHE_SIZE_BYTES = 5 * 1024 * 1024 // 5M
        private const val CACHE_KEY_WEBVIEW = "cache_webview"
        private const val CACHE_KEY_SCROLL_POSITION = "scroll_position"
    }
}