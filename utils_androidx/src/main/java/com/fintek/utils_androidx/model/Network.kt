package com.fintek.utils_androidx.model

/**
 * Created by ChaoShen on 2020/12/3
 */
data class BaseResponse<T> (
    val code: String? = null,
    val message: String? = null,
    val time: String? = null,
    val data: T? = null
)