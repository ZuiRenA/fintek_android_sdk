package com.fintek.coroutine

import kotlin.coroutines.CoroutineContext

/**
 * Created by ChaoShen on 2021/7/23
 */
class DispatchedCoroutineComponent : CoroutineComponent() {

    override fun handleException(context: CoroutineContext, exception: Throwable) {
        val currentThread = Thread.currentThread()
        dispatch2Handler(currentThread, exception)
    }
}