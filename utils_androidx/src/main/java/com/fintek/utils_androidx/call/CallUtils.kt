package com.fintek.utils_androidx.call

import android.Manifest
import android.database.Cursor
import android.provider.CallLog
import androidx.annotation.RequiresPermission
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.common.CallQueryOrder
import com.fintek.utils_androidx.common.QueryOrder

/**
 * Created by ChaoShen on 2020/11/16
 */
object CallUtils {
    private val DEFAULT = CallDefaultStructHandler()


    /**
     * Get all call log, struct @see [com.fintek.utils_androidx.model.CallLog]
     *
     * @param sortOrder sorted by desc or asc
     * @return callLogList
     */
    @JvmStatic
    @JvmOverloads
    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    fun getCalls(sortOrder: QueryOrder = CallQueryOrder.DateDESC) = getCalls(
        sortOrder = sortOrder,
        callStructHandler = DEFAULT
    )

    /**
     * Get all call log, struct [T]
     *
     * @param sortOrder sorted by desc or asc
     * @param callStructHandler custom struct handler
     * @return struct TList
     */
    @JvmStatic
    @JvmOverloads
    @RequiresPermission(Manifest.permission.READ_CALL_LOG)
    fun <T> getCalls(
        sortOrder: QueryOrder = CallQueryOrder.DateDESC,
        callStructHandler: ICallStruct<T>
    ): List<T> {
        if (callStructHandler.queryColumns().isEmpty()) return emptyList()

        val contentResolver = FintekUtils.requiredContext.contentResolver
        var cursor: Cursor? = null
        try {
            cursor = contentResolver.query(
                CallLog.Calls.CONTENT_URI,
                null,
                null,
                null,
                sortOrder.toSortOrder
            ) ?: return emptyList()

            val callStructList = mutableListOf<T>()

            while (cursor.moveToNext()) {
                val sparseArrayCompat = SparseArrayCompat<Int>()
                callStructHandler.queryColumns().forEachIndexed { index, key ->
                    sparseArrayCompat.put(index, cursor.getColumnIndex(key))
                }
                val struct = callStructHandler.structHandler(contentResolver, cursor, sparseArrayCompat)
                if (struct != null) {
                    callStructList.add(struct)
                }
            }

            return callStructList
        } catch (e: Throwable) {
            e.printStackTrace()
            return emptyList()
        } finally {
            cursor?.close()
        }
    }
}