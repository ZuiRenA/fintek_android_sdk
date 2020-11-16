package com.fintek.utils_androidx.call

import android.content.ContentResolver
import android.database.Cursor
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.model.CallLog
import android.provider.CallLog as AndroidCallLog

/**
 * Created by ChaoShen on 2020/11/16
 */
class CallDefaultStructHandler : ICallStruct<CallLog> {
    override fun queryColumns(): Array<String> = arrayOf(
        AndroidCallLog.Calls.CACHED_NAME, AndroidCallLog.Calls.NUMBER,
        AndroidCallLog.Calls.TYPE, AndroidCallLog.Calls.DATE,
        AndroidCallLog.Calls.DURATION
    )

    override fun structHandler(
        contentResolver: ContentResolver,
        cursor: Cursor,
        columns: SparseArrayCompat<Int>
    ): CallLog? {
        if (columns.isEmpty) return null

        val callLog = CallLog()
        callLog.apply {
            name = cursor.getString(AndroidCallLog.Calls.CACHED_NAME.columnIndex())
            phone = cursor.getString(AndroidCallLog.Calls.NUMBER.columnIndex())
            type = cursor.getType(AndroidCallLog.Calls.TYPE.columnIndex())
            time = cursor.getLong(AndroidCallLog.Calls.DATE.columnIndex())
            duration = cursor.getInt(AndroidCallLog.Calls.DURATION.columnIndex())
        }

        return callLog
    }
}