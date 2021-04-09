package com.fintek.live_data_bus.core

import android.os.Handler
import android.os.Looper
import androidx.annotation.MainThread
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.fintek.live_data_bus.ExternalLiveData
import com.fintek.live_data_bus.START_VERSION
import com.fintek.live_data_bus.condition.BusKeyCondition
import com.fintek.live_data_bus.condition.Observable
import java.util.concurrent.TimeUnit

/**
 * Created by ChaoShen on 2020/9/10
 */
internal val isMainThread: Boolean get() = Looper.myLooper() == Looper.getMainLooper()

@Suppress("UNCHECKED_CAST")
class LiveDataEvent<T>(private val key: BusKeyCondition<T>) : Observable<T> {

    private val liveData: ExternalLiveData<T> = ExternalLiveData()
    private val observerMap: HashMap<Observer<T>, ObserverWrapper<T>> = hashMapOf()
    private val mainHandler = Handler(Looper.getMainLooper())

    override fun post(value: T) {
        if (isMainThread) {
            postInternal(value)
        } else {
            mainHandler.post { postInternal(value) }
        }
    }

    override fun postDelay(value: T, delay: Long, timeUnit: TimeUnit) {
        val delayMilliSeconds = timeUnit.toMillis(delay)
        mainHandler.postDelayed({ postInternal(value) }, delayMilliSeconds)
    }

    override fun postDelay(sender: LifecycleOwner?, value: T, delay: Long, timeUnit: TimeUnit) {
        val delayMilliSeconds = timeUnit.toMillis(delay)
        mainHandler.postDelayed(PostLifeValue(value, sender), delayMilliSeconds)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<T>) {
        if (isMainThread)
            observeInternal(owner, observer, isSticky = false)
        else
            mainHandler.post { observeInternal(owner, observer, isSticky = false) }
    }

    override fun observeSticky(owner: LifecycleOwner, observer: Observer<T>) {
        if (isMainThread)
            observeInternal(owner, observer, isSticky = true)
        else
            mainHandler.post { observeInternal(owner, observer, isSticky = true) }
    }

    override fun observeForever(observer: Observer<T>) {
        if (isMainThread)
            observeForeverInternal(observer, isSticky = false)
        else
            mainHandler.post { observeForeverInternal(observer, isSticky = false) }
    }

    override fun observeStickyForever(observer: Observer<T>) {
        if (isMainThread)
            observeForeverInternal(observer, isSticky = true)
        else
            mainHandler.post { observeForeverInternal(observer, isSticky = true) }
    }

    override fun removeObserver(observer: Observer<T>) {
        if (isMainThread)
            removeObserverInternal(observer)
        else
            mainHandler.post { removeObserverInternal(observer) }
    }

    @MainThread
    private fun postInternal(value: T) {
        liveData.value = value
    }

    @MainThread
    private fun observeInternal(owner: LifecycleOwner, observer: Observer<T>, isSticky: Boolean) {
        val observerWrapper: ObserverWrapper<T> = ObserverWrapper(observer)
        if (!isSticky) {
            observerWrapper.preventNextEvent = liveData.getVersion() > START_VERSION
        }
        liveData.observe(owner, observerWrapper)
    }

    @MainThread
    private fun observeForeverInternal(
        observer: Observer<T>,
        isSticky: Boolean
    ) {
        val observerWrapper: ObserverWrapper<T> = ObserverWrapper(observer)
        if (!isSticky) {
            observerWrapper.preventNextEvent = liveData.getVersion() > START_VERSION
        }
        observerMap[observer] = observerWrapper
        liveData.observeForever(observerWrapper)
    }

    @MainThread
    private fun removeObserverInternal(observer: Observer<T>) {
        val realObserver: Observer<T>? = if (observerMap.containsKey(observer))
            observerMap.remove(observer) else null
        liveData.removeObserver(realObserver ?: observer)
    }


    inner class PostLifeValue(private val value: Any?, private val lifecycleOwner: LifecycleOwner?): Runnable {
        override fun run() {
            lifecycleOwner?.let {
                if (it.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {
                    postInternal(value as T)
                }
            }
        }
    }
}