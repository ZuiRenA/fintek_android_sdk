package com.fintek.utils_androidx.model

import android.os.Parcel
import android.os.Parcelable
import java.io.Serializable
import java.lang.Exception


data class CoronetResponse<T>(
    val response: T?,
    val exception: Exception?
) : Serializable