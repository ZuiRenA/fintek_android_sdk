package com.fintek.ocr_camera.cropper

import android.graphics.Bitmap

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${裁剪监听接口}
 */
interface CropListener {
    fun onFinish(bitmap: Bitmap?)
}