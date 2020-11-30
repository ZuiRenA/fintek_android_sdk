package com.fintek.utils_androidx.storage

import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import android.text.format.Formatter
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.FintekUtils
import java.io.File
import java.lang.reflect.Array
import java.lang.reflect.InvocationTargetException
import java.util.ArrayList

/**
 * Created by ChaoShen on 2020/11/10
 */
object SDCardUtils {
    /**
     * Return whether sdcard is enabled by environment.
     *
     * @return `true`: enabled  `false`: disabled
     */
    @JvmStatic
    fun isSDCardEnableByEnvironment(): Boolean {
        return Environment.MEDIA_MOUNTED == Environment.getExternalStorageState()
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getTotalSize(): Long = getDisk().second

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getAvailableSize(): Long = getDisk().first

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUsedSize(): Long = getTotalSize() - getAvailableSize()

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getDisk(): Pair<Long, Long> {
        val sdCardInfo = getSDCardInfo()
        if (sdCardInfo.isEmpty()) {
            return 0L to 0L
        }
        for (i in sdCardInfo.indices) {
            if (sdCardInfo[i].isRemovable) {
                val s = sdCardInfo[i].path
                return getDiskCapacity(s)
            }
        }
        return 0L to 0L
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getDiskCapacity(
        path: String
    ): Pair<Long, Long> {
        val file = File(path)
        if (!file.exists()) {
            return 0L to 0L
        }
        val stat = StatFs(path)
        val blockSize = stat.blockSizeLong
        val totalBlockCount = stat.blockCountLong
        val feeBlockCount = stat.availableBlocksLong
        return Formatter.formatFileSize(FintekUtils.requiredContext, blockSize * feeBlockCount).toLong() to
                Formatter.formatFileSize(FintekUtils.requiredContext, blockSize * totalBlockCount).toLong()
    }

    private fun getSDCardInfo(): List<SDCardInfo> {
        val paths: MutableList<SDCardInfo> =
            ArrayList()
        val sm =
            FintekUtils.requiredContext.getSystemService(Context.STORAGE_SERVICE) as? StorageManager
                ?: return paths
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val storageVolumes = sm.storageVolumes
            try {
                val getPathMethod =
                    StorageVolume::class.java.getMethod("getPath")
                for (storageVolume in storageVolumes) {
                    val isRemovable = storageVolume.isRemovable //是否可卸载
                    val state = storageVolume.state //是否已载入
                    val path =
                        getPathMethod.invoke(storageVolume) as String //路径
                    paths.add(SDCardInfo(path, state, isRemovable))
                }
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            }
            paths
        } else {
            try {
                val storageVolumeClazz =
                    Class.forName("android.os.storage.StorageVolume")
                val getPathMethod =
                    storageVolumeClazz.getMethod("getPath")
                val isRemovableMethod =
                    storageVolumeClazz.getMethod("isRemovable")
                val getVolumeStateMethod =
                    StorageManager::class.java.getMethod(
                        "getVolumeState",
                        String::class.java
                    )
                val getVolumeListMethod =
                    StorageManager::class.java.getMethod("getVolumeList")
                val result = getVolumeListMethod.invoke(sm)
                val length = Array.getLength(result)
                for (i in 0 until length) {
                    val storageVolumeElement = Array.get(result, i)
                    val path =
                        getPathMethod.invoke(storageVolumeElement) as String
                    val isRemovable =
                        isRemovableMethod.invoke(storageVolumeElement) as Boolean
                    val state =
                        getVolumeStateMethod.invoke(sm, path) as String
                    paths.add(SDCardInfo(path, state, isRemovable))
                }
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            }
            paths
        }
    }

    private data class SDCardInfo(
        val path: String,
        val state: String,
        val isRemovable: Boolean
    )
}