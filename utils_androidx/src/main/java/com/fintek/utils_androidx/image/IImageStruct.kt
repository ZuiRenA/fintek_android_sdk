package com.fintek.utils_androidx.image

import androidx.collection.SparseArrayCompat
import android.media.ExifInterface


/**
 * Created by ChaoShen on 2020/11/16
 */
interface IImageStruct<T> {

    /**
     * This params will used in [ExifInterface.getAttribute]
     * @return please use [ExifInterface] static constant
     */
    fun queryAttributeParams(): Array<String>

    /**
     * Handler convert SparseArrayCompat to [T],
     * @param attributes [ExifInterface.getAttribute] result, please use [paramIndex] to get index
     * @return struct
     */
    fun structHandler(attributes: SparseArrayCompat<String>): T

    /**
     * Get param index in [queryAttributeParams]
     */
    fun String.paramIndex(): Int {
        val index = queryAttributeParams().indexOf(this)
        if (index == -1) throw IllegalArgumentException("Can't find $this in queryAttributeParams[${queryAttributeParams()}]")
        return index
    }
}