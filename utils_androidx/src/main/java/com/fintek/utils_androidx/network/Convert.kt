package com.fintek.utils_androidx.network

/**
 * Created by ChaoShen on 2021/4/15
 */
interface Convert<From, After> {

    fun convert(from: From): After
}