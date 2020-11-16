package com.fintek.utils_androidx.call

import android.content.ContentResolver
import android.database.Cursor
import androidx.collection.SparseArrayCompat

/**
 * Created by ChaoShen on 2020/11/16
 */
interface ICallStruct<T> {
    /**
     * query columns
     */
    fun queryColumns(): Array<String>


    /**
     * handler columns to [T]
     *
     * @param contentResolver
     * @param cursor
     * @param columns column index in [queryColumns]
     */
    fun structHandler(contentResolver: ContentResolver, cursor: Cursor, columns: SparseArrayCompat<Int>): T?


    /**
     * Get param index in [queryColumns]
     */
    fun String.columnIndex(): Int {
        val index = queryColumns().indexOf(this)
        if (index == -1) throw IllegalArgumentException("Can't find $this in queryColumns[${queryColumns()}]")
        return index
    }

    fun <E> SparseArrayCompat<E>.getAssertNotNull(index: Int): E {
        val e = this.get(index)
        return checkNotNull(e)
    }
}