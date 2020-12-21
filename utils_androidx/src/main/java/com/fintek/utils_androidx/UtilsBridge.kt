package com.fintek.utils_androidx

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.app.AppUtils
import com.fintek.utils_androidx.file.FileIOUtils
import com.fintek.utils_androidx.file.FileUtils
import com.fintek.utils_androidx.json.JsonUtils
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.process.ProcessUtils
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_androidx.thread.Task
import com.fintek.utils_androidx.thread.ThreadUtils
import com.fintek.utils_androidx.throwable.ThrowableUtils
import com.fintek.utils_androidx.upload.UploadUtils
import java.io.File
import java.nio.charset.Charset

/**
 * Created by ChaoShen on 2020/11/10
 */
internal object UtilsBridge {


    ///////////////////////////////////////////////////////////////////////////
    // SDCard
    ///////////////////////////////////////////////////////////////////////////
    fun isSDCardEnableByEnvironment(): Boolean {
        return SDCardUtils.isSDCardEnableByEnvironment()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Process
    ///////////////////////////////////////////////////////////////////////////
    fun getCurrentProcessName(): String? {
        return ProcessUtils.getCurrentProcessName()
    }

    ///////////////////////////////////////////////////////////////////////////
    // Thread
    ///////////////////////////////////////////////////////////////////////////

    fun <T> executeBySingle(task: Task<T>, priority: Int = Thread.NORM_PRIORITY): Task<T> {
        ThreadUtils.executeBySingle(task, priority)
        return task
    }

    fun <T> executeByCached(task: Task<T>, priority: Int = Thread.NORM_PRIORITY): Task<T> {
        ThreadUtils.executeByCached(task, priority)
        return task
    }

    fun <T> executeByIO(task: Task<T>, priority: Int = Thread.NORM_PRIORITY): Task<T> {
        ThreadUtils.executeByIo(task, priority)
        return task
    }

    fun <T> executeByCPU(task: Task<T>, priority: Int = Thread.NORM_PRIORITY): Task<T> {
        ThreadUtils.executeByCpu(task, priority)
        return task
    }

    fun <T> executeByCustom(task: Task<T>, size: Int ,priority: Int = Thread.NORM_PRIORITY): Task<T> {
        ThreadUtils.executeByCustom(ThreadUtils.getFixedPool(size, priority), task)
        return task
    }

    fun runOnUiThread(runnable: Runnable, delayMillis: Long = 0L) {
        if (delayMillis == 0L) {
            ThreadUtils.runOnUiThread(runnable)
        } else {
            ThreadUtils.runOnUiThreadDelay(runnable, delayMillis)
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Json
    ///////////////////////////////////////////////////////////////////////////
    fun formatJson(json: String): String {
        return JsonUtils.formatJson(json)
    }

    ///////////////////////////////////////////////////////////////////////////
    // Throwable
    ///////////////////////////////////////////////////////////////////////////
    fun getFullStackTrace(throwable: Throwable?): String {
        return ThrowableUtils.getFullStackTrace(throwable)
    }


    ///////////////////////////////////////////////////////////////////////////
    // File
    ///////////////////////////////////////////////////////////////////////////
    fun createOrExistsDir(file: File?): Boolean {
        return FileUtils.createOrExistsDir(file)
    }

    fun getFileByPath(filePath: String?): File? {
        return FileUtils.getFileByPath(filePath)
    }

    fun writeFileFromString(
        filePath: String,
        content: String,
        append: Boolean,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        return FileIOUtils.writeFileFromString(filePath, content, append, charset)
    }

    fun createOrExistsFile(file: File?): Boolean {
        return FileUtils.createOrExistsFile(file)
    }

    ///////////////////////////////////////////////////////////////////////////
    // App
    ///////////////////////////////////////////////////////////////////////////
    fun getAppVersionName(): String = AppUtils.getAppVersionName()

    fun getAppVersionCode(): Int = AppUtils.getAppVersionCode()


    ///////////////////////////////////////////////////////////////////////////
    // Log
    ///////////////////////////////////////////////////////////////////////////
    fun v(vararg contents: Any?) {
        if (FintekUtils.isDebugEnable) {
            TimberUtil.vTag(tag = FintekUtils.TAG, contents = contents)
        }
    }

    fun d(vararg contents: Any?) {
        if (FintekUtils.isDebugEnable) {
            TimberUtil.dTag(tag = FintekUtils.TAG, contents = contents)
        }
    }

    fun i(vararg contents: Any?) {
        if (FintekUtils.isDebugEnable) {
            TimberUtil.iTag(tag = FintekUtils.TAG, contents = contents)
        }
    }

    fun w(vararg contents: Any?) {
        if (FintekUtils.isDebugEnable) {
            TimberUtil.wTag(tag = FintekUtils.TAG, contents = contents)
        }
    }

    fun e(vararg contents: Any?) {
        if (FintekUtils.isDebugEnable) {
            TimberUtil.eTag(tag = FintekUtils.TAG, contents = contents)
        }
    }

    fun a(vararg contents: Any?) {
        if (FintekUtils.isDebugEnable) {
            TimberUtil.aTag(tag = FintekUtils.TAG, contents = contents)
        }
    }


    ///////////////////////////////////////////////////////////////////////////
    // Check
    ///////////////////////////////////////////////////////////////////////////
    fun checkOffsetAndCount(arrayLength: Long, offset: Long, count: Long) {
        if (offset or count < 0L || offset > arrayLength || arrayLength - offset < count) {
            throw ArrayIndexOutOfBoundsException()
        }
    }

    ///////////////////////////////////////////////////////////////////////////
    // Upload
    ///////////////////////////////////////////////////////////////////////////
    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    fun monthlyUpload() {
        UploadUtils.monthlyUpload()
    }
}