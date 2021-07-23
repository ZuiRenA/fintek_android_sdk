package com.fintek.throwable.base

/**
 * The top level throwable exception handler
 */
typealias GLOBAL_COMPONENT = BaseThreadUncaughtExceptionComponent

object BaseThreadUncaughtExceptionComponent : ComponentHandler {
    private val defaultHandler: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun uncaughtException(thread: Thread?, throwable: Throwable?) {
        println("thread: ${thread?.name}, throwable: ${throwable?.localizedMessage}")
    }
}