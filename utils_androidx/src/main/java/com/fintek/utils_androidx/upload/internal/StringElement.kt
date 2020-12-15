package com.fintek.utils_androidx.upload.internal

import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayInputStream
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicInteger


private val PARENT: File? get() {
    val file: File? = FintekUtils.requiredContext.getExternalFilesDir("element/")
    val isExist = file?.exists() ?: false
    if (!isExist) {
        file?.mkdirs()
    }
    return file
}

internal val HEADER: File? get() {
    val file = File(PARENT, "/header.txt")
    if (!file.exists()) {
        return null
    }
    return file
}

internal val CONTENT: File? get() {
    val file = File(PARENT, "/content.txt")
    if (!file.exists()) {
        return null
    }
    return file
}

internal val IS_EXISTED: Boolean get() {
    if (HEADER == null && CONTENT == null) return false
    val headerList: List<String>? = HEADER?.readLines()
    val contentList: List<String>? = CONTENT?.readLines()
    if (headerList.isNullOrEmpty() || contentList.isNullOrEmpty()) return false
    return true
}

private val gson = Gson()

internal data class Header(
    val total: Int,
    val part: Int,
)

class StringElement(
    rsa: String,
    limit: Int = 100 * 1024, // 100kb, Base unit is byte
) : Element<String>() {

    override val total: AtomicInteger

    override val partIndex: AtomicInteger = AtomicInteger()

    init {
        val bis = ByteArrayInputStream(rsa.toByteArray())
        var i: Int
        var position = 0
        val buffer = ByteArray(limit)
        val sb = StringBuilder()
        while (bis.read(buffer).also { i = it } > 0) {
            for (b in buffer) {
                sb.append(b.toChar())
            }
            Arrays.fill(buffer, 0.toByte())
            buffer.fill(0.toByte())
            sb.append("\n")
            position ++
        }
        sb.delete(sb.length - 2, sb.length)
        total = AtomicInteger(position)
        write(sb.toString())
    }

    override fun getAsList(): List<String> {
        val contentList = CONTENT?.readLines()
        return contentList ?: emptyList()
    }

    override fun next(): String {
        if (!hasNext()) {
            throw ArrayIndexOutOfBoundsException("Element don't have next")
        }

        partIndex.getAndIncrement()
        val internalList = getAsList().toMutableList()
        val first: String = internalList.removeFirst()
        save(internalList)
        return first
    }

    override fun save(element: List<String>) {
        val headerFile = HEADER ?: File(PARENT, "/header.txt")
        val contentFile = CONTENT ?: File(PARENT, "/content.txt")

        val header = Header(total = total.get(), part = partIndex.get())
        UtilsBridge.writeFileFromString(headerFile.path, gson.toJson(header), false)

        val sb = StringBuilder()
        for (i in element.indices) {
            sb.append(element[i])
            if (i != element.lastIndex) {
                sb.append("\n")
            }
        }
        UtilsBridge.writeFileFromString(contentFile.path, sb.toString(), false)
    }

    private fun write(content: String) {
        val headerFile = File(PARENT, "/header.txt")
        val headerParentExist = headerFile.parentFile?.exists() ?: false
        if (!headerParentExist) {
            headerFile.parentFile?.mkdirs()
        }
        val header = Header(total = total.get(), part = partIndex.get())
        UtilsBridge.writeFileFromString(headerFile.path, gson.toJson(header), false)

        val contentFile = File(PARENT, "/content.txt")
        val contentParentExist = contentFile.parentFile?.exists() ?: false
        if (!contentParentExist) {
            contentFile.parentFile?.mkdirs()
        }
        UtilsBridge.writeFileFromString(contentFile.path, content, false)
    }
}

/**
 * Please check
 */
class ExistedStringElement : Element<String>() {
    override val total: AtomicInteger
    override val partIndex: AtomicInteger
    private val headerFile: File = checkNotNull(HEADER)
    private val contentFile: File = checkNotNull(CONTENT)

    init {
        total = try {
            val header = gson.fromJson<Header>(
                headerFile.readText(),
                object : TypeToken<Header>() {}.type
            )
            AtomicInteger(header.total)
        } catch (e: Exception) {
            AtomicInteger()
        }

        partIndex = try {
            val header = gson.fromJson<Header>(
                headerFile.readText(),
                object : TypeToken<Header>() {}.type
            )
            AtomicInteger(header.part)
        } catch (e: Exception) {
            AtomicInteger()
        }
    }

    override fun getAsList(): List<String> = contentFile.readLines()

    override fun next(): String {
        if (!hasNext()) {
            throw ArrayIndexOutOfBoundsException("Element don't have next")
        }

        partIndex.getAndIncrement()
        val internalList = getAsList().toMutableList()
        val first: String = internalList.removeFirst()
        save(internalList)
        return first
    }

    override fun save(element: List<String>) {
        val header = Header(total = total.get(), part = partIndex.get())
        UtilsBridge.writeFileFromString(headerFile.path, gson.toJson(header), false)

        val sb = StringBuilder()
        for (i in element.indices) {
            sb.append(element[i])
            if (i != element.lastIndex) {
                sb.append("\n")
            }
        }
        UtilsBridge.writeFileFromString(contentFile.path, sb.toString(), false)
    }
}