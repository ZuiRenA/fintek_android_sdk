package com.fintek.utils_androidx.contact

import android.content.ContentResolver
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.common.ext.getAssertNotNull
import com.fintek.utils_androidx.model.Contact

/**
 * Created by ChaoShen on 2020/11/16
 */
@RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
class ContactDefaultStructHandler(
    private val isDeduplication: Boolean
) : IContactStruct<Contact> {

    override fun queryColumns(): Array<String> = arrayOf(
        ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME,
        ContactsContract.Contacts.LAST_TIME_CONTACTED, ContactsContract.Contacts.SEND_TO_VOICEMAIL,
        ContactsContract.Contacts.IN_VISIBLE_GROUP, ContactsContract.Contacts.STARRED,
        ContactsContract.Contacts.IS_USER_PROFILE, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP,
        ContactsContract.Contacts.TIMES_CONTACTED, ContactsContract.Contacts.HAS_PHONE_NUMBER
    )

    override fun structHandler(
        contentResolver: ContentResolver,
        cursor: Cursor,
        column: SparseArrayCompat<Int>
    ): Contact? {
        if (column.isEmpty) return null

        val contact = Contact()

        contact.apply {
            id = cursor.getString(column
                .getAssertNotNull(ContactsContract.Contacts._ID.columnIndex()))
            name = cursor.getString(column
                .getAssertNotNull(ContactsContract.Contacts.DISPLAY_NAME.columnIndex()))
            hasPhoneNumber = cursor.getInt(column
                .getAssertNotNull(ContactsContract.Contacts.HAS_PHONE_NUMBER.columnIndex()))
            inVisibleGroup = cursor.getInt(column
                .getAssertNotNull(ContactsContract.Contacts.IN_VISIBLE_GROUP.columnIndex()))
            isUserProfile = cursor.getString(column
                .getAssertNotNull(ContactsContract.Contacts.IS_USER_PROFILE.columnIndex()))
            lastTimeContacted = cursor.getInt(column
                .getAssertNotNull(ContactsContract.Contacts.LAST_TIME_CONTACTED.columnIndex()))
            sendToVoiceMail = cursor.getInt(column
                .getAssertNotNull(ContactsContract.Contacts.SEND_TO_VOICEMAIL.columnIndex()))
            starred = cursor.getInt(column
                .getAssertNotNull(ContactsContract.Contacts.STARRED.columnIndex()))
            timesContacted = cursor.getInt(column
                .getAssertNotNull(ContactsContract.Contacts.TIMES_CONTACTED.columnIndex()))
            upTime = cursor.getString(column
                .getAssertNotNull(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP.columnIndex()))
        }

        if (contact.hasPhoneNumber > 0) {  // is't empty contact
            val phoneCursor = contentResolver.query(
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null,
                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "=" + contact.id,
                null,
                null
            ) ?: return contact

            val phoneList = mutableListOf<String>()
            //get all phone
            while (phoneCursor.moveToNext()) {
                val phoneColumn = phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)
                val phone = phoneCursor.getString(phoneColumn)
                if (isDeduplication && !phoneList.contains(phone)) {
                    phoneList.add(phone)
                } else {
                    phoneList.add(phone)
                }
            }
            contact.phone = phoneList
            phoneCursor.close()
        }

        return contact
    }
}