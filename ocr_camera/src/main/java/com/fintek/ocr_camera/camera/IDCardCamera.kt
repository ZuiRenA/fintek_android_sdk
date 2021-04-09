package com.fintek.ocr_camera.camera

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.fragment.app.Fragment
import java.lang.ref.WeakReference

/**
 * Created by ChaoShen on 2020/9/16
 */
class IDCardCamera {
    companion object {
        const val TYPE_ID_CARD_FRONT    = 1 // 身份证正面
        const val TYPE_ID_CARD_BACK     = 2 //身份证背面
        const val PERMISSION_CODE_FIRST = 0X12 //权限请求码
        const val TAKE_TYPE             = "take_type" //拍摄类型标记
        const val IMAGE_PATH            = "image_path" //图片路径标记

        fun create(activity: Activity) = IDCardCamera(activity)
        fun create(fragment: Fragment) = IDCardCamera(fragment)
    }

    private val activityWeakReference: WeakReference<Activity>?
    private val fragmentWeakReference: WeakReference<Fragment>?

    constructor(fragment: Fragment) {
        fragmentWeakReference = WeakReference(fragment)
        activityWeakReference = null
    }
    constructor(activity: Activity) {
        fragmentWeakReference = null
        activityWeakReference = WeakReference(activity)
    }

    fun openCamera(direction: Direction, requestCode: Int, path: String? = null) {
        val intent = Intent(if (fragmentWeakReference != null)
            fragmentWeakReference.get()?.requireContext()
        else
            activityWeakReference?.get(), OcrCameraActivity::class.java)
        intent.putExtra(TAKE_TYPE, direction.direction)
        if (path != null) {
            intent.putExtra(IMAGE_PATH, path)
        }
        if (fragmentWeakReference != null)
            fragmentWeakReference.get()?.startActivityForResult(intent, requestCode)
        else
            activityWeakReference?.get()?.startActivityForResult(intent, requestCode)
    }

    sealed class Direction(val direction: Int) {
        /**
         * 身份证正面
         */
        object Front : Direction(TYPE_ID_CARD_FRONT)

        /**
         * 身份证背面
         */
        object Back : Direction(TYPE_ID_CARD_BACK)
    }
}