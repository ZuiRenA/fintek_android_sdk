package com.fintek.utils_mexico.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

/**
 * Created by ChaoShen on 2021/8/4
 */
@JsonClass(generateAdapter = true)
data class MexicoAddress(
    /**地址*/
    @field:Json(name = "address0") val address: String?,
    /***/
    @field:Json(name = "admin_area") val adminArea: String?,
    @field:Json(name = "country_code") val countryCode: String?,
    @field:Json(name = "country_name") val countryName: String?,
    @field:Json(name = "feature_name") val featureName: String?,
    @field:Json(name = "locality") val locality: String?,
)