package com.fintek.utils_androidx.log

import android.os.Build
import android.util.Log
import androidx.collection.SimpleArrayMap
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.model.TagHead
import java.io.File
import java.io.IOException
import java.lang.reflect.ParameterizedType
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import java.util.regex.Pattern

/**
 * Created by ChaoShen on 2020/11/4
 */
object TimberUtil {
    
    @JvmStatic
    fun getConfig() = CONFIG

    @JvmStatic
    fun v(vararg contents: Any?) = log(Timber.V, CONFIG.getGlobalTag(), contents = contents)

    @JvmStatic
    fun vTag(tag: String, vararg contents: Any?) = log(Timber.V, tag, contents = contents)

    @JvmStatic
    fun d(vararg contents: Any?) = log(Timber.D, CONFIG.getGlobalTag(), contents = contents)

    @JvmStatic
    fun dTag(tag: String, vararg contents: Any?) = log(Timber.D, tag, contents = contents)

    @JvmStatic
    fun i(vararg contents: Any?) = log(Timber.I, CONFIG.getGlobalTag(), contents = contents)

    @JvmStatic
    fun iTag(tag: String, vararg contents: Any?) = log(Timber.I, tag, contents = contents)

    @JvmStatic
    fun w(vararg contents: Any?) = log(Timber.W, CONFIG.getGlobalTag(), contents = contents)

    @JvmStatic
    fun wTag(tag: String, vararg contents: Any?) = log(Timber.W, tag, contents = contents)

    @JvmStatic
    fun e(vararg contents: Any?) = log(Timber.E, CONFIG.getGlobalTag(), contents = contents)

    @JvmStatic
    fun eTag(tag: String, vararg contents: Any?) = log(Timber.E, tag, contents = contents)

    @JvmStatic
    fun a(vararg contents: Any?) = log(Timber.A, CONFIG.getGlobalTag(), contents = contents)

    @JvmStatic
    fun aTag(tag: String, vararg contents: Any?) = log(Timber.A, tag, contents = contents)

    @JvmStatic
    @JvmOverloads
    fun file(
        type: Timber = Timber.D,
        tag: String = CONFIG.getGlobalTag(),
        content: Any?
    ) = log(type and FILE, tag, content)

    @JvmStatic
    @JvmOverloads
    fun json(
        type: Timber = Timber.D,
        tag: String = CONFIG.getGlobalTag(),
        content: Any?
    ) = log(type and JSON, tag, content)

    @JvmStatic
    @JvmOverloads
    fun xml(
        type: Timber = Timber.D,
        tag: String = CONFIG.getGlobalTag(),
        content: Any?
    ) = log(type and XML, tag, content)

    fun log(type: Timber, tag: String, vararg contents: Any?) {
        if (!CONFIG.isLogSwitch()) return
        val typeLow = type.logLevel and 0x0f
        val typeHigh = type.logLevel and 0xf0

        if (CONFIG.isLog2ConsoleSwitch() || CONFIG.isLog2FileSwitch() || typeHigh == FILE) {
            if (typeLow < CONFIG.consoleFilter.logLevel && typeLow < CONFIG.fileFilter.logLevel) return

            val tagHead = tag.processTagAndHead()
            val body = processBody(typeHigh, contents = contents)
            if (CONFIG.isLog2ConsoleSwitch() && typeHigh != FILE && typeLow >= CONFIG.consoleFilter.logLevel) {
                print2Console(typeLow, tagHead.tag, tagHead.consoleHead?.toTypedArray(), body ?: "")
            }
            if ((CONFIG.isLog2FileSwitch() || typeHigh == FILE) && typeLow >= CONFIG.fileFilter.logLevel) {
                EXECUTOR.execute {
                    print2File(typeLow, tagHead.tag, tagHead.fileHead + body)
                }
            }
        }
    }

    private fun String.processTagAndHead(): TagHead {
        var shadowTag = this
        if (!CONFIG.tagIsSpace && !CONFIG.isLogHeadSwitch()) {
            shadowTag = CONFIG.getGlobalTag()
        } else {
            val stackTrace = Throwable().stackTrace
            val stackIndex = 3 + CONFIG.getStackOffset()

            if (stackIndex >= stackTrace.size) {
                val targetElement = stackTrace[3]
                val fileName = targetElement.getInternalFileName()
                if (CONFIG.tagIsSpace && shadowTag.isBlank()) {
                    val index = fileName.indexOf('.') // Use proguard may not find '.'.
                    shadowTag = when (index) {
                        -1 -> fileName
                        else -> fileName.substring(0, index)
                    }
                }
                return TagHead(shadowTag, null, ": ")
            }

            var targetElement = stackTrace[stackIndex]
            val fileName = targetElement.getInternalFileName()
            if (CONFIG.tagIsSpace && shadowTag.isBlank()) {
                val index = fileName.indexOf('.')
                shadowTag = when (index) {
                    -1 -> fileName
                    else -> fileName.substring(0, index)
                }
            }

            if (CONFIG.isLogHeadSwitch()) {
                val tName = Thread.currentThread().name
                val head = Formatter().format(
                    "%s, %s.%s(%s:%d)",
                    tName,
                    targetElement.className,
                    targetElement.methodName,
                    fileName,
                    targetElement.lineNumber
                ).toString()

                val fileHead = " [$head]: "
                if (CONFIG.getStackDeep() <= 1) {
                    return TagHead(shadowTag, listOf(head), fileHead)
                } else {
                    val consoleHead: Array<String> = Array(
                        CONFIG.getStackDeep().coerceAtMost(
                            stackTrace.size - stackIndex
                        )
                    ) { "" }

                    consoleHead[0] = head
                    val spaceLen = tName.length + 2
                    val space = Formatter().format("%" + spaceLen + "s", "").toString()
                    consoleHead.forEachIndexed { index, s ->
                        targetElement = stackTrace[index + stackIndex]
                        consoleHead[index] = Formatter().format(
                            "%s%s.%s(%s:%d)",
                            space,
                            targetElement.className,
                            targetElement.methodName,
                            targetElement.getInternalFileName(),
                            targetElement.lineNumber
                        ).toString()
                    }
                    return TagHead(shadowTag, consoleHead.toList(), fileHead)
                }
            }
        }

        return TagHead(shadowTag, null, ": ")
    }

    private fun processBody(type: Int, vararg contents: Any?): String? {
        var body: String = NULL
        if (contents.isNotEmpty()) {
            body = when (contents.size) {
                1 -> formatObject(type, contents[0])
                else -> {
                    val sb = StringBuilder()
                    contents.forEachIndexed { index, obj ->
                        sb.append(ARGS)
                            .append("[").append(index).append("]")
                            .append(" = ")
                            .append(formatObject(obj))

                        if (index != contents.lastIndex) {
                            sb.append(LINE_SEP)
                        }
                    }
                    sb.toString()
                }
            }
        }

        return if (body.isEmpty()) NOTHING else body
    }

    private fun StackTraceElement.getInternalFileName(): String {
        if (fileName != null) return fileName
        // If name of file is null, should add
        // "-keepattributes SourceFile,LineNumberTable" in proguard file.
        var shadowClassName = className
        val classNameInfo = shadowClassName.split("\\.".toRegex()).toTypedArray()
        if (classNameInfo.isNotEmpty()) {
            shadowClassName = classNameInfo[classNameInfo.lastIndex]
        }

        val index = shadowClassName.indexOf('$')
        if (index != -1) {
            shadowClassName = shadowClassName.substring(0, index)
        }

        return "$shadowClassName.java"
    }

    internal fun formatObject(type: Int, any: Any?): String {
        if (any == null) return NULL
        if (type == JSON) return TimberFormatter.object2String(any, JSON)
        if (type == XML) return TimberFormatter.object2String(any, XML)
        return formatObject(any)
    }

    internal fun formatObject(any: Any?): String {
        if (any == null) return NULL
        if (!I_FORMATTER_MAP.isEmpty) {
            val iFormatter: IFormatter<Any?>? =
                I_FORMATTER_MAP.get(getClassFromObject(any)) as? IFormatter<Any?>
            if (iFormatter != null) {
                //noinspection unchecked
                return iFormatter.format(any) ?: NULL
            }
        }

        return TimberFormatter.object2String(any)
    }

    private fun getClassFromObject(obj: Any): Class<*>? {
        val objClass: Class<*> = obj.javaClass
        if (objClass.isAnonymousClass || objClass.isSynthetic) {
            val genericInterfaces = objClass.genericInterfaces
            var className: String
            if (genericInterfaces.size == 1) { // interface
                var type = genericInterfaces[0]
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = type.toString()
            } else { // abstract class or lambda
                var type = objClass.genericSuperclass
                while (type is ParameterizedType) {
                    type = type.rawType
                }
                className = "$type"
            }
            if (className.startsWith("class ")) {
                className = className.substring(6)
            } else if (className.startsWith("interface ")) {
                className = className.substring(10)
            }
            try {
                return Class.forName(className)
            } catch (e: ClassNotFoundException) {
                e.printStackTrace()
            }
        }
        return objClass
    }

    private fun print2Console(
        type: Int,
        tag: String,
        head: Array<String>?,
        msg: String
    ) {
        if (CONFIG.isSingleTagSwitch()) {
            printSingleTagMsg(type, tag, processSingleTagMsg(head, msg))
        } else {
            printBorder(type, tag, true)
            printHead(type, tag, head)
            printMsg(type, tag, msg)
            printBorder(type, tag, false)
        }
    }

    private fun printBorder(type: Int, tag: String, isTop: Boolean) {
        if (CONFIG.isLogBorderSwitch()) {
            Log.println(type, tag, if (isTop) TOP_BORDER else BOTTOM_BORDER)
        }
    }

    private fun printHead(type: Int, tag: String, head: Array<String>?) {
        if (!head.isNullOrEmpty()) {
            head.forEach {
                Log.println(type, tag, if (CONFIG.isLogBorderSwitch()) LEFT_BORDER + it else it)
            }
            if (CONFIG.isLogBorderSwitch()) Log.println(type, tag, MIDDLE_BORDER)
        }
    }

    private fun printMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub = len / MAX_LEN
        if (countOfSub > 0) {
            var index = 0
            for (i in 0 until countOfSub) {
                printSubMsg(type, tag, msg.substring(index, index + MAX_LEN))
                index += MAX_LEN
            }

            if (index != len) printSubMsg(type, tag, msg.substring(index, len))
        } else {
            printSubMsg(type, tag, msg)
        }
    }

    private fun printSubMsg(type: Int, tag: String, msg: String) {
        if (!CONFIG.isLogBorderSwitch()) {
            Log.println(type, tag, msg)
            return
        }

        val lines = msg.split(LINE_SEP)
        lines.forEach {
            Log.println(type, tag, LEFT_BORDER + it)
        }
    }

    private fun processSingleTagMsg(
        head: Array<String>?,
        msg: String
    ): String {
        val sb = StringBuilder()
        if (CONFIG.isLogBorderSwitch()) {
            sb.append(PLACEHOLDER).append(LINE_SEP)
            sb.append(TOP_BORDER).append(LINE_SEP)
            if (head != null) {
                for (aHead in head) {
                    sb.append(LEFT_BORDER).append(aHead).append(LINE_SEP)
                }
                sb.append(MIDDLE_BORDER).append(LINE_SEP)
            }
            for (line in msg.split(LINE_SEP)) {
                sb.append(LEFT_BORDER).append(line).append(LINE_SEP)
            }
            sb.append(BOTTOM_BORDER)
        } else {
            if (head != null) {
                sb.append(PLACEHOLDER).append(LINE_SEP)
                for (aHead in head) {
                    sb.append(aHead).append(LINE_SEP)
                }
            }
            sb.append(msg)
        }
        return sb.toString()
    }

    private fun printSingleTagMsg(type: Int, tag: String, msg: String) {
        val len = msg.length
        val countOfSub: Int =
            if (CONFIG.isLogBorderSwitch()) (len - BOTTOM_BORDER.length) / MAX_LEN else len / MAX_LEN
        if (countOfSub > 0) {
            if (CONFIG.isLogBorderSwitch()) {
                Log.println(type, tag, msg.substring(0, MAX_LEN) + LINE_SEP + BOTTOM_BORDER)
                var index: Int = MAX_LEN
                for (i in 1 until countOfSub) {
                    Log.println(
                        type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP
                                + LEFT_BORDER + msg.substring(
                            index,
                            index + MAX_LEN
                        ) + LINE_SEP + BOTTOM_BORDER
                    )
                    index += MAX_LEN
                }
                if (index != len - BOTTOM_BORDER.length) {
                    Log.println(
                        type, tag, PLACEHOLDER + LINE_SEP + TOP_BORDER + LINE_SEP + LEFT_BORDER +
                                msg.substring(index, len)
                    )
                }
            } else {
                Log.println(type, tag, msg.substring(0, MAX_LEN))
                var index: Int = MAX_LEN
                for (i in 1 until countOfSub) {
                    Log.println(
                        type, tag, PLACEHOLDER + LINE_SEP + msg.substring(
                            index, index + MAX_LEN
                        )
                    )
                    index += MAX_LEN
                }
                if (index != len) {
                    Log.println(type, tag, PLACEHOLDER + LINE_SEP + msg.substring(index, len))
                }
            }
        } else {
            Log.println(type, tag, msg)
        }
    }

    private fun print2File(type: Int, tag: String, msg: String) {
        val format: String = getSdf().format(Date())
        val date = format.substring(0, 10)
        val time = format.substring(11)
        val fullPath: String =
            (CONFIG.getDir() + CONFIG.getFilePrefix() + "_"
                    + date + "_" +
                    CONFIG.getProcessName() + CONFIG.getFileExtension())
        if (!createOrExistsFile(fullPath, date)) {
            Log.e("LogUtils", "create $fullPath failed!")
            return
        }
        val content = time + T[type - Timber.V.logLevel] + "/" + tag + msg + LINE_SEP
        input2File(fullPath, content)
    }

    private fun getSdf(): SimpleDateFormat {
        if (simpleDateFormat == null) {
            simpleDateFormat = SimpleDateFormat("yyyy_MM_dd HH:mm:ss.SSS ", Locale.getDefault())
        }
        return simpleDateFormat!!
    }

    private fun createOrExistsFile(filePath: String, date: String): Boolean {
        val file = File(filePath)
        if (file.exists()) return file.isFile
        return if (!UtilsBridge.createOrExistsDir(file.parentFile)) false else try {
            deleteDueLogs(filePath, date)
            val isCreate = file.createNewFile()
            if (isCreate) {
                printDeviceInfo(filePath, date)
            }
            isCreate
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }

    private fun deleteDueLogs(filePath: String, date: String) {
        if (CONFIG.getSaveDays() <= 0) return
        val file = File(filePath)
        val parentFile: File? = file.parentFile
        val files = parentFile?.listFiles { _, name ->
            isMatchLogFileName(
                name
            )
        }
        if (files == null || files.isEmpty()) return
        val sdf = SimpleDateFormat("yyyy_MM_dd", Locale.getDefault())
        try {
            val dueMillis: Long = checkNotNull(sdf.parse(date)?.time) - CONFIG.getSaveDays() * 86400000L
            for (aFile in files) {
                val name = aFile.name
                val logDay: String = findDate(name)
                if (checkNotNull(sdf.parse(logDay)?.time) <= dueMillis) {
                    EXECUTOR.execute {
                        val delete = aFile.delete()
                        if (!delete) {
                            Log.e("LogUtils", "delete $aFile failed!")
                        }
                    }
                }
            }
        } catch (e: ParseException) {
            e.printStackTrace()
        }
    }

    private fun isMatchLogFileName(name: String): Boolean {
        return name.matches("^${CONFIG.getFilePrefix()}_[0-9]{4}_[0-9]{2}_[0-9]{2}_.*$".toRegex())
    }

    private fun findDate(str: String): String {
        val pattern = Pattern.compile("[0-9]{4}_[0-9]{2}_[0-9]{2}")
        val matcher = pattern.matcher(str)
        return if (matcher.find()) {
            matcher.group()
        } else ""
    }

    private fun printDeviceInfo(filePath: String, date: String) {
        val head = """
            ************* Log Head ****************
            Date of Log        : $date
            Device Manufacturer: ${Build.MANUFACTURER}
            Device Model       : ${Build.MODEL}
            Android Version    : ${Build.VERSION.RELEASE}
            Android SDK        : ${Build.VERSION.SDK_INT}
            App VersionName    : ${UtilsBridge.getAppVersionName()}
            App VersionCode    : ${UtilsBridge.getAppVersionCode()}
            ************* Log Head ****************
            """.trimIndent()
        input2File(filePath, head)
    }

    private fun input2File(filePath: String, input: String) {
        CONFIG.fileWriter?.write(filePath, input) ?: UtilsBridge.writeFileFromString(
            filePath,
            input,
            true
        )
    }
}

internal val CONFIG = TimberConfig()
internal val T = charArrayOf('V', 'D', 'I', 'W', 'E', 'A')

internal val FILE_SEP = System.getProperty("file.separator") ?: ""
internal val LINE_SEP = System.getProperty("line.separator") ?: ""

internal val I_FORMATTER_MAP: SimpleArrayMap<Class<*>, IFormatter<*>> = SimpleArrayMap()

internal const val MAX_LEN = 1100 //fit for Chinese character
internal const val NOTHING = "log nothing"
internal const val NULL = "null"
internal const val ARGS = "args"
internal const val PLACEHOLDER = " "

internal const val FILE = 0x10
internal const val JSON = 0x20
internal const val XML = 0x30

private var simpleDateFormat: SimpleDateFormat? = null
private val EXECUTOR = Executors.newSingleThreadExecutor()

private const val TOP_CORNER = "┌"
private const val MIDDLE_CORNER = "├"
private const val LEFT_BORDER = "│ "
private const val BOTTOM_CORNER = "└"
private const val SIDE_DIVIDER = "────────────────────────────────────────────────────────"
private const val MIDDLE_DIVIDER = "┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄┄"

private const val TOP_BORDER: String = TOP_CORNER + SIDE_DIVIDER + SIDE_DIVIDER
private const val MIDDLE_BORDER: String = MIDDLE_CORNER + MIDDLE_DIVIDER + MIDDLE_DIVIDER
private const val BOTTOM_BORDER: String = BOTTOM_CORNER + SIDE_DIVIDER + SIDE_DIVIDER

sealed class Timber(internal val logLevel: Int, internal val logChar: Char) {
    object V : Timber(Log.VERBOSE, 'V')
    object D : Timber(Log.DEBUG, 'D')
    object I : Timber(Log.INFO, 'I')
    object W : Timber(Log.WARN, 'W')
    object E : Timber(Log.ERROR, 'E')
    object A : Timber(Log.ASSERT, 'A')

    private companion object {
        fun Int.toTimber(): Timber = when(this) {
            Log.VERBOSE -> V
            Log.DEBUG -> D
            Log.INFO -> I
            Log.WARN -> W
            Log.ERROR -> E
            Log.ASSERT -> A
            else -> V
        }
    }

    infix fun and(other: Int): Timber = (logLevel and other).toTimber()
}