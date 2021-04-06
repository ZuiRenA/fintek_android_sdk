package com.fintek.ocr_camera.camera

import android.annotation.SuppressLint
import android.hardware.Camera
import android.os.AsyncTask
import android.os.Build
import android.os.Handler
import android.os.Message
import android.util.Log
import java.lang.ref.WeakReference
import java.util.concurrent.RejectedExecutionException

/**
 * Created by ChaoShen on 2020/9/15
 */
internal class AutoFocusManager(private val camera: Camera) : Camera.AutoFocusCallback {

    private companion object {
        const val TAG = "AutoFocusManager"
        const val AUTO_FOCUS_INTERVAL_MS = 2000L
        val FOCUS_MODES_CALLING_AF: MutableCollection<String> = ArrayList(2)

        class AutoFocusTask(
            block: () -> Unit
        ) : AsyncTask<Any?, Any?, Any?>() {
            private val blockWeak = WeakReference(block)

            override fun doInBackground(vararg p0: Any?): Any? {
                try {
                    Thread.sleep(AUTO_FOCUS_INTERVAL_MS)
                } catch (e: InterruptedException) {
                    // ignore this
                }
                blockWeak.get()?.invoke()
                return null
            }
        }
    }

    private val useAutoFocus: Boolean
    private var stopped: Boolean = false
    private var focusing: Boolean = false
    private var outStandAsyncTask: AsyncTask<*, *, *>? = null

    init {
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_AUTO)
        FOCUS_MODES_CALLING_AF.add(Camera.Parameters.FOCUS_MODE_MACRO)
        val currentFocusMode = camera.parameters.focusMode
        useAutoFocus = FOCUS_MODES_CALLING_AF.contains(currentFocusMode)
        start()
    }

    @Synchronized
    override fun onAutoFocus(success: Boolean, camera: Camera?) {
        focusing = true
        autoFocusAgainLater()
    }

    @Synchronized
    fun start() {
        if (!useAutoFocus) {
            return
        }

        outStandAsyncTask = null
        if (!stopped && !focusing) {
            try {
                camera.autoFocus(this)
                Log.w(TAG, "自动对焦")
                focusing = true
            } catch (e: RuntimeException) {
                Log.w(TAG, "Unexpected exception while focusing", e)
                autoFocusAgainLater()
            }
        }
    }

    @Synchronized
    fun stop() {
        stopped = true
        if (useAutoFocus) {
            cancelOutStandingAsyncTask()
            try {
                camera.cancelAutoFocus()
            } catch (e: RuntimeException) {
                Log.w(TAG, "Unexpected exception while cancelling focusing", e)
            }
        }
    }

    @SuppressLint("ObsoleteSdkInt")
    @Synchronized
    private fun autoFocusAgainLater() {
        if (!stopped && outStandAsyncTask == null) {
            val newAsyncTask = AutoFocusTask { start() }
            try {
                if (Build.VERSION.SDK_INT >= 11) {
                    newAsyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR)
                } else {
                    newAsyncTask.execute()
                }
                outStandAsyncTask = newAsyncTask
            } catch (ree: RejectedExecutionException) {
                Log.w(TAG, "Could not request auto focus", ree)
            }
        }
    }

    @Synchronized
    private fun cancelOutStandingAsyncTask() {
        outStandAsyncTask?.let {
            if (it.status != AsyncTask.Status.FINISHED) {
                it.cancel(true)
            }
            outStandAsyncTask = null
        }
    }
}