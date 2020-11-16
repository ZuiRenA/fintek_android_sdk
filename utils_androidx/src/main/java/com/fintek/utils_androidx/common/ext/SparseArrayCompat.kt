package com.fintek.utils_androidx.common.ext

import androidx.collection.SparseArrayCompat

/**
 * Created by ChaoShen on 2020/11/16
 */
internal fun <E> SparseArrayCompat<E>.getAssertNotNull(index: Int): E {
    val e = this.get(index)
    return checkNotNull(e)
}