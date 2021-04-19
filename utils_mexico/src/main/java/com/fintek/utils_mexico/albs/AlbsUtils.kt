package com.fintek.utils_mexico.albs

import android.Manifest
import androidx.annotation.RequiresPermission
import androidx.exifinterface.media.ExifInterface
import com.fintek.utils_androidx.image.ImageUtils
import com.fintek.utils_androidx.image.ImageUtils.getExifInterface
import com.fintek.utils_androidx.image.ImageUtils.getImageParams
import com.fintek.utils_mexico.model.ImageInfo
import com.fintek.utils_mexico.structHandler.ImageMexicoStructHandler
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import java.io.File
import java.lang.reflect.ParameterizedType

/**
 * Created by ChaoShen on 2021/4/16
 */
object AlbsUtils {
    private val moshi by lazy { Moshi.Builder().build() }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun getAlbs(): String {
        val imageList = getImageList()
        val type: ParameterizedType = Types.newParameterizedType(List::class.java, ImageInfo::class.java)
        return moshi.adapter<List<ImageInfo>>(type).toJson(imageList)
    }

    @RequiresPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
    fun getImageList(): List<ImageInfo> {
        return ImageUtils.getImageList().asSequence().mapNotNull {
            it.getExifInterface()?.getImageParams(ImageMexicoStructHandler(it))
        }.toList()
    }
}