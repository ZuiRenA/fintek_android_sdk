package com.fintek.throwable.base

fun interface ThreadUncaughtExceptionComponent : Thread.UncaughtExceptionHandler {
    override fun uncaughtException(thread: Thread?, throwable: Throwable?)
}

fun interface ComponentHandler : ThreadUncaughtExceptionComponent

fun interface DispatchedComponentHandler : ComponentHandler {
    fun dispatch2Handler(thread: Thread?, throwable: Throwable?)

    override fun uncaughtException(thread: Thread?, throwable: Throwable?) {
        dispatch2Handler(thread, throwable)
    }
}

fun interface BaseBindComponentHandler {
    fun bindComponent()
}
