package com.fintek.utils_androidx.upload.internal

import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.ByteArrayInputStream
import java.io.File
import java.io.FileNotFoundException
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
    val file = File(PARENT, "/header")
    if (!file.exists()) {
        return null
    }
    return file
}



internal val CONTENT: File? get() {
    val file = File(PARENT, "/content")
    if (!file.exists()) {
        return null
    }
    return file
}

internal val CACHE: File? get() {
    val file = File(PARENT, "/cache")
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

internal val IS_UPLOADING: Boolean get() {
    if (HEADER == null && CACHE == null) return false
    val headerList: List<String>? = HEADER?.readLines()
    val cacheList: List<String>? = CACHE?.readLines()
    if (headerList.isNullOrEmpty() || cacheList.isNullOrEmpty()) return false
    return true
}

private val HEADER_PATH get() = HEADER?.path ?: File(PARENT, "/header").path
private val CONTENT_PATH get() = CONTENT?.path ?: File(PARENT, "/content").path
private val CACHE_PATH get() = CACHE?.path ?: File(PARENT, "/cache").path

internal fun elementDelete() {
    HEADER?.delete()
    CONTENT?.delete()
    CACHE?.delete()
}

private val gson = Gson()

internal data class Header(
    val total: Int,
    val part: Int,
)

internal class StringElement(
    rsa: String,
    limit: Int = 10 * 1024, // 100kb, Base unit is byte
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
            try {
                var endIndex = buffer.size
                // filter the first 0 in the buffer
                for (j in buffer.indices) {
                    if (buffer[j] == 0.toByte()) {
                        endIndex = j
                        break
                    }
                }
                sb.append(buffer.decodeToString(0, endIndex))
            } catch (e: Exception) {
                // ignore this, it's only catch convert to string error
            }
            Arrays.fill(buffer, 0.toByte())
            buffer.fill(0.toByte())
            sb.append("\n")
            position ++
        }
        sb.delete(sb.length - 1, sb.length)
        total = AtomicInteger(position)
        write(sb.toString())
    }

    override fun header(): Header {
        return Header(total = total.get(), part = partIndex.get())
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
        val internalList = getAsList()
        val firstElement = internalList.first()
        saveCache(firstElement)
        return firstElement
    }

    override fun hasNext(): Boolean {
        return super.hasNext() && IS_EXISTED
    }

    override fun remove(): String {
        val removedElement: String
        val internalList: MutableList<String> = getAsList().toMutableList()
        if (internalList.isNullOrEmpty()) {
            return ""
        }
        removedElement = internalList.removeFirst()
        save(internalList)
        return removedElement
    }

    override fun save(element: List<String>) {
        val headerFile = HEADER ?: File(PARENT, "/header")
        val contentFile = CONTENT ?: File(PARENT, "/content")

        val header = Header(total = total.get(), part = partIndex.get())
        UtilsBridge.writeFileFromString(headerFile.path, gson.toJson(header), false)

        val sb = StringBuilder()
        for (i in element.indices) {
            sb.append(element[i])
            if (i != element.lastIndex) {
                sb.append("\n")
            }
        }
        // if StringBuilder is Empty, need delete it
        if (sb.isEmpty()) {
            headerFile.delete()
            contentFile.delete()
        } else {
            UtilsBridge.writeFileFromString(contentFile.path, sb.toString(), false)
        }
    }

    override fun cache(): String? = CACHE?.readText()

    override fun saveCache(element: String): Boolean {
        val cacheFile = CACHE ?: File(PARENT, "/cache")
        return UtilsBridge.writeFileFromString(cacheFile.path, element, false)
    }

    override fun removeCache(): Boolean {
        if (CACHE == null) return false
        return CACHE?.delete() ?: false
    }

    private fun write(content: String) {
        val headerFile = File(PARENT, "/header")
        val headerParentExist = headerFile.parentFile?.exists() ?: false
        if (!headerParentExist) {
            headerFile.parentFile?.mkdirs()
        }
        val header = Header(total = total.get(), part = partIndex.get())
        UtilsBridge.writeFileFromString(headerFile.path, gson.toJson(header), false)

        val contentFile = File(PARENT, "/content")
        val contentParentExist = contentFile.parentFile?.exists() ?: false
        if (!contentParentExist) {
            contentFile.parentFile?.mkdirs()
        }
        UtilsBridge.writeFileFromString(contentFile.path, content, false)
    }
}

internal class ExistedStringElement : Element<String>() {
    override val total: AtomicInteger
    override val partIndex: AtomicInteger

    init {
        total = try {
            val header = gson.fromJson<Header>(
                HEADER?.readText(),
                object : TypeToken<Header>() {}.type
            )
            AtomicInteger(header.total)
        } catch (e: Exception) {
            AtomicInteger()
        }

        partIndex = try {
            val header = gson.fromJson<Header>(
                HEADER?.readText(),
                object : TypeToken<Header>() {}.type
            )
            AtomicInteger(header.part)
        } catch (e: Exception) {
            AtomicInteger()
        }
    }

    override fun header(): Header {
        return Header(total = total.get(), part = partIndex.get())
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
        val internalList = getAsList()
        val firstElement = internalList.first()
        saveCache(firstElement)
        return firstElement
    }

    override fun hasNext(): Boolean {
        return super.hasNext() && IS_EXISTED
    }

    override fun remove(): String {
        val removedElement: String
        val internalList: MutableList<String> = getAsList().toMutableList()
        if (internalList.isNullOrEmpty()) {
            return ""
        }
        removedElement = internalList.removeFirst()
        save(internalList)
        return removedElement
    }

    override fun save(element: List<String>) {
        val header = Header(total = total.get(), part = partIndex.get())
        UtilsBridge.writeFileFromString(HEADER_PATH, gson.toJson(header), false)

        val sb = StringBuilder()
        for (i in element.indices) {
            sb.append(element[i])
            if (i != element.lastIndex) {
                sb.append("\n")
            }
        }
        if (sb.isEmpty()) {
            HEADER?.delete()
            CONTENT?.delete()
        } else {
            UtilsBridge.writeFileFromString(CONTENT_PATH, sb.toString(), false)
        }
    }

    override fun cache(): String? = CACHE?.readText()

    override fun saveCache(element: String): Boolean {
        val cacheFile = CACHE ?: File(PARENT, "/cache")
        return UtilsBridge.writeFileFromString(cacheFile.path, element, false)
    }

    override fun removeCache(): Boolean {
        if (CACHE == null) return false
        return CACHE?.delete() ?: false
    }
}