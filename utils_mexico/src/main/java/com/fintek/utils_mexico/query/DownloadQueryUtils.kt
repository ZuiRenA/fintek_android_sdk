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
    private val downloadManager = FintekMexicoUtils.requiredApplication.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

    /**
     * path /mnt/sdcard/Download
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