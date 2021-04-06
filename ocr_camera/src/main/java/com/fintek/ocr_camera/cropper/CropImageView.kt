package com.fintek.ocr_camera.cropper

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import com.fintek.ocr_camera.R

/**
 * Author       wildma
 * Github       https://github.com/wildma
 * Date         2018/6/24
 * Desc	        ${裁剪布局}
 */
internal class CropImageView : FrameLayout {
    private var mImageView: ImageView? = null
    private var mCropOverlayView: CropOverlayView? = null

    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {
        val inflater = LayoutInflater.from(context)
        val v = inflater.inflate(R.layout.crop_image_view, this, true)
        mImageView = v.findViewById<View>(R.id.img_crop) as ImageView
        mCropOverlayView = v.findViewById<View>(R.id.overlay_crop) as CropOverlayView
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
    }

    fun setImageBitmap(bitmap: Bitmap?) {
        mImageView!!.setImageBitmap(bitmap)
        mCropOverlayView!!.setBitmap(bitmap)
    }

    fun crop(listener: CropListener?, needStretch: Boolean) {
        if (listener == null) return
        mCropOverlayView!!.crop(listener, needStretch)
    }
}