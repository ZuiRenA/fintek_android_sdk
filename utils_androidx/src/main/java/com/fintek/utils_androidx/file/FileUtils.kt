package com.fintek.utils_androidx.file

import java.io.File
import java.io.IOException

/**
 * Created by ChaoShen on 2020/11/12
 */
object FileUtils {

    /**
     * Create a directory if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsDir(file: File?): Boolean {
        return file != null && if (file.exists()) file.isDirectory else file.mkdirs()
    }


    /**
     * Return the file by path.
     *
     * @param filePath The path of file.
     * @return the file
     */
    @JvmStatic
    fun getFileByPath(filePath: String?): File? {
        return if (filePath.isNullOrBlank()) null else File(filePath)
    }

    /**
     * Create a file if it doesn't exist, otherwise do nothing.
     *
     * @param file The file.
     * @return `true`: exists or creates successfully<br></br>`false`: otherwise
     */
    @JvmStatic
    fun createOrExistsFile(file: File?): Boolean {
        if (file == null) return false
        if (file.exists()) return file.isFile
        return if (!createOrExistsDir(file.parentFile)) false else try {
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
            false
        }
    }
}