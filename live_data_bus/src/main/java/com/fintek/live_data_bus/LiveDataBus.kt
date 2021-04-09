package com.fintek.live_data_bus

import com.fintek.live_data_bus.condition.BusKeyCondition
import com.fintek.live_data_bus.condition.Observable
import com.fintek.live_data_bus.core.LiveDataBusCore

/**
 * Created by ChaoShen on 2020/9/10
 */
object LiveDataBus {

    @JvmStatic
    fun <T> get(key: BusKeyCondition<T>): Observable<T> = LiveDataBusCore.INSTANCE.with(key)

    @JvmStatic
    fun getObj(key: BusKeyCondition<*>): Observable<*> = get(key)
}