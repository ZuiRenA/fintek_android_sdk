package com.fintek.utils_androidx.network

import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.UtilsBridge
import com.fintek.utils_androidx.thread.*
import com.fintek.utils_androidx.thread.TYPE_CACHE
import com.fintek.utils_androidx.thread.TYPE_CPU
import com.fintek.utils_androidx.thread.TYPE_IO
import com.fintek.utils_androidx.thread.TYPE_SINGLE

class RequestTask<T>(
    private val doInBackground: () -> T
) : SimpleTask<T>() {

    private var onNext: FintekUtils.Consumer<T>? = null
    private var onError: FintekUtils.Consumer<Throwable>? = null
    private var onCancel: FintekUtils.Consumer<Unit>? = null

    fun execute(dispatcher: Dispatchers) {
        when(dispatcher.type) {
            TYPE_SINGLE -> UtilsBridge.executeBySingle(this)
            TYPE_CACHE -> UtilsBridge.executeByCached(this)
            TYPE_IO -> UtilsBridge.executeByIO(this)
            TYPE_CPU -> UtilsBridge.executeByCPU(this)
            else -> UtilsBridge.executeByCustom(this, dispatcher.type)
        }
    }

    fun onNext(consumer: FintekUtils.Consumer<T>) = apply {
        this.onNext = consumer
    }

    fun onError(consumer: FintekUtils.Consumer<Throwable>) = apply {
        this.onError = consumer
    }

    fun onCancel(consumer: FintekUtils.Consumer<Unit>) = apply {
        this.onCancel = consumer
    }

    override fun doInBackground(): T = doInBackground.invoke()

    override fun onFail(t: Throwable) {
        onError?.accept(t)
    }

    override fun onSuccess(result: T) {
        onNext?.accept(result)
    }

    override fun onCancel() {
        onCancel?.accept(Unit)
    }
}

enum class Dispatchers(internal val type: Int) {
    SINGLE(-1),
    CACHE(-2),
    IO(-4),
    CPU(-5),
}