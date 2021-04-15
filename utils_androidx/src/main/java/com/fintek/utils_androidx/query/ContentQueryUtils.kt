package com.fintek.utils_androidx.query

import android.database.Cursor
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2021/4/15
 */
object ContentQueryUtils {

    @JvmStatic
    fun countQuery(uri: Uri, vararg queryString: String = emptyArray()): Int {
        val contentResolver = FintekUtils.requiredContext.contentResolver
        val cursor: Cursor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            contentResolver.query(uri, queryString, null, null) ?: return 0
        } else {
            contentResolver.query(uri, queryString, null, null, null) ?: return 0
        }
        if (cursor.moveToFirst()) {
            cursor.moveToFirst()
        }
        var count = 0
        while (cursor.moveToNext()) {
            count ++
        }
        cursor.close()
        return count
    }
}