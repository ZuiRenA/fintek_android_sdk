package com.fintek.throwable.base

/**
 * Created by ChaoShen on 2021/7/23
 */
object BaseGlobalComponentBinder : BaseBindComponentHandler {
    override fun bindComponent() {
        Thread.setDefaultUncaughtExceptionHandler(GLOBAL_COMPONENT)
    }
}