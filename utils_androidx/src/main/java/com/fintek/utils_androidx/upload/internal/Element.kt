package com.fintek.utils_androidx.upload.internal

import java.io.File
import java.util.concurrent.atomic.AtomicInteger


abstract class Element <T> {

    protected abstract val total: AtomicInteger

    protected abstract val partIndex: AtomicInteger

    abstract fun getAsList(): List<T>

    abstract fun next(): T

    abstract fun save(element: List<T>)

    fun hasNext(): Boolean = total.get() > partIndex.get()
}