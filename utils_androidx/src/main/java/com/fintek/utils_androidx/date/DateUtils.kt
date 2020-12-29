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
    fun getCurrentDateTime(format: SimpleDateFormat = DEFAULT_FORMAT): String {
        val date = Date()
        return format.format(date)
    }
}