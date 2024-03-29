package com.fintek.utils_mexico.structHandler

import android.Manifest
import android.content.ContentResolver
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.provider.CalendarContract
import androidx.collection.SparseArrayCompat
import androidx.core.app.ActivityCompat
import com.fintek.utils_androidx.calendar.ICalendarStruct
import com.fintek.utils_mexico.model.Calendar
import com.fintek.utils_mexico.model.Reminder


/**
 * Created by ChaoShen on 2021/4/16
 */
class CalendarMexicoStructHandler : ICalendarStruct<Calendar> {

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
        val cursorReminders = contentResolver.query(
            CalendarContract.Reminders.CONTENT_URI,
            arrayOf(
                CalendarContract.Reminders._ID,
                CalendarContract.Reminders.EVENT_ID,
                CalendarContract.Reminders.MINUTES,
                CalendarContract.Reminders.METHOD
            ),
            CalendarContract.Reminders.EVENT_ID + "=?",
            arrayOf("$id"), null
        )

        if (cursorReminders != null) {
            while (cursorReminders.moveToNext()) {
                val method =
                    cursorReminders.getInt(cursorReminders.getColumnIndex(CalendarContract.Reminders.METHOD))
                val minutes =
                    cursorReminders.getInt(cursorReminders.getColumnIndex(CalendarContract.Reminders.MINUTES))
                val eventId =
                    cursorReminders.getInt(cursorReminders.getColumnIndex(CalendarContract.Reminders.EVENT_ID))
                val reminderId =
                    cursorReminders.getInt(cursorReminders.getColumnIndex(CalendarContract.Reminders._ID))

                reminders.add(
                    Reminder(
                        eventId = eventId,
                        method = method,
                        minutes = minutes,
                        reminderId = reminderId
                    )
                )
            }
        }
        cursorReminders?.close()


        return Calendar(
            eventTitle = cursor.getString(columns.getAssertNotNull(CalendarContract.Events.TITLE.columnIndex())).orEmpty(),
            eventId = id.toLong(),
            endTime = cursor.getLong(columns.getAssertNotNull(CalendarContract.Events.DTEND.columnIndex())),
            startTime = cursor.getLong(columns.getAssertNotNull(CalendarContract.Events.DTSTART.columnIndex())),
            des = cursor.getString(columns.getAssertNotNull(CalendarContract.Events.DESCRIPTION.columnIndex())).orEmpty(),
            reminders = reminders
        )
    }
}