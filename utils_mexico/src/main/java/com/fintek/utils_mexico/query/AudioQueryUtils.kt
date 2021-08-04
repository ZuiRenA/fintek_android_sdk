package com.fintek.utils_mexico.query

import android.provider.MediaStore
import com.fintek.utils_androidx.query.ContentQueryUtils
import com.fintek.utils_androidx.throwable.catchOrZero

/**
 * Created by ChaoShen on 2021/4/15
 */
object AudioQueryUtils {

    @JvmStatic
    fun getExternalAudioCount(): Int = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    @JvmStatic
    fun getInternalAudioCount(): Int = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        )
    }
}