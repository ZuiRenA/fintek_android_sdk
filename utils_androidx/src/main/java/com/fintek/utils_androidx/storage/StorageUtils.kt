package com.fintek.utils_androidx.storage

import android.annotation.SuppressLint
import android.app.usage.StorageStatsManager
import android.content.Context
import android.os.Build
import android.os.Environment
import android.os.StatFs
import android.os.storage.StorageManager
import android.os.storage.StorageVolume
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.FintekUtils
import java.io.File
import java.io.IOException
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.util.*

/**
 * Created by ChaoShen on 2020/11/18
 */
object StorageUtils {
    private const val DISTINGUISH = "-"

    @JvmStatic
    fun getMainStoragePath(): String = try {
        Environment.getDataDirectory().path
    } catch (e: Exception) {
        ""
    }

    @JvmStatic
    fun getExternalStoragePath(): String = try {
        Environment.getExternalStorageDirectory().path
    } catch (e: Exception) {
        ""
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getTotalSize(): Long = getStorageInfo().first

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getAvailableSize(): Long = getStorageInfo().second


    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUsedSize(): Long = getTotalSize() - getAvailableSize()

    /**
     * Return totalSize - systemSize
     * @return byte
     */
    @JvmStatic
    fun getAdjustSize(): Long {
        val path = Environment.getDataDirectory()
        val stat = StatFs(path.path)
        val blockSize = stat.blockSize.toLong()
        val totalSize = stat.blockCount.toLong()
        return  blockSize * totalSize
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getStorageInfo(): Pair<Long, Long> {
        try {
            val storageTotalSize: String
            val storageAvailableSize: String
            val storageStr = queryWithStorageManager()
            if (storageStr != null && storageStr.contains("-")) {
                val split = storageStr.split("-").toTypedArray()
                val storageTotalSizeStr = split[0]
                val storageAvailableSizeStr = split[1]
                storageTotalSize = storageTotalSizeStr
                storageAvailableSize = storageAvailableSizeStr
            } else {
                storageTotalSize = getExternalTotalSpace().toString()
                storageAvailableSize = getExternalAvailableSpace().toString()
            }
            return storageTotalSize.toLong() to storageAvailableSize.toLong()
        } catch (e: Exception) {
            return 0L to 0L
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getExternalTotalSpace(): Long = try {
        val stat = StatFs(getExternalStoragePath())
        stat.blockCountLong * stat.blockSizeLong
    } catch (e: Exception) {
        0L
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getExternalAvailableSpace(): Long = try {
        val stat = StatFs(getExternalStoragePath())
        stat.availableBlocksLong * stat.blockSizeLong
    } catch (e: Exception) {
        0L
    }

    @SuppressLint("DiscouragedPrivateApi", "UsableSpace")
    private fun queryWithStorageManager(): String? {
        val storageManager =
            FintekUtils.requiredContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            try {
                val getVolumeList = StorageManager::class.java.getDeclaredMethod("getVolumeList")
                val volumeList = getVolumeList.invoke(storageManager) as? Array<StorageVolume>
                var totalSize: Long = 0
                var availableSize: Long = 0
                if (volumeList != null) {
                    var getPathFile: Method? = null
                    for (volume in volumeList) {
                        if (getPathFile == null) {
                            getPathFile = volume.javaClass.getDeclaredMethod("getPathFile")
                        }
                        val file = getPathFile?.invoke(volume) as File
                        totalSize += file.totalSpace
                        availableSize += file.usableSpace
                    }
                }
                return totalSize.toString() + DISTINGUISH + availableSize
            } catch (e: NoSuchMethodException) {
                e.printStackTrace()
            } catch (e: IllegalAccessException) {
                e.printStackTrace()
            } catch (e: InvocationTargetException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                return ""
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val getVolumes =
                    StorageManager::class.java.getDeclaredMethod("getVolumes") //6.0
                val getVolumeInfo =
                    getVolumes.invoke(storageManager) as List<Any>
                var total = 0L
                var used = 0L
                var systemSize = 0L
                for (obj in getVolumeInfo) {
                    val getType = obj.javaClass.getField("type")
                    val type = getType.getInt(obj)
                    if (type == 1) { //TYPE_PRIVATE
                        var totalSize = 0L

                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            val getFsUuid =
                                obj.javaClass.getDeclaredMethod("getFsUuid")
                            val fsUuid = getFsUuid.invoke(obj) as? String
                            totalSize = getTotalSize(FintekUtils.requiredContext, fsUuid) //8.0 以后使用
                        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) { //7.1.1
                            val getPrimaryStorageSize =
                                StorageManager::class.java.getMethod("getPrimaryStorageSize") //5.0 6.0 7.0没有
                            totalSize = getPrimaryStorageSize.invoke(storageManager) as Long
                        }
                        val isMountedReadable =
                            obj.javaClass.getDeclaredMethod("isMountedReadable")
                        val readable =
                            isMountedReadable.invoke(obj) as Boolean
                        if (readable) {
                            val file =
                                obj.javaClass.getDeclaredMethod("getPath")
                            val f = file.invoke(obj) as File
                            if (totalSize == 0L) {
                                totalSize = f.totalSpace
                            }
                            systemSize = totalSize - f.totalSpace
                            used += totalSize - f.freeSpace
                            total += totalSize
                        }
                    } else if (type == 0) { //TYPE_PUBLIC
                        //外置存储
                        val isMountedReadable =
                            obj.javaClass.getDeclaredMethod("isMountedReadable")
                        val readable =
                            isMountedReadable.invoke(obj) as Boolean
                        if (readable) {
                            val file =
                                obj.javaClass.getDeclaredMethod("getPath")
                            val f = file.invoke(obj) as File
                            used += f.totalSpace - f.freeSpace
                            total += f.totalSpace
                        }
                    } else if (type == 2) { //TYPE_EMULATED
                    }
                }
                return total.toString() + DISTINGUISH + (total - used)
            } catch (e: SecurityException) {
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                return queryWithStatFs()
            }
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getTotalSize(context: Context, fsUuid: String?): Long {
        return try {
            val id: UUID = if (fsUuid == null) {
                StorageManager.UUID_DEFAULT
            } else {
                UUID.fromString(fsUuid)
            }
            val stats = context.getSystemService(
                StorageStatsManager::class.java
            )
            stats.getTotalBytes(id)
        } catch (e: NoSuchFieldError) {
            e.printStackTrace()
            -1
        } catch (e: NoClassDefFoundError) {
            e.printStackTrace()
            -1
        } catch (e: NullPointerException) {
            e.printStackTrace()
            -1
        } catch (e: IOException) {
            e.printStackTrace()
            -1
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun queryWithStatFs(): String? {
        val statFs = StatFs(Environment.getExternalStorageDirectory().path)

        //存储块
        val blockCount = statFs.blockCountLong
        //块大小
        val blockSize = statFs.blockSizeLong
        //可用块数量
        val availableCount = statFs.availableBlocksLong
        //剩余块数量，注：这个包含保留块（including reserved blocks）即应用无法使用的空间
        val freeBlocks = statFs.freeBlocksLong

        return (blockSize * blockCount).toString() + DISTINGUISH + blockSize * availableCount
    }
}