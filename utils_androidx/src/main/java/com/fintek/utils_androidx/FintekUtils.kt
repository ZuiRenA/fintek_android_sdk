package com.fintek.utils_androidx

import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import com.fintek.utils_androidx.thread.SimpleTask

/**
 * Created by ChaoShen on 2020/11/4
 */
@SuppressLint("StaticFieldLeak")
@TargetApi(Build.VERSION_CODES.R)
object FintekUtils {
    /* Utils global context */
    private var context: Context? = null

    /* debug tag */
    internal const val TAG = "FintekUtils"

    /* Whether to enable debug log */
    var isDebugEnable = true

    var isThrowable = false

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
    fun init(context: Context) = apply {
        this.context = context
    }

    interface Consumer<T> {
        fun accept(t: T)
    }

    ///////////////////////////////////////////////////////////////////////////
    // interface
    ///////////////////////////////////////////////////////////////////////////
    abstract class Task<Result> @JvmOverloads constructor (
        private val consumer: Consumer<Result>? = null
    ) : SimpleTask<Result>() {

        override fun onSuccess(result: Result) {
            consumer?.accept(result)
        }
    }
}