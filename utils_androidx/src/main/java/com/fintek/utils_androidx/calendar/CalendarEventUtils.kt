package com.fintek.utils_androidx.calendar

import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils

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
    ): List<T> {
        if (calendarStructHandler.queryColumns().isEmpty()) return emptyList()

        val contentResolver = FintekUtils.requiredContext.contentResolver
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(Uri.parse("content://com.android.calendar/events"),
                null,
                null,
                null,
                null) ?: return emptyList()

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
            return calendarStructList
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor?.close()
        }

        return emptyList()
    }
}