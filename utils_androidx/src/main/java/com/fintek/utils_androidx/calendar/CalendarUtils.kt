package com.fintek.utils_androidx.calendar

import android.database.Cursor
import android.provider.CalendarContract
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.safely

/**
 * Created by ChaoShen on 2021/6/9
 */
object CalendarUtils {

    @JvmStatic
    fun <T> getCalendar(
        calendarStructHandler: ICalendarStruct<T>
    ): List<T>? {
        if (calendarStructHandler.queryColumns().isEmpty()) return null

        return safely {
            val contentResolver = FintekUtils.requiredContext.contentResolver
            val cursor: Cursor = contentResolver.query(
                CalendarContract.Calendars.CONTENT_URI,
                null,
                null,
                null,
                null
            ) ?: return null

            val calendarStructList = mutableListOf<T>()
            if (!cursor.moveToFirst()) {
                cursor.moveToFirst()
            }
            while (cursor.moveToNext()) {
                val sparseArrayCompat = SparseArrayCompat<Int>()
                calendarStructHandler.queryColumns().forEachIndexed { index, key ->
                    sparseArrayCompat.put(index, cursor.getColumnIndex(key))
                }
                val struct =
                    calendarStructHandler.structHandler(contentResolver, cursor, sparseArrayCompat)
                if (struct != null) {
                    calendarStructList.add(struct)
                }
            }
            cursor.close()
            calendarStructList
        }
    }
}