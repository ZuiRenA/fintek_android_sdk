package com.fintek.utils_mexico.structHandler

import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import com.fintek.utils_androidx.packageInfo.IPackageStruct
import com.fintek.utils_mexico.model.App

/**
 * Created by ChaoShen on 2021/4/16
 */
class AppMexicoStructHandler : IPackageStruct<App> {

    override fun structHandler(
        packageManager: PackageManager,
        packageInfo: PackageInfo
    ): App {

        return App(
            name = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
            packageName = packageInfo.packageName,
            versionCode = packageInfo.versionCode.toString(),
            obtainTime = System.currentTimeMillis() / 1000,
            appType = if (isSystemApp(packageInfo.applicationInfo.flags)) "1" else "0",
            installTime = packageInfo.firstInstallTime,
            updateTime = packageInfo.lastUpdateTime,
            appVersion = packageInfo.versionName.orEmpty()
        )
    }

    private fun isSystemApp(flags: Int): Boolean {
        return (flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
    }
}