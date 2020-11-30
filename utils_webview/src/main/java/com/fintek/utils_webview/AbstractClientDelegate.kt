package com.fintek.utils_webview

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebBackForwardList
import android.webkit.WebView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LifecycleObserver
import com.fintek.utils_webview.webview_interface.WebClientOverride
import com.fintek.utils_webview.webview_interface.WebClientStatusListener
import java.lang.ref.WeakReference

/**
 * Created by ChaoShen on 2020/9/22
 */
abstract class AbstractClientDelegate(
    context: Context,
    statusListener: WebClientStatusListener
) : WebClientOverride, LifecycleObserver, WebClientStatusListener by statusListener {

    private val _context: WeakReference<Context> = WeakReference(context)
    private var firstStartTimeStamp: Long = 0

    val context: Context? get() = _context.get()
    val requiredContext: Context get() = _context.get()!!

    fun release() {
        _context.clear()
    }

    /**
     * API-agnostic implementation of deciding whether to override url or not
     * now nothing to do
     */
    override fun shouldOverride(view: WebView, uri: Uri): Boolean = true

    override fun receivedSslError(view: WebView?, handler: SslErrorHandler?, error: SslError?) {
        val builder = AlertDialog.Builder(requiredContext).apply {
            setMessage("Ssl authentication failed, whether to continue access ?")
            setPositiveButton("Konfirmasi") { _, _ -> handler?.proceed() }
            setNegativeButton("Batalkan") { _, _ -> handler?.cancel() }
        }
        builder.create().show()
    }

    override fun pageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
        try {
            val navigationList: WebBackForwardList = view?.safeCopyBackForwardList() ?: return
            onNavigationStateChange(navigationList)
            firstStartTimeStamp = System.currentTimeMillis()
        } catch (e: Throwable) {

        }
    }

    override fun pageFinished(view: WebView?, url: String?) {
        try {
            val navigationList: WebBackForwardList = view?.safeCopyBackForwardList() ?: return
            onNavigationStateChange(navigationList)
            val loadDuration = System.currentTimeMillis() - firstStartTimeStamp
            durationUpload(url, loadDuration)
        } catch (e: Throwable) {

        }
    }

    private fun WebView.safeCopyBackForwardList(): WebBackForwardList? = try {
        copyBackForwardList()
    } catch (e: NullPointerException) {
        null
    }

    private fun durationUpload(url: String?, duration: Long) {
        val text = "WebView duration[url: $url, duration: $duration ms]"
    }
}