package com.fintek.ocr_camera.camera

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.fintek.ocr_camera.CameraExt
import com.fintek.ocr_camera.CameraExt.hasFlash
import com.fintek.ocr_camera.CameraUtils.checkPermissionFirst
import com.fintek.ocr_camera.CameraUtils.getScreenHeight
import com.fintek.ocr_camera.CameraUtils.getScreenWidth
import com.fintek.ocr_camera.CameraUtils.isFastClick
import com.fintek.ocr_camera.FileUtils
import com.fintek.ocr_camera.ImageUtils
import com.fintek.ocr_camera.R
import com.fintek.ocr_camera.cropper.CropListener
import kotlinx.android.synthetic.main.activity_ocr_camera.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.net.URI
import kotlin.math.min


internal class OcrCameraActivity : AppCompatActivity(), View.OnClickListener {

    private var cropBitmap: Bitmap? = null
    private var type: Int = IDCardCamera.TYPE_ID_CARD_FRONT //拍摄类型
    private var filePath: String? = null
    private var isToast: Boolean = true //是否弹toast 为了保证for循环只弹一次

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val checkPermission = checkPermissionFirst(
            IDCardCamera.PERMISSION_CODE_FIRST,
            arrayOf(Manifest.permission.CAMERA)
        )
        if (checkPermission) {
            init()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        var isPermissions = true
        for (i in permissions.indices) {
            if (grantResults[i] == PackageManager.PERMISSION_DENIED) {
                isPermissions = false
                if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permissions[i])) {
                    //用户选择了"不再询问"
                    if (isToast) {
                        Toast.makeText(applicationContext, "请手动打开该应用需要的权限", Toast.LENGTH_SHORT).show()
                        isToast = false
                    }
                }
            }
        }
        isToast = true
        if (isPermissions) {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "允许所有权限")
            init()
        } else {
            Log.d("onRequestPermission", "onRequestPermissionsResult: " + "有权限不允许")
            finish()
        }
    }

    override fun onStart() {
        super.onStart()
        camera_preview.onStart()
    }

    override fun onStop() {
        super.onStop()
        camera_preview.onStop()
    }

    private fun init() {
        setContentView(R.layout.activity_ocr_camera)
        type = intent.getIntExtra(IDCardCamera.TAKE_TYPE, IDCardCamera.TYPE_ID_CARD_FRONT)
        filePath = intent.getStringExtra(IDCardCamera.IMAGE_PATH)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        initView()
        initListener()
    }

    private fun initView() {
        val screenMinSize = min(getScreenWidth(), getScreenHeight())
        val screenMaxSize = min(getScreenWidth(), getScreenHeight())
        val height = screenMinSize * 0.75
        val width = (height * 75f / 47f)
        //获取底部"操作区域"的宽度
        val flCameraOptionWidth = ((screenMaxSize - width) / 2).toFloat()
        val containerParams = LinearLayout.LayoutParams(
            width.toInt(), ViewGroup.LayoutParams.MATCH_PARENT
        )
        val cropParams = LinearLayout.LayoutParams(
            width.toInt(), height.toInt()
        )
        val cameraOptionParams = LinearLayout.LayoutParams(
            flCameraOptionWidth.toInt(), ViewGroup.LayoutParams.MATCH_PARENT
        )
        ll_camera_crop_container.layoutParams = containerParams
        iv_camera_crop.layoutParams = cropParams
        //获取"相机裁剪区域"的宽度来动态设置底部"操作区域"的宽度，使"相机裁剪区域"居中
        fl_camera_option.layoutParams = cameraOptionParams

        when (type) {
            IDCardCamera.TYPE_ID_CARD_FRONT -> iv_camera_crop.setImageResource(R.mipmap.camera_idcard_front)
            IDCardCamera.TYPE_ID_CARD_BACK -> iv_camera_crop.setImageResource(R.mipmap.camera_idcard_back)
        }

        Handler().postDelayed({
            runOnUiThread {
                camera_preview.visibility = View.VISIBLE
            }
        }, 500)
    }

    private fun initListener() {
        camera_preview.setOnClickListener(this)
        iv_camera_flash.setOnClickListener(this)
        iv_camera_close.setOnClickListener(this)
        iv_camera_take.setOnClickListener(this)
        iv_camera_result_ok.setOnClickListener(this)
        iv_camera_result_cancel.setOnClickListener(this)
    }

    override fun onClick(view: View?) {
        when (view) {
            camera_preview -> camera_preview.focus()
            iv_camera_flash -> if (hasFlash()) {
                iv_camera_flash.setImageResource(getFlashMipmap(camera_preview.switchFlashLight()))
            } else {
                Toast.makeText(applicationContext, R.string.no_flash, Toast.LENGTH_SHORT).show()
            }
            iv_camera_close -> finish()
            iv_camera_take -> if (!isFastClick()) {
                takePhoto()
            }
            iv_camera_result_ok -> confirm()
            iv_camera_result_cancel -> {
                camera_preview.run {
                    isEnabled = true
                    addCallback()
                    startPreview()
                }
                iv_camera_flash.setImageResource(R.mipmap.camera_flash_off)
                setTakePhotoLayout()
            }
        }
    }

    private fun takePhoto() {
        camera_preview.isEnabled = false
        CameraExt.camera?.setOneShotPreviewCallback { bytes, camera ->
            val size = camera.parameters.previewSize
            camera.stopPreview()
            MainScope().launch(Dispatchers.Default) {
                val w = size.width
                val h = size.height
                val bitmap = ImageUtils.getBitmapFromByte(bytes, w, h)
                cropImage(bitmap)
            }
        }
    }

    private fun confirm() {
        crop_image_view.crop(object : CropListener {
            override fun onFinish(bitmap: Bitmap?) {
                if (bitmap == null) {
                    Toast.makeText(applicationContext, getString(R.string.crop_fail), Toast.LENGTH_SHORT).show()
                    finish()
                }

                val imagePath = if (filePath == null) {
                    StringBuffer().append(FileUtils.getImageCacheDir(this@OcrCameraActivity)).append(File.separator)
                        .append(System.currentTimeMillis()).append(".jpg").toString()
                } else {
                    filePath
                }

                if (ImageUtils.save(bitmap, imagePath, Bitmap.CompressFormat.JPEG)) {
                    val intent = Intent()
                    intent.putExtra(IDCardCamera.IMAGE_PATH, imagePath)
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }
        }, true)
    }

    @Synchronized
    private suspend fun cropImage(bitmap: Bitmap) = withContext(Dispatchers.Default) {
        /*计算扫描框的坐标点*/
        val left = view_camera_crop_left.width
        val top = iv_camera_crop.top
        val right = iv_camera_crop.right + left
        val bottom = iv_camera_crop.bottom

        /*计算扫描框坐标点占原图坐标点的比例*/
        val leftProportion: Float = left.toFloat() / camera_preview.width
        val topProportion: Float = top.toFloat() / camera_preview.height
        val rightProportion: Float = right.toFloat() / camera_preview.width
        val bottomProportion: Float = bottom.toFloat() / camera_preview.bottom

        /*自动裁剪*/
        cropBitmap = Bitmap.createBitmap(
            bitmap,
            (leftProportion * bitmap.width.toFloat()).toInt(),
            (topProportion * bitmap.height.toFloat()).toInt(),
            ((rightProportion - leftProportion) * bitmap.width.toFloat()).toInt(),
            ((bottomProportion - topProportion) * bitmap.height.toFloat()).toInt()
        )

        /*设置成手动裁剪模式*/
        withContext(Dispatchers.Main) {
            //将手动裁剪区域设置成与扫描框一样大
            crop_image_view.layoutParams = LinearLayout.LayoutParams(
                iv_camera_crop.width,
                iv_camera_crop.height
            )
            setCropLayout()
            crop_image_view.setImageBitmap(cropBitmap)
        }
    }

    private fun setTakePhotoLayout() {
        iv_camera_crop.visibility    = View.VISIBLE
        camera_preview.visibility    = View.VISIBLE
        ll_camera_option.visibility  = View.VISIBLE
        crop_image_view.visibility   = View.GONE
        ll_camera_result.visibility  = View.GONE
        view_camera_crop_bottom.text = getString(R.string.touch_to_focus)

        camera_preview.focus()
    }

    private fun setCropLayout() {
        iv_camera_crop.visibility    = View.GONE
        camera_preview.visibility    = View.GONE
        ll_camera_option.visibility  = View.GONE
        crop_image_view.visibility   = View.VISIBLE
        ll_camera_result.visibility  = View.VISIBLE
        view_camera_crop_bottom.text = ""
    }

    private fun getFlashMipmap(isFlashOn: Boolean): Int =
        if (isFlashOn) R.mipmap.camera_flash_on else R.mipmap.camera_flash_off
}