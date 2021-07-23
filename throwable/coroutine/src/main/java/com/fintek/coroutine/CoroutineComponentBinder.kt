package com.fintek.coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch


val CoroutineComponentBinder: DispatchedCoroutineComponent by getCoroutineComponent()

fun CoroutineScope.catchLaunch(
    component: CoroutineComponent = CoroutineComponentBinder,
    start: CoroutineStart = CoroutineStart.DEFAULT,
    block: suspend CoroutineScope.() -> Unit
): Job {
    val coroutineContext = coroutineContext + component
    return launch(
        context = coroutineContext,
        start = start,
        block = block
    )
}

private fun getCoroutineComponent() = lazy { DispatchedCoroutineComponent() }