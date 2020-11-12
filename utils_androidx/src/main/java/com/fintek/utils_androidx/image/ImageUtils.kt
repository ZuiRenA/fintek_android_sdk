package com.fintek.utils_androidx.image

import android.Manifest
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.FintekUtils
import java.io.File
import java.io.IOException

/**
 * Created by ChaoShen on 2020/11/4
 */
object ImageUtils {


    /**
     * get all image
     * @throws IllegalArgumentException throw when getColumnIndexOrThrow
     * @return file path list
     */
    @TargetApi(Build.VERSION_CODES.R)
    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    @JvmStatic
    @Throws(IllegalArgumentException::class)
    fun getImageList(): List<String> {
        // the image uri
        val uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        val contentResolver = FintekUtils.requiredContext.contentResolver
        //get image cursor
        val cursor = contentResolver.query(uri, null, null, null, null)
        if (cursor == null || cursor.count <= 0) return emptyList()

        val photoList = mutableListOf<String>()
        //start get image path
        while (cursor.moveToNext()) {
            val index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val path = cursor.getString(index)
            val photoFile = File(path)

            if (photoFile.exists()) {
                photoList.add(path)
            }
        }
        cursor.close()
        return photoList
    }

    /**
     * provide extension method to get ExifInterface
     *
     * [String] file path
     * @throws IOException if an I/O error occurs while retrieving file descriptor via
     *         [java.io.FileInputStream#getFD()].
     * @return [android.media.ExifInterface]
     */
    @Throws(IOException::class)
    @JvmStatic
    fun String.getExifInterface(): ExifInterface = ExifInterface(this)

    /**
     * Default will get GPS info and DATETIME, If you need other params please put other TAG in
     * [android.media.ExifInterface]
     * @param params TAG in [android.media.ExifInterface]
     * @return params map if you need get value please use TAG in [params]
     */
    @JvmStatic
    @JvmOverloads
    fun ExifInterface.getImageParams(vararg params: String = arrayOf(
            ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LATITUDE_REF,
            ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LONGITUDE_REF,
            ExifInterface.TAG_DATETIME
    )): Map<String, String?> {
        if (params.isEmpty()) return emptyMap()

        val paramsMap: HashMap<String, String?> = HashMap(8)

        params.forEach {
            val attribute = getAttribute(it)
            paramsMap[it] = attribute
        }

        return paramsMap
    }


    /**
     * Get file size by path
     *
     * [String] file path
     * @return byte
     */
    @JvmStatic
    fun String.getSize(): Long = File(this).length()

    /**
     * Get bitmap size
     *
     * [Bitmap] bitmap
     * @return byte
     */
    @JvmStatic
    fun Bitmap.getSize(): Long = this.byteCount.toLong()
}
