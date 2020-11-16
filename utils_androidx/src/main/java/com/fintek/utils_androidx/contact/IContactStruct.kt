package com.fintek.utils_androidx.contact

import android.content.ContentResolver
import android.database.Cursor
import androidx.collection.SparseArrayCompat

/**
 * Created by ChaoShen on 2020/11/16
 */
interface IContactStruct<T> {

    fun queryColumns(): Array<String>

    fun structHandler(
        contentResolver: ContentResolver,
        cursor: Cursor,
        sparseArrayCompat: SparseArrayCompat<Int>,
    ): T?

    /**
     * Get param index in [queryColumns]
     */
    fun String.columnIndex(): Int {
        val index = queryColumns().indexOf(this)
        if (index == -1) throw IllegalArgumentException("Can't find $this in queryColumns[${queryColumns()}]")
        return index
    }
}