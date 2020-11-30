package com.fintek.utils_androidx.model

/**
 * Created by ChaoShen on 2020/11/30
 */
data class PackageInfo(

    val appName: String,
    val packageName: String,
    val installTime: Long,
    val updateTime: Long,
    val versionName: String,
    val versionCode: Int,

    /**
     * application flag
     */
    val flags: Int,

    /**
     * application is System app
     */
    val appType: Int,
)