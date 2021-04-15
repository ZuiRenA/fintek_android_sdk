package com.fintek.utils_mexico.query

import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.query.ContentQueryUtils
import com.fintek.utils_mexico.ext.catchOrZero

/**
 * Created by ChaoShen on 2021/4/15
 */
object AudioQueryUtils {

    @JvmStatic
    @RequiresApi(Build.VERSION_CODES.O)
    fun getExternalAudioCount(): Int = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        )
    }

    fun getInternalAudioCount(): Int = catchOrZero {
        ContentQueryUtils.countQuery(
            MediaStore.Audio.Media.INTERNAL_CONTENT_URI
        )
    }
}