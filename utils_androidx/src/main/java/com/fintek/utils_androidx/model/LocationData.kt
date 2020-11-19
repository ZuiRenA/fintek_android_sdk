package com.fintek.utils_androidx.model

import android.location.Location
import android.os.Parcelable
import androidx.annotation.IntRange

/**
 * Created by ChaoShen on 2020/11/19
 */
data class LocationData(
    @IntRange(from = INIT.toLong(), to = LOCATION_NULL.toLong())
    var locationType: Int = INIT,
    var location: Location? = null
) {

    companion object {
        const val INIT          = 0
        const val NETWORK       = 1
        const val GPS           = 2
        const val NATIVE_NULL   = 3
        const val LOCATION_NULL = 4
    }
}

