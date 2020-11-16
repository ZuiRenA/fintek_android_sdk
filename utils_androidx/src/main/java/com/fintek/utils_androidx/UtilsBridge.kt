package com.fintek.utils_androidx

import android.content.ContentResolver
import android.database.Cursor
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.app.AppUtils
import com.fintek.utils_androidx.file.FileIOUtils
import com.fintek.utils_androidx.file.FileUtils
import com.fintek.utils_androidx.json.JsonUtils
import com.fintek.utils_androidx.storage.SDCardUtils
import com.fintek.utils_androidx.throwable.ThrowableUtils
import java.io.File

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

    fun writeFileFromString(filePath: String, content: String, append: Boolean): Boolean {
        return FileIOUtils.writeFileFromString(filePath, content, append)
    }

    fun createOrExistsFile(file: File?): Boolean {
        return FileUtils.createOrExistsFile(file)
    }

    ///////////////////////////////////////////////////////////////////////////
    // App
    ///////////////////////////////////////////////////////////////////////////
    fun getAppVersionName(): String = AppUtils.getAppVersionName()

    fun getAppVersionCode(): Int = AppUtils.getAppVersionCode()
}