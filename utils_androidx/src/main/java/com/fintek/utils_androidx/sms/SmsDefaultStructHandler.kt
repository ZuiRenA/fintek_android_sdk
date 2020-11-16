package com.fintek.utils_androidx.sms

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.provider.ContactsContract
import android.provider.Telephony
import androidx.annotation.RequiresApi
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.common.ext.getAssertNotNull
import com.fintek.utils_androidx.model.Sms
import java.util.*

/**
 * Created by ChaoShen on 2020/11/13
 */

@RequiresApi(Build.VERSION_CODES.KITKAT)
internal class SmsDefaultStructHandler : ISmsStruct<Sms> {

    private fun String.projectionIndex(): Int = projection().indexOf(this)

    override fun projection(): Array<String> = arrayOf(
        Telephony.Sms._ID,
        Telephony.Sms.ADDRESS,
        Telephony.Sms.PERSON,
        Telephony.Sms.BODY,
        Telephony.Sms.DATE,
        Telephony.Sms.TYPE
    )

    override fun structHandler(
        columnIndex: SparseArrayCompat<Int>,
        cursor: Cursor,
        contentResolver: ContentResolver
    ): Sms? {
        val sms = Sms()
        val nameId = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.PERSON.projectionIndex()))
        val phoneNumber = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.ADDRESS.projectionIndex()))
        val body = cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.BODY.projectionIndex()))
        val date = Date(
            cursor.getString(columnIndex.getAssertNotNull(Telephony.Sms.DATE.projectionIndex()))
                .toLong()
        )


        val personUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            phoneNumber
        )
        if (personUri != null && personUri.toString().contains("http")) {
            return null
        }

        val localCursor = contentResolver.query(
            personUri, arrayOf(
                ContactsContract.PhoneLookup.DISPLAY_NAME,
                ContactsContract.PhoneLookup.PHOTO_ID,
                ContactsContract.PhoneLookup._ID
            ),
            null, null, null
        ) ?: return null

        sms.apply {
            this.body = body
            phone = phoneNumber
            senderName = if (localCursor.count != 0) {
                localCursor.moveToFirst()
                localCursor.getString(localCursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME))
            } else {
                phoneNumber
            }

            localCursor.close()
            type = columnIndex.getAssertNotNull(Telephony.Sms.TYPE.projectionIndex())
            time = date.time.toString()

        }

        return sms
    }
}