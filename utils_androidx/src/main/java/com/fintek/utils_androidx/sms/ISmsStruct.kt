package com.fintek.utils_androidx.sms

import android.content.ContentResolver
import android.database.Cursor
import androidx.collection.SparseArrayCompat

interface ISmsStruct<T> {

    /**
     * The ContentResolver query projection
     */
    fun projection(): Array<String>

    /**
     * This function will invoke many times, please don't do overly burdensome task
     *
     * @return T is struct type
     */
    fun structHandler(columnIndex: SparseArrayCompat<Int>, cursor: Cursor, contentResolver: ContentResolver): T?
}