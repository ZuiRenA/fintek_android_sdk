package com.fintek.utils_androidx.file

import android.util.Log
import com.fintek.utils_androidx.UtilsBridge
import java.io.*
import java.nio.charset.Charset

/**
 * Created by ChaoShen on 2020/11/12
 */
object FileIOUtils {

    /**
     * Write file from string.
     *
     * @param filePath The path of file.
     * @param content  The string of content.
     * @param append   True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmStatic
    @JvmOverloads
    fun writeFileFromString(
        filePath: String?,
        content: String?,
        append: Boolean = false,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        return writeFileFromString(UtilsBridge.getFileByPath(filePath), content, append, charset)
    }

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @param append  True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    @JvmOverloads
    @JvmStatic
    fun writeFileFromString(
        file: File?,
        content: String?,
        append: Boolean,
        charset: Charset = Charsets.UTF_8
    ): Boolean {
        if (file == null || content == null) return false
        if (!UtilsBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var bw: BufferedWriter? = null
        return try {
            bw = BufferedWriter(OutputStreamWriter(FileOutputStream(file, append), charset))
            bw.write(content)
            true
        } catch (e: IOException) {
            e.printStackTrace()
            false
        } finally {
            try {
                bw?.close()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
}