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
import com.fintek.utils_androidx.throwable.catchOrEmpty
import com.fintek.utils_androidx.throwable.catchOrLong
import com.fintek.utils_androidx.throwable.safely
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
    fun getMainStoragePath(): String = catchOrEmpty {
        Environment.getDataDirectory().path
    }

    @JvmStatic
    fun getExternalStoragePath(): String = catchOrEmpty {
        Environment.getExternalStorageDirectory().path
    }


    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getTotalSizeRepaired(): Long = getStorageInfoRepaired().first

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getAvailableSizeRepaired(): Long = getStorageInfoRepaired().second


    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUsedSizeRepaired(): Long = getTotalSizeRepaired() - getAvailableSizeRepaired()

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getTotalSizeBug(): Long = getStorageInfoBug().first

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getAvailableSizeBug(): Long = getStorageInfoBug().second


    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun getUsedSizeBug(): Long = getTotalSizeBug() - getAvailableSizeBug()

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
        return blockSize * totalSize
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getStorageInfoRepaired(): Pair<Long, Long> = safely(0L to 0L) {
        val storageTotalSize: String
        val storageAvailableSize: String
        val storageStr = queryWithStorageManagerRepaired()
        if (storageStr.contains("-")) {
            val split = storageStr.split("-").toTypedArray()
            val storageTotalSizeStr = split[0]
            val storageAvailableSizeStr = split[1]
            storageTotalSize = storageTotalSizeStr
            storageAvailableSize = storageAvailableSizeStr
        } else {
            storageTotalSize = getExternalTotalSpace().toString()
            storageAvailableSize = getExternalAvailableSpace().toString()
        }
        storageTotalSize.toLong() to storageAvailableSize.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getStorageInfoBug(): Pair<Long, Long> = safely(0L to 0L) {
        val storageTotalSize: String
        val storageAvailableSize: String
        val storageStr = queryWithStorageManagerBug()
        if (storageStr.contains("-")) {
            val split = storageStr.split("-").toTypedArray()
            val storageTotalSizeStr = split[0]
            val storageAvailableSizeStr = split[1]
            storageTotalSize = storageTotalSizeStr
            storageAvailableSize = storageAvailableSizeStr
        } else {
            storageTotalSize = getExternalTotalSpace().toString()
            storageAvailableSize = getExternalAvailableSpace().toString()
        }
        storageTotalSize.toLong() to storageAvailableSize.toLong()
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getExternalTotalSpace(): Long = catchOrLong {
        val stat = StatFs(getExternalStoragePath())
        stat.blockCountLong * stat.blockSizeLong
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    private fun getExternalAvailableSpace(): Long = catchOrLong {
        val stat = StatFs(getExternalStoragePath())
        stat.availableBlocksLong * stat.blockSizeLong
    }

    @SuppressLint("DiscouragedPrivateApi", "UsableSpace")
    private fun queryWithStorageManagerRepaired(): String {
        val storageManager =
            FintekUtils.requiredContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return catchOrEmpty {
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
                totalSize.toString() + DISTINGUISH + availableSize
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return catchOrEmpty(queryWithStatFs()) {
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
                            totalSize = getTotalSizeRepaired(FintekUtils.requiredContext, fsUuid) //8.0 以后使用
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
                total.toString() + DISTINGUISH + (total - used)
            }
        }
        return ""
    }

    @SuppressLint("DiscouragedPrivateApi", "UsableSpace")
    private fun queryWithStorageManagerBug(): String {
        val storageManager =
            FintekUtils.requiredContext.getSystemService(Context.STORAGE_SERVICE) as StorageManager
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return catchOrEmpty {
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
                totalSize.toString() + DISTINGUISH + availableSize
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return catchOrEmpty(queryWithStatFs()) {
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
                            val fsUuid = getFsUuid.invoke(obj) as String
                            totalSize = getTotalSizeRepaired(FintekUtils.requiredContext, fsUuid) //8.0 以后使用
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
                total.toString() + DISTINGUISH + (total - used)
            }
        }
        return ""
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun getTotalSizeRepaired(context: Context, fsUuid: String?): Long {
        return catchOrLong(-1) {
            val id: UUID = if (fsUuid == null) {
                StorageManager.UUID_DEFAULT
            } else {
                UUID.fromString(fsUuid)
            }
            val stats = context.getSystemService(
                StorageStatsManager::class.java
            )
            stats.getTotalBytes(id)
        }
    }

    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun queryWithStatFs(): String {
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

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun internalTotalStorageSize(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.blockCountLong * stat.blockSizeLong
    }

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    fun internalAvailableStorageSize(): Long {
        val stat = StatFs(Environment.getExternalStorageDirectory().path)
        return stat.availableBlocksLong * stat.blockSizeLong
    }
}