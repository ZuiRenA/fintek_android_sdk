package com.fintek.utils_mexico.query

import android.provider.ContactsContract
import com.fintek.utils_androidx.query.ContentQueryUtils
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.ext.catchOrZero

/**
 * Created by ChaoShen on 2021/4/16
 */
object ContactQueryUtils {

    @JvmStatic
    fun getContactGroupCount(): Int {
        return catchOrZero {
            val cursor = FintekMexicoUtils.requiredApplication.contentResolver.query(
                ContactsContract.Data.CONTENT_URI,
                arrayOf(
                    ContactsContract.Groups._ID,
                ), null, null, null
            ) ?: return@catchOrZero 0

            val groupSet: MutableSet<Int> = mutableSetOf()
            while (cursor.moveToNext()) {
                val groupIdColumn = cursor.getColumnIndex(ContactsContract.Groups._ID)
                val id = cursor.getInt(groupIdColumn)
                groupSet.add(id)
            }
            return@catchOrZero groupSet.size
        }
    }
}