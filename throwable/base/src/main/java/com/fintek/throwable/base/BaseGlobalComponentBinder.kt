package com.fintek.throwable.base

/**
 * Created by ChaoShen on 2021/7/23
 */
object BaseGlobalComponentBinder : BaseBindComponentHandler {

    internal val jvmDefaultExceptionHandler: Thread.UncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()

    override fun bindComponent() {
        Thread.setDefaultUncaughtExceptionHandler(GLOBAL_COMPONENT)
    }

    override fun setDefaultExceptionHandler(handler: DefaultExceptionHandler) {
        BaseThreadUncaughtExceptionComponent.defaultExceptionHandler = handler
    }
}