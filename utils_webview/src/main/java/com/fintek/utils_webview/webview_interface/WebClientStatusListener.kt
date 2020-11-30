package com.fintek.utils_webview.webview_interface

import android.webkit.WebBackForwardList
import android.webkit.WebView

/**
 * Created by ChaoShen on 2020/9/22
 */
interface WebClientStatusListener {
    /**
     * WebView BackForwardList change
     */
    fun onNavigationStateChange(webNavigationState: WebBackForwardList)

    /**
     * Need used in onSaveInstanceState
     */
    fun saveWebViewState(view: WebView?)

    /**
     * Need used in onViewStateRestored
     */
    fun restoreWebViewState(view: WebView?)

    /**
     * To confirm the showing browser
     * used in multiple tabs
     */
    fun determineShowBrowser()
}