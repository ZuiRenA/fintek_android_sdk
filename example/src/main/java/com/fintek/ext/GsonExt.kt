package com.fintek.ext

import com.google.gson.Gson

/**
 * Created by ChaoShen on 2021/4/12
 */
private val gson by lazy { Gson() }

fun Any.toJson(): String = gson.toJson(this)