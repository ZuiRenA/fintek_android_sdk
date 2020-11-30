package com.fintek.utils_webview.webview_interface

import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView

/**
 * Created by ChaoShen on 2020/9/22
 */
interface WebClientOverride {
    fun pageStarted(view: WebView?, url: String?, favicon: Bitmap?)
    fun pageFinished(view: WebView?, url: String?)
    fun shouldOverride(view: WebView, uri: Uri): Boolean
    fun receivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?)
    fun receivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?)
    fun receivedError(view: WebView?, errorCode: Int, descriptor: String?, failingUrl: String?)
}