package com.fintek.utils_webview.webview_interface
/**
 * Created by du on 16/12/31.
 */
interface CompletionHandler<T> {
    fun complete(retValue: T)
    fun complete()
    fun setProgressData(value: T)
}