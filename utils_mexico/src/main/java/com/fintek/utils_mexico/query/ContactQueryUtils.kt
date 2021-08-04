package com.fintek.utils_mexico.query

import android.provider.ContactsContract
import com.fintek.utils_androidx.throwable.catchOrZero
import com.fintek.utils_mexico.FintekMexicoUtils
/**
 * Created by ChaoShen on 2021/4/16
 */
object ContactQueryUtils {

    @JvmStatic
    fun getContactGroupCount(): Int {
        return catchOrZero {
            val cursor = FintekMexicoUtils.requiredApplication.contentResolver.query(
                ContactsContract.Groups.CONTENT_URI,
                null, null, null, null
            ) ?: return@catchOrZero 0

            val groupSet: MutableSet<String> = mutableSetOf()
            while (cursor.moveToNext()) {
                val groupIdColumn = cursor.getColumnIndex(ContactsContract.Groups._ID)
                val groupTitleColumn = cursor.getColumnIndex(ContactsContract.Groups.TITLE)
                val id = cursor.getInt(groupIdColumn)
                val title = cursor.getString(groupTitleColumn)
                groupSet.add(title)
            }
            cursor.close()
            return@catchOrZero groupSet.size
        }
    }
}