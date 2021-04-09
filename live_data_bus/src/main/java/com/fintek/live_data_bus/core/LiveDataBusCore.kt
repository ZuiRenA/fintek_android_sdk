package com.fintek.live_data_bus.core

import com.fintek.live_data_bus.condition.BusKeyCondition

/**
 * Created by ChaoShen on 2020/9/10
 */
@Suppress("UNCHECKED_CAST")
internal class LiveDataBusCore private constructor() {
    companion object {
        val INSTANCE by lazy { LiveDataBusCore() }
    }

    private val bus: HashMap<BusKeyCondition<*>, LiveDataEvent<*>> = hashMapOf()

    internal fun <T> with(key: BusKeyCondition<T>): LiveDataEvent<T> {
        containsKeyNullSet(key)
        return bus[key] as LiveDataEvent<T>
    }

    private fun <T> containsKeyNullSet(key: BusKeyCondition<T>) {
        if (!bus.containsKey(key)) {
            bus[key] = LiveDataEvent(key)
        }
    }
}