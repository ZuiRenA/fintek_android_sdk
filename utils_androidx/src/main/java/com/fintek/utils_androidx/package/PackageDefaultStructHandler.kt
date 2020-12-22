package com.fintek.utils_androidx.`package`

import android.content.pm.PackageManager
import com.fintek.utils_androidx.model.PackageInfo

/**
 * Created by ChaoShen on 2020/11/30
 */
class PackageDefaultStructHandler : IPackageStruct<PackageInfo> {

    override fun structHandler(
        packageManager: PackageManager,
        packageInfo: android.content.pm.PackageInfo
    ): PackageInfo = PackageInfo(
        appName = packageManager.getApplicationLabel(packageInfo.applicationInfo).toString(),
        packageName = packageInfo.applicationInfo.packageName,
        installTime = packageInfo.firstInstallTime,
        updateTime = packageInfo.lastUpdateTime,
        versionName = packageInfo.versionName ?: "",
        versionCode = packageInfo.versionCode,
        flags = packageInfo.applicationInfo.flags,
        appType = if (isSystemApp(packageInfo.applicationInfo.flags)) 1 else 0
    )

    private fun isSystemApp(flags: Int): Boolean {
        return (flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0
    }
}