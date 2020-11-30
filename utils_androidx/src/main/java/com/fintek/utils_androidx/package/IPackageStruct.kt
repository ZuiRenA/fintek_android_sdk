package com.fintek.utils_androidx.`package`

import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * Created by ChaoShen on 2020/11/30
 */
interface IPackageStruct<T> {

    fun structHandler(packageManager: PackageManager, packageInfo: PackageInfo): T?
}