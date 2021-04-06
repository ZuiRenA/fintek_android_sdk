package com.fintek.ocr_camera

import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Camera

/**
 * Created by ChaoShen on 2020/9/15
 */
internal object CameraExt {
    var camera: Camera? = null
        private set

    fun Context.hasCamera(): Boolean = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)

    fun Context.hasFlash(): Boolean = packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    fun openCamera(): Camera? {
        camera = null
        try {
            camera = Camera.open()
        } catch (e: Exception) {

        }
        return camera
    }
}