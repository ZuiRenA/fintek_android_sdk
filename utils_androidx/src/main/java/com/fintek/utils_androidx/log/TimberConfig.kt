package com.fintek.utils_androidx.log

import android.os.Build
import androidx.annotation.IntRange
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import java.io.File
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

/**
 * Created by ChaoShen on 2020/11/10
 */
class TimberConfig {

    // The default storage directory of log.
    private val defaultDir: String = if (UtilsBridge.isSDCardEnableByEnvironment()
        && FintekUtils.requiredContext.getExternalFilesDir(null) != null
    ) {
        FintekUtils.requiredContext.getExternalFilesDir(null).toString() + FILE_SEP + "log" + FILE_SEP
    } else {
        FintekUtils.requiredContext.filesDir.toString() + FILE_SEP + "log" + FILE_SEP
    }

    private var dir: String?                = null        // The storage directory of log.
    private var filePrefix                  = "util"      // The file prefix of log.
    private var fileExtension               = ".txt"      // The file extension of log.
    private var logSwitch: Boolean          = true        // The switch of log.
    private var log2ConsoleSwitch           = true        // The logcat's switch of log.
    private var globalTag                   = ""          // The global tag of log.
    internal var tagIsSpace                 = true        // The global tag is space.
    private var logHeadSwitch               = true        // The head's switch of log
    private var log2FileSwitch              = false       // The file's switch of log.
    private var logBorderSwitch             = true        // The border's switch of log.
    private var singleTagSwitch             = true        // The single tag of log.
    internal var consoleFilter: Timber      = Timber.V    // The console's filter of log.
    internal var fileFilter: Timber         = Timber.V    // The file's filter of log.
    private var stackDeep                   = 1           // The stack's deep of log.
    private var stackOffset                 = 0           // The stack's offset of log.
    private var saveDays                    = -1          // The save days of log.
    private var processName: String?        = UtilsBridge.getCurrentProcessName()
    internal var fileWriter: IFileWriter?   = null
        private set
    internal var jsonConvert: IJsonConvert? = null
        private set

    fun setLogSwitch(logSwitch: Boolean) = apply {
        this.logSwitch = logSwitch
    }

    fun setConsoleSwitch(consoleSwitch: Boolean) = apply {
        this.log2ConsoleSwitch = consoleSwitch
    }

    fun setGlobalTag(tag: String) = apply {
        if (tag.isEmpty() || tag.isBlank()) {
            this.globalTag = ""
            this.tagIsSpace = true
        } else {
            this.globalTag = tag
            this.tagIsSpace = false
        }
    }

    fun setLogHeadSwitch(logHeadSwitch: Boolean) = apply {
        this.logHeadSwitch = logHeadSwitch
    }

    fun setLog2FileSwitch(log2FileSwitch: Boolean) = apply {
        this.log2FileSwitch = log2FileSwitch
    }

    fun setDir(dir: String) = apply {
        this.dir = if (dir.isEmpty() || dir.isBlank()) {
            null
        } else {
            when {
                dir.endsWith(FILE_SEP) -> dir
                else -> dir + FILE_SEP
            }
        }
    }

    fun setDir(dir: File?) = apply {
        this.dir = if (dir == null) null else dir.absolutePath + FILE_SEP
    }

    fun setFilePrefix(filePrefix: String) = apply {
        this.filePrefix = if (filePrefix.isEmpty() || filePrefix.isBlank()) "util" else filePrefix
    }

    fun setFileExtension(fileExtension: String) = apply {
        this.fileExtension = if (fileExtension.isEmpty() || fileExtension.isBlank()) ".txt" else when {
            fileExtension.startsWith(".") -> fileExtension
            else -> ".$fileExtension"
        }
    }

    fun setBorderSwitch(borderSwitch: Boolean) = apply {
        this.logBorderSwitch = borderSwitch
    }

    fun setSingleTagSwitch(singleTagSwitch: Boolean) = apply {
        this.singleTagSwitch = singleTagSwitch
    }

    fun setConsoleFilter(consoleFilter: Timber) = apply {
        this.consoleFilter = consoleFilter
    }

    fun setFileFilter(fileFilter: Timber) = apply {
        this.fileFilter = fileFilter
    }

    fun setStackDeep(@IntRange(from = 1) stackDeep: Int) = apply {
        this.stackDeep = stackDeep
    }

    fun setStackOffset(@IntRange(from = 0) stackOffset: Int) = apply {
        this.stackOffset = stackOffset
    }

    fun setSaveDays(@IntRange(from = 1) saveDays: Int) = apply {
        this.saveDays = saveDays
    }

    fun <T> addFormatter(iFormatter: IFormatter<T>) = apply {
        I_FORMATTER_MAP.put(iFormatter.getTypeClassFromParadigm(), iFormatter)
    }

    fun setFileWriter(fileWriter: IFileWriter) = apply {
        this.fileWriter = fileWriter
    }

    fun setJsonConvert(jsonConvert: IJsonConvert) = apply {
        this.jsonConvert = jsonConvert
    }

    fun getProcessName(): String = if (processName == null) "" else checkNotNull(processName).replace(":", "_")

    fun getDefaultDir(): String = defaultDir

    fun getDir(): String = if (dir == null) defaultDir else checkNotNull(dir)

    fun getFilePrefix(): String = filePrefix

    fun getFileExtension(): String = fileExtension

    fun getGlobalTag(): String = if (globalTag.isEmpty() || globalTag.isBlank()) "" else globalTag

    fun getConsoleFilter(): Char = consoleFilter.logChar

    fun getFileFilter(): Char = fileFilter.logChar

    fun getStackDeep(): Int = stackDeep

    fun getStackOffset(): Int = stackOffset

    fun getSaveDays(): Int = saveDays

    fun isLogSwitch(): Boolean = logSwitch

    fun isLog2ConsoleSwitch(): Boolean = log2ConsoleSwitch

    fun isLogHeadSwitch(): Boolean = logHeadSwitch

    fun isLog2FileSwitch(): Boolean = log2FileSwitch

    fun isLogBorderSwitch(): Boolean = singleTagSwitch

    fun isSingleTagSwitch(): Boolean = singleTagSwitch

    override fun toString(): String = "process: ${getProcessName()}" +
            LINE_SEP + "switch: ${isLogSwitch()}" +
            LINE_SEP + "console: ${isLog2ConsoleSwitch()}" +
            LINE_SEP + "tag: ${getGlobalTag()}" +
            LINE_SEP + "head: ${isLogHeadSwitch()}" +
            LINE_SEP + "file: ${isLog2FileSwitch()}" +
            LINE_SEP + "dir: ${getDir()}" +
            LINE_SEP + "filePrefix: ${getFilePrefix()}" +
            LINE_SEP + "border: ${isLogBorderSwitch()}" +
            LINE_SEP + "singleTag: ${isSingleTagSwitch()}" +
            LINE_SEP + "consoleFilter: ${getConsoleFilter()}" +
            LINE_SEP + "fileFilter: ${getFileFilter()}" +
            LINE_SEP + "stackDeep: ${getStackDeep()}" +
            LINE_SEP + "stackOffset: ${getStackOffset()}" +
            LINE_SEP + "saveDays: ${getSaveDays()}" +
            LINE_SEP + "formatter: $I_FORMATTER_MAP"
}

interface IFileWriter {
    fun write(file: String?, content: String?)
}

interface IJsonConvert {
    fun convert(any: Any?): String
}

abstract class IFormatter<T> {
    abstract fun format(t: T): String?
}

private fun <T> IFormatter<T>.getTypeClassFromParadigm(): Class<T>? {
    val genericInterfaces: Array<Type> = javaClass.genericInterfaces
    var type: Type? =if (genericInterfaces.size == 1) {
        genericInterfaces[0]
    } else {
        javaClass.genericSuperclass
    }
    type = (type as ParameterizedType).actualTypeArguments[0]
    while (type is ParameterizedType) {
        type = type.rawType
    }
    var className = type.toString()
    if (className.startsWith("class ")) {
        className = className.substring(6)
    } else if (className.startsWith("interface ")) {
        className = className.substring(10)
    }
    try {
        return Class.forName(className) as Class<T>
    } catch (e: ClassNotFoundException) {
        e.printStackTrace()
    }
    return null
}