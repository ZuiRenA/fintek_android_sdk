package com.fintek.utils_androidx.image

import android.Manifest
import android.annotation.TargetApi
import android.graphics.Bitmap
import android.media.ExifInterface
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.Nullable
import androidx.annotation.RequiresPermission
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.model.ImageInfo
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
     * @receiver [String] file path
     * @throws IOException if an I/O error occurs while retrieving file descriptor via
     *         [java.io.FileInputStream#getFD()].
     * @return [android.media.ExifInterface]
     */
    @Nullable
    @Throws(IOException::class)
    @JvmStatic
    fun String.getExifInterface(): ExifInterface? = try {
        ExifInterface(this)
    } catch (e: Exception) {
        // use try catch to ignore error log in console
        null
    }


    /**
     * Get image info by file path
     *
     * this function have [ImageInfo.size]
     * @param path file path
     * @return [ImageInfo]
     */
    @JvmStatic
    fun getImageParams(path: String) = path.getExifInterface()?.getImageParams(
        ImageDefaultParamsHandler(filePath = path)
    )

    /**
     * Get image info @see [ImageInfo]
     *
     * this function will not have [ImageInfo.size], if you need size please use [getImageParams]
     *
     * @receiver [ExifInterface]
     * @return [ImageInfo]
     */
    @JvmStatic
    fun ExifInterface.getImageParams(): ImageInfo = getImageParams(ImageDefaultParamsHandler())

    /**
     * Get image info Custom
     * @return [T] is custom struct
     */
    @JvmStatic
    fun <T> ExifInterface.getImageParams(
        paramsStructHandler: IImageStruct<T>
    ): T {
        val sparseArrayCompat = SparseArrayCompat<String>()
        val params = paramsStructHandler.queryAttributeParams()
        if (params.isEmpty()) {
            //please handle empty status in your IImageStruct
            return paramsStructHandler.structHandler(sparseArrayCompat)
        }

        params.forEachIndexed { index, param ->
            sparseArrayCompat.put(index, getAttribute(param))
        }

        return paramsStructHandler.structHandler(sparseArrayCompat)
    }


    /**
     * Get file size by path
     *
     * @receiver [String] file path
     * @return byte
     */
    @JvmStatic
    fun String.getSize(): Long = File(this).length()

    /**
     * Get bitmap size
     *
     * @receiver [Bitmap] bitmap
     * @return byte
     */
    @JvmStatic
    fun Bitmap.getSize(): Long = this.byteCount.toLong()
}
