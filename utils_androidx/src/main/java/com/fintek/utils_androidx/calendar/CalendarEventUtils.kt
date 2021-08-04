package com.fintek.utils_androidx.calendar

import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.safely
import com.fintek.utils_androidx.throwable.safelyVoid

/**
 * Created by ChaoShen on 2021/4/16
 */
object CalendarEventUtils {
    private val DEFAULT = CalendarDefaultStructHandler()

    @JvmStatic
    fun getCalendar() = getCalendar(DEFAULT)

    @JvmStatic
    fun <T> getCalendar(
        calendarStructHandler: ICalendarStruct<T>
    ): List<T>? {
        if (calendarStructHandler.queryColumns().isEmpty()) return null
        return safely {
            val contentResolver = FintekUtils.requiredContext.contentResolver
            val cursor: Cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI,
                null,
                null,
                null,
                null) ?: return null

            val calendarStructList = mutableListOf<T>()
            while (cursor.moveToNext()) {
                val sparseArrayCompat = SparseArrayCompat<Int>()
                calendarStructHandler.queryColumns().forEachIndexed { index, key ->
                    sparseArrayCompat.put(index, cursor.getColumnIndex(key))
                }
                val struct = calendarStructHandler.structHandler(contentResolver, cursor, sparseArrayCompat)
                if (struct != null) {
                    calendarStructList.add(struct)
                }
            }
            cursor.close()
            calendarStructList
        }
    }
}