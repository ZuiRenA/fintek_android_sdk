package com.fintek.utils_mexico.albs

import android.Manifest
import androidx.annotation.RequiresPermission
import com.fintek.utils_androidx.image.ImageUtils
import com.fintek.utils_androidx.model.ImageInfo
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
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
    private fun getImageList(): List<ImageInfo> {
        val imagePathList = ImageUtils.getImageList()
        return imagePathList.mapNotNull { ImageUtils.getImageParams(it) }
    }
}