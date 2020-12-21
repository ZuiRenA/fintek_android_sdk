package com.fintek.utils_androidx.upload.internal

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

internal abstract class Element <T> {

    internal abstract val total: AtomicInteger

    internal abstract val partIndex: AtomicInteger

    abstract fun getAsList(): List<T>

    abstract fun next(): T

    abstract fun remove(): T

    abstract fun save(element: List<T>)

    abstract fun cache(): T?

    abstract fun header(): Any?

    abstract fun saveCache(element: T): Boolean

    abstract fun removeCache(): Boolean

    fun first(): T? = getAsList().firstOrNull()

    fun hasNext(): Boolean = total.get() > partIndex.get()
}