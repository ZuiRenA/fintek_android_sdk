package com.fintek.live_data_bus.condition

import java.io.Serializable

/**
 * Created by ChaoShen on 2020/9/10
 * Why use interface, The purpose is to replace String
 */
interface BusKeyCondition<T> : Serializable
