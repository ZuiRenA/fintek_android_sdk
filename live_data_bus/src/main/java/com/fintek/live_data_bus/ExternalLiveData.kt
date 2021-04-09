package com.fintek.live_data_bus

import androidx.lifecycle.*
import com.fintek.live_data_bus.core.ObserverWrapper
import java.lang.reflect.Field

internal const val START_VERSION = -1

internal class ExternalLiveData <T> : MutableLiveData<T>() {
    private val observerMap = hashMapOf<Observer<in T>, Observer<in T>>()

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        super.observe(owner, observer)
        try {
            callMethodVersionSet(observer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeForever(observer: Observer<in T>) {
        if (!observerMap.containsKey(observer)) {
            observerMap[observer] = ObserverWrapper(observer)
        }
        super.observeForever(observer)
    }

    override fun removeObserver(observer: Observer<in T>) {
        val realObserver: Observer<in T> = removeObserverMap(observer) ?: observer
        super.removeObserver(realObserver)
    }

    private fun removeObserverMap(observer: Observer<in T>): Observer<in T>? {
        if (observerMap.containsKey(observer)) {
            return observerMap.remove(observer)
        }
        return null
    }

    fun getVersion(): Int = try {
        getFieldVersion().get(this) as Int
    } catch (e: Exception) {
        START_VERSION
    }

    @Throws(Exception::class)
    private fun getFieldObservers(): Any? {
        val fieldObservers = LiveData::class.java.getDeclaredField("mObservers").apply { isAccessible = true }
        return fieldObservers.get(this)
    }

    @Throws(Exception::class)
    private fun getFieldVersion(): Field {
        return LiveData::class.java.getDeclaredField("mVersion").apply { isAccessible = true }
    }

    private fun callMethodVersionSet(observer: Observer<in T>) {
        val mObservers = getFieldObservers()
        val classOfSafeIterableMap = mObservers?.javaClass
        val methodGet = classOfSafeIterableMap?.getDeclaredMethod("get", Any::class.java)?.apply { isAccessible = true }
        val objWrapperEntry = methodGet?.invoke(mObservers, observer)

        val objWrapper = if (objWrapperEntry is Map.Entry<*, *>) objWrapperEntry.value else null
        require(objWrapper != null) { "Wrapper can not be bull!" }

        val fieldLastVersion = objWrapper.javaClass.superclass?.getDeclaredField("mLastVersion")?.apply { isAccessible = true }
        val fieldVersion = getFieldVersion()
        fieldLastVersion?.set(objWrapper, fieldVersion.get(this))
    }
}