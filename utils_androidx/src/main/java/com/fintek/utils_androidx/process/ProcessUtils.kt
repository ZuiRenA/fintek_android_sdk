package com.fintek.utils_androidx.process

import android.app.ActivityManager
import android.content.Context
import android.os.Process
import android.text.TextUtils
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.catchOrEmpty
import com.fintek.utils_androidx.throwable.safelyVoid
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

    private val currentProcessNameByFile: String get() = catchOrEmpty {
        val file = File("/proc/" + Process.myPid() + "/" + "cmdline")
        val mBufferedReader = BufferedReader(FileReader(file))
        val processName = mBufferedReader.readLine().trim { it <= ' ' }
        mBufferedReader.close()
        processName
    }

    private fun getCurrentProcessNameByAms(): String? {
        safelyVoid {
            val am = FintekUtils.requiredContext.getSystemService(Context.ACTIVITY_SERVICE) as? ActivityManager
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
        }
        return ""
    }

    private fun getCurrentProcessNameByReflect(): String? {
        var processName = ""
        safelyVoid {
            val app: Context = FintekUtils.requiredContext
            val loadedApkField = app.javaClass.getField("mLoadedApk")
            loadedApkField.isAccessible = true
            val loadedApk = loadedApkField[app]
            val activityThreadField = loadedApk.javaClass.getDeclaredField("mActivityThread")
            activityThreadField.isAccessible = true
            val activityThread = activityThreadField[loadedApk]
            val getProcessName = activityThread.javaClass.getDeclaredMethod("getProcessName")
            processName = getProcessName.invoke(activityThread) as String
        }
        return processName
    }
}