package com.fintek.utils_androidx.sms

import android.Manifest
import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.common.QueryOrder
import com.fintek.utils_androidx.common.SmsQueryOrder
import com.fintek.utils_androidx.model.Sms

object SmsUtils {
    /**
     * The sms uriString, used in [android.content.ContentResolver]
     */
    private const val SMS_URI = "content://sms/"

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    private val DEFAULT = SmsDefaultStructHandler()

    /**
     * get all sms info
     *
     * @param sortOrder cursor sort desc or asc
     * @return sms list, it struct see [Sms]
     */
    @JvmStatic
    @JvmOverloads
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @RequiresPermission(anyOf = [Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS])
    fun getAllSms(
        sortOrder: QueryOrder = SmsQueryOrder.DateDESC,
    ): List<Sms> = getAllSms(sortOrder, DEFAULT)

    /**
     * get all sms info
     *
     * @param sortOrder cursor sort desc or asc
     * @param projection sms struct handler example see [SmsDefaultStructHandler]
     * @return struct list
     */
    @JvmStatic
    @JvmOverloads
    @RequiresApi(Build.VERSION_CODES.KITKAT)
    @RequiresPermission(anyOf = [Manifest.permission.READ_SMS])
    fun <T> getAllSms(
        sortOrder: QueryOrder = SmsQueryOrder.DateDESC,
        projection: ISmsStruct<T>,
    ): List<T> {
        var cursor: Cursor? = null
        try {
            val smsStructList = mutableListOf<T>()
            val contentResolver = FintekUtils.requiredContext.contentResolver
            val uri = Uri.parse(SMS_URI)
            cursor = contentResolver.query(uri, projection.projection(),
                null, null, sortOrder.toSortOrder) ?: return emptyList()

            while (cursor.moveToNext()) {
                val sparseArrayCompat = SparseArrayCompat<Int>()
                projection.projection().forEachIndexed { index, columnName ->
                    sparseArrayCompat.put(index, cursor.getColumnIndex(columnName))
                }

                val t = projection.structHandler(sparseArrayCompat, cursor, contentResolver)
                if (t != null) {
                    smsStructList.add(t)
                }
            }
            return smsStructList
        } catch (t: Throwable) {
            t.printStackTrace()
            return emptyList()
        } finally {
            cursor?.close()
        }
    }
}


