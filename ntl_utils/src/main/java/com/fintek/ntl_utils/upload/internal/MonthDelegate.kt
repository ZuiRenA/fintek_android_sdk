package com.fintek.utils_androidx.upload.internal

import com.fintek.utils_androidx.sharedPreference.SharedPreferenceUtils
import com.fintek.utils_androidx.upload.UploadUtils
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

/**
 * Created by ChaoShen on 2020/12/21
 */
internal class MonthDelegate(private val key: String) : ReadWriteProperty<Any?, UploadUtils.MonthlyRecord?> {
    private val gson: Gson = Gson()
    private val typeToken: TypeToken<UploadUtils.MonthlyRecord> = object : TypeToken<UploadUtils.MonthlyRecord>() {}

    override fun setValue(thisRef: Any?, property: KProperty<*>, value: UploadUtils.MonthlyRecord?) {
        val json = if (value == null) "" else gson.toJson(value)
        SharedPreferenceUtils.apply {
            putString(key, json)
        }
    }

    override fun getValue(thisRef: Any?, property: KProperty<*>): UploadUtils.MonthlyRecord? {
        val value = SharedPreferenceUtils.getString(key)
        if (value.isNullOrEmpty() || value.isNullOrBlank()) {
            return null
        }
        return gson.fromJson<UploadUtils.MonthlyRecord>(value, typeToken.type)
    }
}