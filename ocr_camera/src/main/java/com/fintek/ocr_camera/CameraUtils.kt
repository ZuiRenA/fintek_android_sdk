package com.fintek.ocr_camera

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.ActivityCompat
import java.util.*

/**
 * Created by ChaoShen on 2020/9/15
 */
internal object CameraUtils {

    fun Context.getScreenWidth() = resources.displayMetrics.widthPixels

    fun Context.getScreenHeight() = resources.displayMetrics.heightPixels

    /**
     * 第一次检查权限，用在打开应用的时候请求应用需要的所有权限
     *
     * @param context
     * @param requestCode 请求码
     * @param permission  权限数组
     * @return
     */
    fun Context.checkPermissionFirst(
        requestCode: Int,
        permission: Array<String>
    ): Boolean {
        val permissions: MutableList<String> = ArrayList()
        for (per in permission) {
            val permissionCode: Int = ActivityCompat.checkSelfPermission(this, per)
            if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per)
            }
        }
        return if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this as Activity,
                permissions.toTypedArray(),
                requestCode
            )
            false
        } else {
            true
        }
    }

    /**
     * 第二次检查权限，用在某个操作需要某个权限的时候调用
     *
     * @param context
     * @param requestCode 请求码
     * @param permission  权限数组
     * @return
     */
    @SuppressLint("ObsoleteSdkInt")
    fun Context.checkPermissionSecond(
        requestCode: Int,
        permission: Array<String>
    ): Boolean {
        val permissions: MutableList<String> = ArrayList()
        for (per in permission) {
            val permissionCode: Int = ActivityCompat.checkSelfPermission(this, per)
            if (permissionCode != PackageManager.PERMISSION_GRANTED) {
                permissions.add(per)
            }
        }
        return if (permissions.isNotEmpty()) {
            ActivityCompat.requestPermissions(
                this as Activity,
                permissions.toTypedArray(),
                requestCode
            )

            /*跳转到应用详情，让用户去打开权限*/
            val localIntent = Intent()
            localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (Build.VERSION.SDK_INT >= 9) {
                localIntent.action = "android.settings.APPLICATION_DETAILS_SETTINGS"
                localIntent.data = Uri.fromParts("package", getPackageName(), null)
            } else if (Build.VERSION.SDK_INT <= 8) {
                localIntent.action = Intent.ACTION_VIEW
                localIntent.setClassName(
                    "com.android.settings",
                    "com.android.settings.InstalledAppDetails"
                )
                localIntent.putExtra(
                    "com.android.settings.ApplicationPkgName",
                    getPackageName()
                )
            }
            startActivity(localIntent)
            false
        } else {
            true
        }
    }

    private var lastClickTime: Long = 0
    /**
     * 判断是否是快速点击
     *
     * @param intervalTime 间隔时间，单位毫秒。
     * @return true：是，false：否
     */
    fun isFastClick(intervalTime: Long = 1000): Boolean = with(System.currentTimeMillis()) {
        if (this - lastClickTime < intervalTime) {
            return@with true
        }

        lastClickTime = this
        return@with false
    }
}