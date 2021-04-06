package com.fintek.ocr_camera.camera

import android.content.Context
import android.content.res.Configuration
import android.hardware.Camera
import android.util.AttributeSet
import android.util.Log
import android.view.SurfaceHolder
import android.view.SurfaceView
import com.fintek.ocr_camera.CameraExt
import com.fintek.ocr_camera.CameraUtils.getScreenHeight
import com.fintek.ocr_camera.CameraUtils.getScreenWidth
import kotlin.math.abs

/**
 * Created by ChaoShen on 2020/9/15
 */
internal class CameraPreview @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : SurfaceView(context, attrs, defStyleAttr), SurfaceHolder.Callback {

    private val mContext = context
    private var camera: Camera? = null
    private var autoFocusManager: AutoFocusManager? = null
    private val sensorController = SensorController.getInstance(context.applicationContext)
    private val surfaceHolder: SurfaceHolder = holder

    init {
        surfaceHolder.apply {
            addCallback(this@CameraPreview)
            setKeepScreenOn(true)
            setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS)
        }
    }

    /**
     * 对焦
     */
    fun focus() {
        if (camera == null) return
        try {
            camera!!.autoFocus(null)
        } catch (e: Exception) {
            Log.d(TAG, "takePhoto", e)
        }
    }

    /**
     * 开关闪光灯
     * @return 闪光灯是否开启
     */
    fun switchFlashLight(): Boolean {
        camera?.let {
            val parameters = it.parameters
            return if (parameters.flashMode == Camera.Parameters.FLASH_MODE_TORCH) {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_TORCH
                it.parameters = parameters
                true
            } else {
                parameters.flashMode = Camera.Parameters.FLASH_MODE_OFF
                it.parameters = parameters
                false
            }
        }

        return false
    }

    @JvmOverloads
    fun takePhoto(
        shutter: Camera.ShutterCallback? = null,
        raw: Camera.PictureCallback? = null,
        pictureCallback: Camera.PictureCallback
    ) {
        camera?.let {
            try {
                it.takePicture(shutter, raw, pictureCallback)
            } catch (e: Exception) {
                Log.d(TAG, "takePhoto", e)
            }
        }
    }

    fun startPreview() {
        camera?.startPreview()
    }

    fun onStart() {
        addCallback()
        sensorController.onStart()
        sensorController.setCameraFocusListener(object : SensorController.CameraFocusListener {
            override fun onFocus() {
                focus()
            }
        })
    }

    fun onStop() {
        sensorController.onStop()
    }

    override fun surfaceCreated(holder: SurfaceHolder) {
        camera = CameraExt.openCamera()
        if (camera == null) return

        try {
            camera!!.setPreviewDisplay(holder)

            setupPreviewView(camera!!, true)
        } catch (e: Exception) {
            Log.d(TAG, "Error setting camera preview", e)
            try {
                setupPreviewView(camera!!, false)
            } catch (e1: Exception) {
                e1.printStackTrace()
                camera = null
            }
        }
    }

    override fun surfaceChanged(p0: SurfaceHolder, p1: Int, p2: Int, p3: Int) {
        // 固定屏幕方向，不会触发
    }

    override fun surfaceDestroyed(holder: SurfaceHolder) {
        holder.removeCallback(this)
        // 回收资源
        release()
    }

    fun addCallback() {
        surfaceHolder.addCallback(this)
    }

    private fun setupPreviewView(camera: Camera, needBestSize: Boolean) {
        val parameters = camera.parameters
        if (resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT) {
            // 竖屏拍照时需要设置旋转角度90，否则预览的方向和界面方向不同
            camera.setDisplayOrientation(90)
            parameters.setRotation(90)
        } else {
            camera.setDisplayOrientation(0)
            parameters.setRotation(0)
        }
        if (needBestSize) {
            val sizeList = parameters.supportedPreviewSizes //获取支持的预览大小
            val bestSize = getOptimalPreviewSize(sizeList, mContext.getScreenWidth(), mContext.getScreenHeight())
            require(bestSize != null)
            parameters.setPreviewSize(bestSize.width, bestSize.height)
        }
        camera.parameters = parameters
        camera.startPreview()
        focus() //首次对焦
    }

    private fun getOptimalPreviewSize(size: List<Camera.Size>, w: Int, h: Int): Camera.Size? {
        val aspectTolerance = 0.1
        val targetRatio = w.toDouble() / h.toDouble()

        var optimalSize: Camera.Size? = null
        var minDiff = Double.MAX_VALUE

        // Try to find an size match aspect ratio and size
        for (it in size) {
            val ratio = it.width.toDouble() / it.height.toDouble()
            if (abs(ratio - targetRatio) > aspectTolerance) {
                continue
            }
            if (abs(it.height - h) < minDiff) {
                optimalSize = it
                minDiff = abs(it.height.toDouble() - h)
            }
        }

        // Cannot find the one match the aspect ratio, ignore the requirement

        // Cannot find the one match the aspect ratio, ignore the requirement
        if (optimalSize == null) {
            minDiff = Double.MAX_VALUE
            for (it in size) {
                if (abs(it.height - h) < minDiff) {
                    optimalSize = it
                    minDiff = abs(it.height.toDouble() - h)
                }
            }
        }
        return optimalSize
    }

    /**
     * 释放资源
     */
    private fun release() {
        camera?.let {
            it.setPreviewCallback(null)
            it.stopPreview()
            it.release()
            camera = null

            if (autoFocusManager != null) {
                autoFocusManager!!.stop()
                autoFocusManager = null
            }
        }
    }

    private companion object {
        const val TAG = "CameraPreview"
    }
}