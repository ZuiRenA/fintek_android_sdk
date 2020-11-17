package com.fintek.utils_androidx.process

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.fintek.utils_androidx.FintekUtils
import java.io.BufferedReader
import java.io.File
import java.io.FileReader

/**
 * Created by ChaoShen on 2020/11/10
 */
object ProcessUtils {
    /**
     * Return the name of current process.
     *
     * @return the name of current process
     */
    @JvmStatic
    fun getCurrentProcessName(): String? {
        var name: String? = currentProcessNameByFile
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByAms()
        if (!TextUtils.isEmpty(name)) return name
        name = getCurrentProcessNameByReflect()
        return name
    }

    private val currentProcessNameByFile: String get() = try {
        val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
        val mBufferedReader = BufferedReader(FileReader(file))
        val processName = mBufferedReader.readLine().trim { it <= ' ' }
        mBufferedReader.close()
        processName
    } catch (e: Exception) {
        e.printStackTrace()
        ""
    }

    private fun getCurrentProcessNameByAms(): String? {
        try {
            val am = FintekUtils.requiredContext.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
                ?: return ""
            val info = am.runningAppProcesses
            if (info == null || info.size == 0) return ""
            val pid = Process.myPid()
            for (aInfo in info) {
                if (aInfo.pid == pid) {
                    if (aInfo.processName != null) {
                        return aInfo.processName
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            return ""
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(): String? {
        var processName = ""
        try {
            val app: Context = FintekUtils.requiredContext
            val loadedApkField = app.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[app]
            val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
            processName = getProcessName.invoke(activityThread) as String
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
        }
        return processName
    }
}