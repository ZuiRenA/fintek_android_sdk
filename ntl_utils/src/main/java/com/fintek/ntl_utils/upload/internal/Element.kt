package com.fintek.utils_androidx.upload.internal

import java.io.File
import java.util.concurrent.atomic.AtomicInteger

internal abstract class Element <T> {

    internal abstract val total: AtomicInteger

    internal abstract val partIndex: AtomicInteger

    internal open var isMonthly: Boolean = false

    abstract fun getAsList(): List<T>

    abstract fun next(): T

    abstract fun remove(): T

    abstract fun save(element: List<T>)

    abstract fun cache(): T?

    abstract fun header(): Any?

    abstract fun saveCache(element: T): Boolean

    abstract fun removeCache(): Boolean

    open fun first(): T? = getAsList().firstOrNull()

    open fun hasNext(): Boolean = total.get() > partIndex.get()
}