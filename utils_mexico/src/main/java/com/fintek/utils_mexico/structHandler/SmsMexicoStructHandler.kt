package com.fintek.utils_mexico.structHandler

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Telephony
import androidx.annotation.RequiresApi
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.sms.ISmsStruct
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.model.Sms

/**
 * Created by ChaoShen on 2021/4/16
 */
@RequiresApi(Build.VERSION_CODES.KITKAT)
class SmsMexicoStructHandler : ISmsStruct<Sms> {
    private fun String.projectionIndex(): Int = projection().indexOf(this)

    override fun projection(): Array<String> = arrayOf(
        Telephony.Sms.ADDRESS,
        Telephony.Sms.PERSON,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE,
        Telephony.Sms.TYPE,
        Telephony.Sms.SEEN,
        Telephony.Sms.READ,
        Telephony.Sms.SUBJECT,
        Telephony.Sms.STATUS
    )

    override fun structHandler(
        columnIndex: SparseArrayCompat<Int>,
        cursor: Cursor,
        contentResolver: ContentResolver
    ): Sms? {
        val phoneNumber = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.ADDRESS.projectionIndex()))
        val body = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.BODY.projectionIndex()))
        val date = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.DATE.projectionIndex()))
            .toLong()
        val seen = cursor.getInt(columnIndex.getAssertNotNull(Telephony.Sms.SEEN.projectionIndex()))
        val read = cursor.getInt(columnIndex.getAssertNotNull(Telephony.Sms.READ.projectionIndex()))
        val subject = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.SUBJECT.projectionIndex())).orEmpty()
        val status = when(cursor.getInt(columnIndex.getAssertNotNull(Telephony.Sms.STATUS.projectionIndex()))) {
            Telephony.Sms.STATUS_NONE -> -1
            Telephony.Sms.STATUS_COMPLETE -> 0
            Telephony.Sms.STATUS_PENDING -> 64
            Telephony.Sms.STATUS_FAILED -> 128
            else -> -1
        }

        val type: Int = when(cursor.getInt(columnIndex.getAssertNotNull(Telephony.Sms.TYPE.projectionIndex()))) {
            1 -> 1
            else -> 2
        }

        return Sms(
            otherPhone = phoneNumber,
            content = body,
            seen = seen,
            read = read,
            subject = subject,
            status = status,
            time = date,
            type = type,
            packageName = FintekMexicoUtils.requiredApplication.packageName
        )
    }

    private fun <E> SparseArrayCompat<E>.getAssertNotNull(index: Int): E {
        val e = this.get(index)
        return checkNotNull(e)
    }
}