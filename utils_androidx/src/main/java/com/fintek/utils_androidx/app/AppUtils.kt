package com.fintek.utils_androidx.app

import android.content.pm.PackageManager
import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.catchOrEmpty
import com.fintek.utils_androidx.throwable.catchOrZero

/**
 * Created by ChaoShen on 2020/11/12
 */
object AppUtils {
    /**
     * Return the application's version name.
     *
     * @return the application's version name
     */
    @JvmStatic
    fun getAppVersionName(): String {
        return getAppVersionName(FintekUtils.requiredContext.packageName)
    }

    /**
     * Return the application's version name.
     *
     * @param packageName The name of the package.
     * @return the application's version name
     */
    @JvmStatic
    fun getAppVersionName(packageName: String?): String {
        return if (packageName.isNullOrBlank()) "" else catchOrEmpty {
            val pm: PackageManager = FintekUtils.requiredContext.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi.versionName
        }
    }

    /**
     * Return the application's version code.
     *
     * @return the application's version code
     */
    @JvmStatic
    fun getAppVersionCode(): Int {
        return getAppVersionCode(FintekUtils.requiredContext.packageName)
    }

    /**
     * Return the application's version code.
     *
     * @param packageName The name of the package.
     * @return the application's version code
     */
    @JvmStatic
    fun getAppVersionCode(packageName: String?): Int {
        return if (packageName.isNullOrBlank()) -1 else catchOrZero(-1) {
            val pm: PackageManager = FintekUtils.requiredContext.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.versionCode ?: -1
        }
    }
}