package com.fintek.utils_mexico.date

import com.fintek.utils_androidx.date.DateUtils
import com.fintek.utils_mexico.ext.catchOrEmpty

/**
 * Created by ChaoShen on 2021/4/28
 */
object DateMexicoUtils {

    @JvmStatic
    fun getCurrentDateTime() = catchOrEmpty {
        DateUtils.getCurrentDateTime()
    }
}