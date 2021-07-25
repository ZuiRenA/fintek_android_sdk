package com.fintek.throwable.coroutine

import com.fintek.throwable.base.DispatchedComponentHandler
import com.fintek.throwable.base.GLOBAL_COMPONENT
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlin.coroutines.AbstractCoroutineContextElement


abstract class CoroutineComponent :
    AbstractCoroutineContextElement(CoroutineExceptionHandler), CoroutineExceptionHandler,
    DispatchedComponentHandler {

    override fun dispatch2Handler(thread: Thread?, throwable: Throwable?) {
        GLOBAL_COMPONENT.uncaughtException(thread, throwable)
    }
}