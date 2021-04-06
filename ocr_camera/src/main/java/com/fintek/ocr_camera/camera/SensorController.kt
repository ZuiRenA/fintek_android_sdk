package com.fintek.ocr_camera.camera

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import java.util.*
import kotlin.math.abs
import kotlin.math.sqrt

/**
 * Created by ChaoShen on 2020/9/15
 */
internal class SensorController private constructor(context: Context): SensorEventListener {

    private val sensorManager = context.getSystemService(Activity.SENSOR_SERVICE) as SensorManager
    private val sensor: Sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var cameraFocusListener: CameraFocusListener? = null
    private var x: Int = 0; private var y: Int = 0; private var z: Int = 0
    private var lastStaticStamp: Long = 0L
    private var calendar: Calendar? = null
    private var focusing = 1 // 1 表示没有被锁定，0表示被锁定

    private var isFocusing = false
    private var canFocusIn = false //内部能否能够对焦控制机制
    private var canFocus = false

    private var status: Int = STATUS_NONE

    fun onStart() {
        resetParams()
        canFocus = true
        sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    fun onStop() {
        cameraFocusListener = null
        sensorManager.unregisterListener(this, sensor)
        canFocus = false
    }

    fun isFocusLocked(): Boolean = if (canFocus) focusing <= 0 else false

    /**
     * 锁定对焦
     */
    fun lockFocus() {
        isFocusing = true
        focusing --
        Log.i(TAG, "lockFocus")
    }

    fun resetFocus() {
        focusing = 1
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event?.sensor == null) {
            return
        }

        if (isFocusing) {
            resetParams()
            return
        }

        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            val eventX = event.values[0].toInt()
            val eventY = event.values[1].toInt()
            val eventZ = event.values[2].toInt()
            calendar = Calendar.getInstance()
            calendar?.let {
                val stamp: Long = it.timeInMillis
                val second = it.get(Calendar.SECOND)

                if(status != STATUS_NONE) {
                    val px = abs(x - eventX)
                    val py = abs(y - eventY)
                    val pz = abs(z - eventZ)

                    val value = sqrt((px * px + py * py + pz * pz).toDouble())
                    if (value > 1.4) {
                        status = STATUS_MOVE
                    } else {
                        //上一次状态是move，记录静态时间点
                        if (status == STATUS_MOVE) {
                            lastStaticStamp = stamp
                            canFocusIn = true
                        }

                        if (canFocusIn) {
                            if (stamp - lastStaticStamp > DELAY_DURATION) {
                                //移到后静止一段时间，可以发送对焦行为
                                if (!isFocusing) {
                                    canFocusIn = false

                                    cameraFocusListener?.onFocus()
                                }
                            }
                        }

                        status = STATUS_STATIC
                    }
                } else {
                    lastStaticStamp = stamp
                    status = STATUS_STATIC
                }

                x = eventX; y = eventY; z = eventZ
            }
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
        // ignore this
    }

    private fun resetParams() {
        status = STATUS_NONE
        canFocusIn = false
        x = 0; y = 0; z = 0
    }

    companion object {
        private const val TAG = "SensorController"
        private const val DELAY_DURATION = 500
        const val STATUS_NONE = 0
        const val STATUS_STATIC = 1
        const val STATUS_MOVE = 2

        private var instance: SensorController? = null

        @JvmStatic
        fun getInstance(context: Context): SensorController {
            if (instance == null) {
                instance = SensorController(context)
            }

            return instance!!
        }
    }

    interface CameraFocusListener {
        fun onFocus()
    }

    fun setCameraFocusListener(mCameraFocusListener: CameraFocusListener?) {
        this.cameraFocusListener = mCameraFocusListener
    }
}