package com.fintek.utils_mexico.date

import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.date.DateUtils
import com.fintek.utils_mexico.ext.catchOrEmpty
import java.time.ZoneId
import java.time.temporal.Temporal
import java.time.temporal.TemporalAccessor
import java.util.*

/**
 * Created by ChaoShen on 2021/4/28
 */
object DateMexicoUtils {

    @JvmStatic
    fun getCurrentDateTime() = catchOrEmpty {
        DateUtils.getCurrentDateTime(timeZone = TimeZone.getTimeZone("GMT-5"))
    }
}