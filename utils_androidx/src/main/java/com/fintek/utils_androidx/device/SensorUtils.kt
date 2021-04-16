package com.fintek.utils_androidx.device

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorManager
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2021/4/15
 */
object SensorUtils {
    private val sensorManager by lazy {
        FintekUtils.requiredContext.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    /**
     * Return all sensor it's type [Sensor]
     * @return sensor list
     */
    @JvmStatic
    fun getSensors(): List<Sensor> {
        return sensorManager.getSensorList(Sensor.TYPE_ALL)
    }
}