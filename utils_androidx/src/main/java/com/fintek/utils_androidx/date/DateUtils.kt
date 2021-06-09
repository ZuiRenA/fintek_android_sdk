package com.fintek.utils_androidx.date

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by ChaoShen on 2020/12/29
 */
object DateUtils {

    @SuppressLint("SimpleDateFormat")
    private val DEFAULT_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

    @JvmOverloads
    @JvmStatic
    fun getCurrentDateTime(
        format: SimpleDateFormat = DEFAULT_FORMAT,
        timeZone: TimeZone = TimeZone.getDefault()
    ): String {
        val date = Calendar.getInstance()
        format.timeZone = timeZone
        return format.format(date.time)
    }
}