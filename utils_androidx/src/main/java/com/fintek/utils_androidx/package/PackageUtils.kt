package com.fintek.utils_androidx.`package`

import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.model.PackageInfo

/**
 * Created by ChaoShen on 2020/11/30
 */
object PackageUtils {
    private val DEFAULT = PackageDefaultStructHandler()

    /**
     *
     * @return default package info list
     */
    @JvmStatic
    fun getAllPackage(): List<PackageInfo> = getAllPackage(DEFAULT)

    /**
     * Used custom package struct
     *
     * @return custom package info list
     */
    @JvmStatic
    fun <T> getAllPackage(iApplicationStruct: IPackageStruct<T>): List<T>{
        val packageStruct = mutableListOf<T>()
        val packageManager = FintekUtils.requiredContext.packageManager
        val packageInfoList = packageManager.getInstalledPackages(0)

        packageInfoList.forEach {
            val struct = iApplicationStruct.structHandler(packageManager, it)
            if (struct != null) {
                packageStruct.add(struct)
            }
        }

        return packageStruct
    }
}