package com.fintek.utils_androidx.file

import android.util.Log
import com.fintek.utils_androidx.UtilsBridge
import java.io.BufferedWriter
import java.io.File
import java.io.FileWriter
import java.io.IOException

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
        append: Boolean = false
    ): Boolean {
        return writeFileFromString(UtilsBridge.getFileByPath(filePath), content, append)
    }

    /**
     * Write file from string.
     *
     * @param file    The file.
     * @param content The string of content.
     * @param append  True to append, false otherwise.
     * @return `true`: success<br></br>`false`: fail
     */
    fun writeFileFromString(
        file: File?,
        content: String?,
        append: Boolean
    ): Boolean {
        if (file == null || content == null) return false
        if (!UtilsBridge.createOrExistsFile(file)) {
            Log.e("FileIOUtils", "create file <$file> failed.")
            return false
        }
        var bw: BufferedWriter? = null
        return try {
            bw = BufferedWriter(FileWriter(file, append))
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