package com.fintek.live_data_bus.core

import androidx.lifecycle.Observer

/**
 * Created by ChaoShen on 2020/9/10
 */
internal const val ANDROIDX_LIVE_DATA = "androidx.lifecycle.LiveData"
internal const val OBSERVER_FOREVER = "observeForever"

internal class ObserverWrapper<T>(private val observer: Observer<T>? = null) : Observer<T> {
    internal var preventNextEvent = false

    override fun onChanged(t: T) {
//        if (isCallOnObserver) {
//            return
//        }
        if (preventNextEvent) {
            preventNextEvent = false
            return
        }

        observer?.onChanged(t)
    }

    private val isCallOnObserver: Boolean
        @JvmName("isCallOnObserver") get() =
            with(Thread.currentThread().stackTrace) {
                if (isNotEmpty()) {
                    forEach {
                        if (ANDROIDX_LIVE_DATA == it.className && OBSERVER_FOREVER == it.methodName) {
                            return@with true
                        }
                    }
                }
                return@with false
            }
}