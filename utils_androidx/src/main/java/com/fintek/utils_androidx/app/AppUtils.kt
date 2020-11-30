package com.fintek.utils_androidx.app

import android.content.pm.PackageManager
import com.fintek.utils_androidx.FintekUtils

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
        return if (packageName.isNullOrBlank()) "" else try {
            val pm: PackageManager = FintekUtils.requiredContext.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi.versionName
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            ""
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
        return if (packageName.isNullOrBlank()) -1 else try {
            val pm: PackageManager = FintekUtils.requiredContext.packageManager
            val pi = pm.getPackageInfo(packageName, 0)
            pi?.versionCode ?: -1
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            -1
        }
    }
}