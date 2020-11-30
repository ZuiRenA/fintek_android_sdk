package com.fintek.utils_webview

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.Message
import android.util.AttributeSet
import android.util.Log
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.webkit.*
import android.widget.EditText
import android.widget.FrameLayout
import androidx.annotation.Keep
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.fintek.utils_webview.webview_interface.CompletionHandler
import com.fintek.utils_webview.webview_interface.OnReturnValue
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.lang.reflect.Method
import java.util.*


class DWebView : WebView, LifecycleObserver {
    private val javaScriptNamespaceInterfaces: MutableMap<String, Any> = HashMap()

    @Deprecated("Cache dir invalid in android 10 or higher")
    private var appCacheDirName: String? = null
    private var callID = 0
    private var customWebChromeClient: WebChromeClient? = null

    @Volatile
    private var alertBoxBlock = true
    private var javascriptCloseWindowListener: JavascriptCloseWindowListener? = null
    private var callInfoList: ArrayList<CallInfo>? = null
    private val innerJavascriptInterface = InnerJavascriptInterface()
    private val mainHandler = Handler(Looper.getMainLooper())
    
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResumeEvent() {
        onResume()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPauseEvent() {
        onPause()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroyEvent() {
        destroy()
    }

    private inner class InnerJavascriptInterface {
        private fun PrintDebugInfo(error: String) {
            Log.d(LOG_TAG, error)
            if (isDebug) {
                evaluateJavascript(String.format("alert('%s')", "DEBUG ERR MSG:\\n" + error.replace("\\'".toRegex(), "\\\\'")))
            }
        }

        @Keep
        @JavascriptInterface
        fun call(methodName: String, argStr: String?): String {
            var methodName = methodName
            var error = "Js bridge  called, but can't find a corresponded " +
                    "JavascriptInterface object , please check your code!"
            val nameStr = parseNamespace(methodName.trim { it <= ' ' })
            methodName = nameStr[1]
            val jsb = javaScriptNamespaceInterfaces[nameStr[0]]
            val ret = JSONObject()
            try {
                ret.put("code", -1)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            if (jsb == null) {
                PrintDebugInfo(error)
                return ret.toString()
            }
            var arg: Any? = null
            var method: Method? = null
            var callback: String? = null
            try {
                val args = JSONObject(argStr)
                if (args.has("_dscbstub")) {
                    callback = args.getString("_dscbstub")
                }
                if (args.has("data")) {
                    arg = args["data"]
                }
            } catch (e: JSONException) {
                error = String.format("The argument of \"%s\" must be a JSON object string!", methodName)
                PrintDebugInfo(error)
                e.printStackTrace()
                return ret.toString()
            }
            val cls: Class<*> = jsb.javaClass
            var asyn = false
            try {
                method = cls.getMethod(methodName,
                        *arrayOf(Any::class.java, CompletionHandler::class.java))
                asyn = true
            } catch (e: Exception) {
                try {
                    method = cls.getMethod(methodName, *arrayOf<Class<*>>(Any::class.java))
                } catch (ex: Exception) {
                }
            }
            if (method == null) {
                error = "Not find method \"$methodName\" implementation! please check if the  signature or namespace of the method is right "
                PrintDebugInfo(error)
                return ret.toString()
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                val annotation = method.getAnnotation(JavascriptInterface::class.java)
                if (annotation == null) {
                    error = "Method " + methodName + " is not invoked, since  " +
                            "it is not declared with JavascriptInterface annotation! "
                    PrintDebugInfo(error)
                    return ret.toString()
                }
            }
            val retData: Any?
            method.isAccessible = true
            try {
                if (asyn) {
                    val cb = callback
                    method.invoke(jsb, arg, object : CompletionHandler<Any?> {
                        override fun complete(retValue: Any?) {
                            complete(retValue, true)
                        }

                        override fun complete() {
                            complete(null, true)
                        }

                        override fun setProgressData(value: Any?) {
                            complete(value, false)
                        }

                        private fun complete(retValue: Any?, complete: Boolean) {
                            try {
                                val ret = JSONObject()
                                ret.put("code", 0)
                                ret.put("data", retValue)
                                //retValue = URLEncoder.encode(ret.toString(), "UTF-8").replaceAll("\\+", "%20");
                                if (cb != null) {
                                    //String script = String.format("%s(JSON.parse(decodeURIComponent(\"%s\")).data);", cb, retValue);
                                    var script = String.format("%s(%s.data);", cb, ret.toString())
                                    if (complete) {
                                        script += "delete window.$cb"
                                    }
                                    //Log.d(LOG_TAG, "complete " + script);
                                    evaluateJavascript(script)
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                    })
                } else {
                    retData = method.invoke(jsb, arg)
                    ret.put("code", 0)
                    ret.put("data", retData)
                    return ret.toString()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                error = String.format("Call failed：The parameter of \"%s\" in Java is invalid.", methodName)
                PrintDebugInfo(error)
                return ret.toString()
            }
            return ret.toString()
        }
    }

    var handlerMap: MutableMap<Int, OnReturnValue<Any?>> = HashMap()

    interface JavascriptCloseWindowListener {
        /**
         * @return If true, close the current activity, otherwise, do nothing.
         */
        fun onClose(): Boolean
    }

    @Deprecated("")
    interface FileChooser {
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        fun openFileChooser(valueCallback: ValueCallback<*>?, acceptType: String?)

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun openFileChooser(valueCallback: ValueCallback<Uri?>?,
                            acceptType: String?, capture: String?)
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(context!!, attrs) {
        init()
    }

    constructor(context: Context?) : super(context!!) {
        init()
    }

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    private fun init() {
        appCacheDirName = context.filesDir.absolutePath + "/webcache"
        val settings = settings
        settings.domStorageEnabled = true
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            settings.mixedContentMode = WebSettings.MIXED_CONTENT_ALWAYS_ALLOW
        }
        settings.allowFileAccess = false
        settings.setAppCacheEnabled(false)
        settings.cacheMode = WebSettings.LOAD_NO_CACHE
        settings.javaScriptEnabled = true
        settings.loadWithOverviewMode = true
        settings.setAppCachePath(appCacheDirName)
        settings.useWideViewPort = true
        // todo 自己添加
//        settings.setDefaultTextEncodingName("utf-8");
//        settings.setAllowUniversalAccessFromFileURLs(true);
//        settings.setDomStorageEnabled(true);
//        settings.setSavePassword(true);
//        settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
        super.setWebChromeClient(mWebChromeClient)
        addInternalJavascriptObject()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN) {
            super.addJavascriptInterface(innerJavascriptInterface, BRIDGE_NAME)
        } else {
            // add dsbridge tag in lower android version
            settings.userAgentString = settings.userAgentString + " _dsbridge"
        }
    }

    private fun parseNamespace(method: String): Array<String> {
        var methodShadow = method
        val pos = methodShadow.lastIndexOf('.')
        var namespace = ""
        if (pos != -1) {
            namespace = methodShadow.substring(0, pos)
            methodShadow = methodShadow.substring(pos + 1)
        }
        return arrayOf(namespace, methodShadow)
    }

    @Keep
    private fun addInternalJavascriptObject() {
        addJavascriptObject(object : Any() {
            @SuppressLint("ObsoleteSdkInt")
            @Keep
            @JavascriptInterface
            @Throws(JSONException::class)
            fun hasNativeMethod(args: Any): Boolean {
                val jsonObject = args as JSONObject
                val methodName = jsonObject.getString("name").trim { it <= ' ' }
                val type = jsonObject.getString("type").trim { it <= ' ' }
                val nameStr = parseNamespace(methodName)
                val jsb = javaScriptNamespaceInterfaces[nameStr[0]]
                if (jsb != null) {
                    val cls: Class<*> = jsb.javaClass
                    var asyn = false
                    var method: Method? = null
                    try {
                        method = cls.getMethod(nameStr[1],
                                *arrayOf(Any::class.java, CompletionHandler::class.java))
                        asyn = true
                    } catch (e: Exception) {
                        try {
                            method = cls.getMethod(nameStr[1], *arrayOf<Class<*>>(Any::class.java))
                        } catch (ex: Exception) {
                        }
                    }
                    if (method != null) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                            val annotation = method.getAnnotation(JavascriptInterface::class.java)
                                    ?: return false
                        }
                        if ("all" == type || asyn && "asyn" == type || !asyn && "syn" == type) {
                            return true
                        }
                    }
                }
                return false
            }

            @Keep
            @JavascriptInterface
            @Throws(JSONException::class)
            fun closePage(`object`: Any?): String? {
                runOnMainThread {
                    if (javascriptCloseWindowListener == null
                            || javascriptCloseWindowListener!!.onClose()) {
                        val context = context
                        if (context is Activity) {
                            context.onBackPressed()
                        }
                    }
                }
                return null
            }

            @Keep
            @JavascriptInterface
            @Throws(JSONException::class)
            fun disableJavascriptDialogBlock(`object`: Any) {
                val jsonObject = `object` as JSONObject
                alertBoxBlock = !jsonObject.getBoolean("disable")
            }

            @Keep
            @JavascriptInterface
            fun dsinit(jsonObject: Any?) {
                dispatchStartupQueue()
            }

            @Keep
            @JavascriptInterface
            fun returnValue(obj: Any) {
                runOnMainThread {
                    val jsonObject = obj as JSONObject
                    var data: Any? = null
                    try {
                        val id = jsonObject.getInt("id")
                        val isCompleted = jsonObject.getBoolean("complete")
                        val handler = handlerMap[id]
                        if (jsonObject.has("data")) {
                            data = jsonObject["data"]
                        }
                        if (handler != null) {
                            handler.onValue(data)
                            if (isCompleted) {
                                handlerMap.remove(id)
                            }
                        }
                    } catch (e: JSONException) {
                        e.printStackTrace()
                    }
                }
            }
        }, "_dsb")
    }

    private fun _evaluateJavascript(script: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            super@DWebView.evaluateJavascript(script, null)
        } else {
            super.loadUrl("javascript:$script")
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param script
     */
    fun evaluateJavascript(script: String) {
        runOnMainThread { _evaluateJavascript(script) }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     */
    override fun loadUrl(url: String) {
        runOnMainThread {
            if (url.startsWith("javascript:")) {
                super@DWebView.loadUrl(url)
            } else {
                callInfoList = ArrayList()
                super@DWebView.loadUrl(url)
            }
        }
    }

    /**
     * This method can be called in any thread, and if it is not called in the main thread,
     * it will be automatically distributed to the main thread.
     *
     * @param url
     * @param additionalHttpHeaders
     */
    override fun loadUrl(url: String, additionalHttpHeaders: MutableMap<String, String>) {
        runOnMainThread {
            if (url.startsWith("javascript:")) {
                super@DWebView.loadUrl(url, additionalHttpHeaders)
            } else {
                callInfoList = ArrayList()
                super@DWebView.loadUrl(url, additionalHttpHeaders)
            }
        }
    }

    override fun reload() {
        runOnMainThread {
            callInfoList = ArrayList()
            super@DWebView.reload()
        }
    }

    /**
     * set a listener for javascript closing the current activity.
     */
    fun setJavascriptCloseWindowListener(listener: JavascriptCloseWindowListener?) {
        javascriptCloseWindowListener = listener
    }

    private class CallInfo(handlerName: String, id: Int, args: Array<Any?>?) {
        private val data: String
        val callbackId: Int
        private val method: String
        override fun toString(): String {
            val jo = JSONObject()
            try {
                jo.put("method", method)
                jo.put("callbackId", callbackId)
                jo.put("data", data)
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            return jo.toString()
        }

        init {
            var args = args
            if (args == null) {
                args = arrayOfNulls(0)
            }
            data = JSONArray(Arrays.asList(*args)).toString()
            callbackId = id
            method = handlerName
        }
    }

    @Synchronized
    private fun dispatchStartupQueue() {
        if (callInfoList != null) {
            for (info in callInfoList!!) {
                dispatchJavascriptCall(info)
            }
            callInfoList = null
        }
    }

    private fun dispatchJavascriptCall(info: CallInfo) {
        evaluateJavascript(String.format("window._handleMessageFromNative(%s)", info.toString()))
    }

    @Synchronized
    fun <T> callHandler(method: String, args: Array<Any?>?, handler: OnReturnValue<T>?) {
        val callInfo = CallInfo(method, ++callID, args)
        if (handler != null) {
            handlerMap[callInfo.callbackId] = handler as OnReturnValue<Any?>
        }
        if (callInfoList != null) {
            callInfoList!!.add(callInfo)
        } else {
            dispatchJavascriptCall(callInfo)
        }
    }

    fun callHandler(method: String, args: Array<Any?>?) {
        callHandler<Any>(method, args, null)
    }

    fun <T> callHandler(method: String, handler: OnReturnValue<T>?) {
        callHandler(method, null, handler)
    }

    /**
     * Test whether the handler exist in javascript
     *
     * @param handlerName
     * @param existCallback
     */
    fun hasJavascriptMethod(handlerName: String?, existCallback: OnReturnValue<Boolean>?) {
        callHandler("_hasJavascriptMethod", arrayOf(handlerName), existCallback)
    }

    /**
     * Add a java object which implemented the javascript interfaces to dsBridge with namespace.
     * Remove the object using [removeJavascriptObject(String)][.removeJavascriptObject]
     *
     * @param object
     * @param namespace if empty, the object have no namespace.
     */
    fun addJavascriptObject(`object`: Any?, namespace: String?) {
        val namespaceShadow = namespace ?: ""
        if (`object` != null) {
            javaScriptNamespaceInterfaces[namespaceShadow] = `object`
        }
    }

    /**
     * remove the javascript object with supplied namespace.
     *
     * @param namespace
     */
    fun removeJavascriptObject(namespace: String?) {
        val namespaceShadow = namespace ?: ""
        javaScriptNamespaceInterfaces.remove(namespaceShadow)
    }

    fun disableJavascriptDialogBlock(disable: Boolean) {
        alertBoxBlock = !disable
    }

    override fun setWebChromeClient(client: WebChromeClient?) {
        customWebChromeClient = client
    }

    private val mWebChromeClient: WebChromeClient = object : WebChromeClient() {
        override fun onProgressChanged(view: WebView, newProgress: Int) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onProgressChanged(view, newProgress)
            } else {
                super.onProgressChanged(view, newProgress)
            }
        }

        override fun onReceivedTitle(view: WebView, title: String) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onReceivedTitle(view, title)
            } else {
                super.onReceivedTitle(view, title)
            }
        }

        override fun onReceivedIcon(view: WebView, icon: Bitmap) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onReceivedIcon(view, icon)
            } else {
                super.onReceivedIcon(view, icon)
            }
        }

        override fun onReceivedTouchIconUrl(view: WebView, url: String, precomposed: Boolean) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onReceivedTouchIconUrl(view, url, precomposed)
            } else {
                super.onReceivedTouchIconUrl(view, url, precomposed)
            }
        }

        override fun onShowCustomView(view: View, callback: CustomViewCallback) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onShowCustomView(view, callback)
            } else {
                super.onShowCustomView(view, callback)
            }
        }

        @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
        override fun onShowCustomView(view: View, requestedOrientation: Int,
                                      callback: CustomViewCallback) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onShowCustomView(view, requestedOrientation, callback)
            } else {
                super.onShowCustomView(view, requestedOrientation, callback)
            }
        }

        override fun onHideCustomView() {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onHideCustomView()
            } else {
                super.onHideCustomView()
            }
        }

        override fun onCreateWindow(view: WebView, isDialog: Boolean,
                                    isUserGesture: Boolean, resultMsg: Message): Boolean {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.onCreateWindow(view, isDialog,
                        isUserGesture, resultMsg)
            } else super.onCreateWindow(view, isDialog, isUserGesture, resultMsg)
        }

        override fun onRequestFocus(view: WebView) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onRequestFocus(view)
            } else {
                super.onRequestFocus(view)
            }
        }

        override fun onCloseWindow(window: WebView) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onCloseWindow(window)
            } else {
                super.onCloseWindow(window)
            }
        }

        override fun onJsAlert(view: WebView, url: String, message: String, result: JsResult): Boolean {
            if (!alertBoxBlock) {
                result.confirm()
            }
            if (customWebChromeClient != null) {
                if (customWebChromeClient!!.onJsAlert(view, url, message, result)) {
                    return true
                }
            }
            val alertDialog: Dialog = AlertDialog.Builder(context).setMessage(message).setCancelable(false).setPositiveButton(
                    android.R.string.ok) { dialog, _ ->
                dialog.dismiss()
                if (alertBoxBlock) {
                    result.confirm()
                }
            }
                    .create()
            alertDialog.show()
            return true
        }

        override fun onJsConfirm(view: WebView, url: String, message: String,
                                 result: JsResult): Boolean {
            if (!alertBoxBlock) {
                result.confirm()
            }
            return if (customWebChromeClient != null && customWebChromeClient!!.onJsConfirm(view, url, message, result)) {
                true
            } else {
                val listener = DialogInterface.OnClickListener { dialog, which ->
                    if (alertBoxBlock) {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            result.confirm()
                        } else {
                            result.cancel()
                        }
                    }
                }
                AlertDialog.Builder(context)
                        .setMessage(message)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener).show()
                true
            }
        }

        override fun onJsPrompt(view: WebView, url: String, message: String,
                                defaultValue: String, result: JsPromptResult): Boolean {
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.JELLY_BEAN) {
                val prefix = "_dsbridge="
                if (message.startsWith(prefix)) {
                    result.confirm(innerJavascriptInterface.call(message.substring(prefix.length), defaultValue))
                    return true
                }
            }
            if (!alertBoxBlock) {
                result.confirm()
            }
            return if (customWebChromeClient != null && customWebChromeClient!!.onJsPrompt(view, url, message, defaultValue, result)) {
                true
            } else {
                val editText = EditText(context)
                editText.setText(defaultValue)
                editText.setSelection(defaultValue.length)
                val dpi = context.resources.displayMetrics.density
                val listener = DialogInterface.OnClickListener { _, which ->
                    if (alertBoxBlock) {
                        if (which == Dialog.BUTTON_POSITIVE) {
                            result.confirm(editText.text.toString())
                        } else {
                            result.cancel()
                        }
                    }
                }
                AlertDialog.Builder(context)
                        .setTitle(message)
                        .setView(editText)
                        .setCancelable(false)
                        .setPositiveButton(android.R.string.ok, listener)
                        .setNegativeButton(android.R.string.cancel, listener)
                        .show()
                val layoutParams = FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT)
                val t = (dpi * 16).toInt()
                layoutParams.setMargins(t, 0, t, 0)
                layoutParams.gravity = Gravity.CENTER_HORIZONTAL
                editText.layoutParams = layoutParams
                val padding = (15 * dpi).toInt()
                editText.setPadding(padding - (5 * dpi).toInt(), padding, padding, padding)
                true
            }
        }

        override fun onJsBeforeUnload(view: WebView, url: String, message: String, result: JsResult): Boolean {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.onJsBeforeUnload(view, url, message, result)
            } else super.onJsBeforeUnload(view, url, message, result)
        }

        override fun onExceededDatabaseQuota(url: String, databaseIdentifier: String, quota: Long,
                                             estimatedDatabaseSize: Long,
                                             totalQuota: Long,
                                             quotaUpdater: WebStorage.QuotaUpdater) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater)
            } else {
                super.onExceededDatabaseQuota(url, databaseIdentifier, quota,
                        estimatedDatabaseSize, totalQuota, quotaUpdater)
            }
        }

        override fun onReachedMaxAppCacheSize(requiredStorage: Long, quota: Long, quotaUpdater: WebStorage.QuotaUpdater) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
            }
            super.onReachedMaxAppCacheSize(requiredStorage, quota, quotaUpdater)
        }

        override fun onGeolocationPermissionsShowPrompt(origin: String, callback: GeolocationPermissions.Callback) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onGeolocationPermissionsShowPrompt(origin, callback)
            } else {
                super.onGeolocationPermissionsShowPrompt(origin, callback)
            }
        }

        override fun onGeolocationPermissionsHidePrompt() {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onGeolocationPermissionsHidePrompt()
            } else {
                super.onGeolocationPermissionsHidePrompt()
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequest(request: PermissionRequest) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onPermissionRequest(request)
            } else {
                super.onPermissionRequest(request)
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onPermissionRequestCanceled(request: PermissionRequest) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onPermissionRequestCanceled(request)
            } else {
                super.onPermissionRequestCanceled(request)
            }
        }

        override fun onJsTimeout(): Boolean {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.onJsTimeout()
            } else super.onJsTimeout()
        }

        override fun onConsoleMessage(message: String, lineNumber: Int, sourceID: String) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.onConsoleMessage(message, lineNumber, sourceID)
            } else {
                super.onConsoleMessage(message, lineNumber, sourceID)
            }
        }

        override fun onConsoleMessage(consoleMessage: ConsoleMessage): Boolean {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.onConsoleMessage(consoleMessage)
            } else super.onConsoleMessage(consoleMessage)
        }

        override fun getDefaultVideoPoster(): Bitmap {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.defaultVideoPoster!!
            } else super.getDefaultVideoPoster()!!
        }

        override fun getVideoLoadingProgressView(): View {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.videoLoadingProgressView!!
            } else super.getVideoLoadingProgressView()!!
        }

        override fun getVisitedHistory(callback: ValueCallback<Array<String>>) {
            if (customWebChromeClient != null) {
                customWebChromeClient!!.getVisitedHistory(callback)
            } else {
                super.getVisitedHistory(callback)
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        override fun onShowFileChooser(webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                                       fileChooserParams: FileChooserParams): Boolean {
            return if (customWebChromeClient != null) {
                customWebChromeClient!!.onShowFileChooser(webView, filePathCallback, fileChooserParams)
            } else super.onShowFileChooser(webView, filePathCallback, fileChooserParams)
        }

        @Keep
        @TargetApi(Build.VERSION_CODES.HONEYCOMB)
        fun openFileChooser(valueCallback: ValueCallback<*>?, acceptType: String?) {
            if (customWebChromeClient is FileChooser) {
                (customWebChromeClient as FileChooser).openFileChooser(valueCallback, acceptType)
            }
        }

        @Keep
        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        fun openFileChooser(valueCallback: ValueCallback<Uri?>?,
                            acceptType: String?, capture: String?) {
            if (customWebChromeClient is FileChooser) {
                (customWebChromeClient as FileChooser).openFileChooser(valueCallback, acceptType, capture)
            }
        }
    }

    override fun clearCache(includeDiskFiles: Boolean) {
        super.clearCache(includeDiskFiles)
        CookieManager.getInstance().removeAllCookie()
        val context = context
        try {
            context.deleteDatabase("webview.db")
            context.deleteDatabase("webviewCache.db")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        val appCacheDir = File(appCacheDirName)
        val webviewCacheDir = File(context.cacheDir
                .absolutePath + "/webviewCache")
        if (webviewCacheDir.exists()) {
            deleteFile(webviewCacheDir)
        }
        if (appCacheDir.exists()) {
            deleteFile(appCacheDir)
        }
    }

    fun deleteFile(file: File) {
        if (file.exists()) {
            if (file.isFile) {
                file.delete()
            } else if (file.isDirectory) {
                val files = file.listFiles()
                for (i in files.indices) {
                    deleteFile(files[i])
                }
            }
            file.delete()
        } else {
            Log.e("Webview", "delete file no exists " + file.absolutePath)
        }
    }

    private fun runOnMainThread(runnable: Runnable) {
        if (Looper.getMainLooper() == Looper.myLooper()) {
            runnable.run()
            return
        }
        mainHandler.post(runnable)
    }

    companion object {
        private const val BRIDGE_NAME = "_dsbridge"
        private const val LOG_TAG = "dsBridge"
        private var isDebug = false

        /**
         * Set debug mode. if in debug mode, some errors will be prompted by a dialog
         * and the exception caused by the native handlers will not be captured.
         *
         * @param enabled
         */
        fun setWebContentsDebuggingEnabled(enabled: Boolean) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                WebView.setWebContentsDebuggingEnabled(enabled)
            }
            isDebug = enabled
        }
    }
}