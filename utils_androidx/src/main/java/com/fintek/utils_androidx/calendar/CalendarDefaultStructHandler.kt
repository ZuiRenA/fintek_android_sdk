package com.fintek.utils_androidx.calendar

import android.content.ContentResolver
import android.database.Cursor
import android.provider.CalendarContract
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.model.Calendar
import com.fintek.utils_androidx.model.Reminder

/**
 * Created by ChaoShen on 2021/4/16
 */
class CalendarDefaultStructHandler : ICalendarStruct<Calendar> {
    override fun queryColumns(): Array<String> = arrayOf(
        CalendarContract.Events.TITLE,
        CalendarContract.Events._ID,
        CalendarContract.Events.DTEND,
        CalendarContract.Events.DTSTART,
        CalendarContract.Events.DESCRIPTION
    )

    override fun structHandler(
        contentResolver: ContentResolver,
        cursor: Cursor,
        columns: SparseArrayCompat<Int>
    ): Calendar {
        val id = cursor.getInt(columns.getAssertNotNull(CalendarContract.Events._ID.columnIndex()))

        val reminders = mutableListOf<Reminder>()
        val cursorReminders = contentResolver.query( CalendarContract.Reminders.CONTENT_URI,
            null,
            CalendarContract.Reminders.EVENT_ID + "=" + id
            , null, null
        )

        while (cursorReminders?.moveToNext() == true) {
            val method = cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.METHOD))
            val minutes = cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders.MINUTES))
            val reminderId = cursor.getInt(cursor.getColumnIndex(CalendarContract.Reminders._ID))

            reminders.add(Reminder(
                eventId = id,
                method = method,
                minutes = minutes,
                reminderId = reminderId
            ))
        }

        return Calendar(
            eventTitle = cursor.getString(columns.getAssertNotNull(CalendarContract.Events.TITLE.columnIndex())),
            eventId = id,
            endTime = cursor.getLong(columns.getAssertNotNull(CalendarContract.Events.DTEND.columnIndex())),
            startTime = cursor.getLong(columns.getAssertNotNull(CalendarContract.Events.DTSTART.columnIndex())),
            des = cursor.getString(columns.getAssertNotNull(CalendarContract.Events.DESCRIPTION.columnIndex())),
            reminders = reminders
        )
    }
}