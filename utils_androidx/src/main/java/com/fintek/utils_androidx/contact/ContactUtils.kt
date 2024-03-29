package com.fintek.utils_androidx.contact

import android.Manifest
import android.database.Cursor
import android.os.Build
import android.provider.ContactsContract
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.call.CallDefaultStructHandler
import com.fintek.utils_androidx.model.Contact
import com.fintek.utils_androidx.throwable.safely

/**
 * Created by ChaoShen on 2020/11/16
 */
object ContactUtils {

    @JvmStatic
    @JvmOverloads
    @RequiresApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    fun getContacts(
        isDeduplication: Boolean = false
    ): List<Contact>? = getContacts(ContactDefaultStructHandler(isDeduplication))


    @JvmStatic
    @RequiresPermission(Manifest.permission.READ_CONTACTS)
    fun <T> getContacts(
        contactStructHandler: IContactStruct<T>,
    ): List<T>? {
        if (contactStructHandler.queryColumns().isEmpty()) return null
        return safely {
            val contentResolver = FintekUtils.requiredContext.contentResolver
            val cursor: Cursor = contentResolver.query(
                ContactsContract.Contacts.CONTENT_URI,
                null,
                null,
                null,
                null
            ) ?: return null

            val contactStructList = mutableListOf<T>()

            while (cursor.moveToNext()) {
                val sparseArrayCompat = SparseArrayCompat<Int>()

                contactStructHandler.queryColumns().forEachIndexed { index, key ->
                    sparseArrayCompat.put(index, cursor.getColumnIndex(key))
                }

                val struct = contactStructHandler.structHandler(contentResolver, cursor, sparseArrayCompat)
                if (struct != null) {
                    contactStructList.add(struct)
                }
            }

            cursor.close()
            contactStructList
        }
    }
}