package com.fintek.utils_androidx

import android.annotation.TargetApi
import android.app.Application
import android.content.Context
import android.os.Build

/**
 * Created by ChaoShen on 2020/11/4
 */
@TargetApi(Build.VERSION_CODES.R)
object FintekUtils {
    /* Utils global context */
    private var context: Context? = null

    /* debug tag */
    internal const val TAG = "FintekUtils"

    /* Whether to enable debug log */
    var isDebugEnable = true

    /**
     * Utils used context
     * @see init
     */
    internal val requiredContext: Context get() = checkNotNull(context) {
        "Please use FintekUtils.init(Context context) first"
    }

    /**
     * init by context
     * @param context please use [android.app.Application] context
     */
    @JvmStatic
    fun init(context: Context) {
        this.context = context
    }
}