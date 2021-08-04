package com.fintek.utils_mexico.device

import com.fintek.utils_androidx.device.SensorUtils
import com.fintek.utils_androidx.throwable.safely
import com.fintek.utils_mexico.model.Sensor

/**
 * Created by ChaoShen on 2021/4/15
 */
object SensorMexicoUtils {

    @JvmStatic
    fun getSensors(): List<Sensor>? = safely {
        val sensor = SensorUtils.getSensors()
        sensor.map { Sensor(
            maxRange = it.maximumRange.toString(),
            minDelay = it.minDelay.toString(),
            name = it.name.orEmpty(),
            power = it.power.toString(),
            resolution = it.resolution.toString(),
            type = it.type.toString(),
            vendor = it.vendor.orEmpty(),
            version = it.version.toString()
        ) }
    }
}