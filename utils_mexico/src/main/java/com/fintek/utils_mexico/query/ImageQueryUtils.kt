package com.fintek.utils_mexico.query

import android.provider.MediaStore
import com.fintek.utils_androidx.query.ContentQueryUtils
import com.fintek.utils_mexico.ext.catchOrZero

/**
 * Created by ChaoShen on 2021/4/15
 */
object ImageQueryUtils {

    @JvmStatic
    fun getExternalImageCount() = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
    }

    @JvmStatic
    fun getInternalImageCount() = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Images.Media.INTERNAL_CONTENT_URI
        )
    }
}