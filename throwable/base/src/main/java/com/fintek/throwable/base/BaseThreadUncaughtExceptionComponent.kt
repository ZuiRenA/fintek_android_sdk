package com.fintek.throwable.base

/**
 * The top level throwable exception handler
 */
typealias GLOBAL_COMPONENT = BaseThreadUncaughtExceptionComponent

object BaseThreadUncaughtExceptionComponent : ComponentHandler {

    internal var defaultExceptionHandler: DefaultExceptionHandler? = null

    override fun uncaughtException(thread: Thread?, throwable: Throwable?) {
        if (defaultExceptionHandler == null) {
            BaseGlobalComponentBinder.jvmDefaultExceptionHandler.uncaughtException(thread, throwable)
        }

        val isHandled = defaultExceptionHandler?.exceptionHandler(thread, throwable) ?: return
        if (isHandled) {
            return
        }

        BaseGlobalComponentBinder.jvmDefaultExceptionHandler.uncaughtException(thread, throwable)
    }
}