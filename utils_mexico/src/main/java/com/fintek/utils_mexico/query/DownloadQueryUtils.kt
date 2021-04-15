package com.fintek.utils_mexico.query

import android.app.DownloadManager
import android.content.Context
import android.os.Environment
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.catchOrZero
import java.io.File

/**
 * Created by ChaoShen on 2021/4/15
 */
object DownloadQueryUtils {
    /**
     * path /mnt/sdcard/Download
     * Get download file in this path, will not deep get file count
     */
    @JvmStatic
    fun getDownloadFileCount(): Int = catchOrZero {
        val downloadFile = File(Environment.getExternalStorageDirectory().path, "Download")
        if (!downloadFile.exists() || !downloadFile.isDirectory) {
            return@catchOrZero 0
        }

        return@catchOrZero downloadFile.listFiles()?.size ?: 0
    }
}