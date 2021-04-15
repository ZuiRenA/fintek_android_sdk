package com.fintek.utils_mexico.query

import android.provider.MediaStore
import com.fintek.utils_androidx.query.ContentQueryUtils
import com.fintek.utils_mexico.ext.catchOrZero

object VideoQueryUtils {

    @JvmStatic
    fun getExternalVideoCount() = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        )
    }

    @JvmStatic
    fun getInternalVideoCount() = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Video.Media.INTERNAL_CONTENT_URI
        )
    }
}