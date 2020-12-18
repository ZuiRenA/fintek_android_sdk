package com.fintek.utils_androidx.sharedPreference

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import com.fintek.utils_androidx.FintekUtils

/**
 * Created by ChaoShen on 2020/12/18
 */
object SharedPreferenceUtils {
    private const val SHARED_PREFERENCE_PATH = "FintekUtilsSP"

    private val sp = FintekUtils.requiredContext.getSharedPreferences(SHARED_PREFERENCE_PATH, Context.MODE_PRIVATE)

    @JvmOverloads
    @JvmStatic
    fun getString(key: String, defaultValue: String = "") = sp.getString(key, defaultValue)

    @JvmOverloads
    @JvmStatic
    fun getInt(key: String, defaultValue: Int = 0) = sp.getInt(key, defaultValue)

    @JvmOverloads
    @JvmStatic
    fun getBoolean(key: String, defaultValue: Boolean = false) = sp.getBoolean(key, defaultValue)

    @JvmOverloads
    @JvmStatic
    fun getFloat(key: String, defaultValue: Float = 0f) = sp.getFloat(key, defaultValue)

    @JvmOverloads
    @JvmStatic
    fun getLong(key: String, defaultValue: Long = 0L) = sp.getLong(key, defaultValue)

    @JvmOverloads
    @JvmStatic
    fun getStringSet(key: String, defaultValue: Set<String> = emptySet()) = sp.getStringSet(key, defaultValue)


    @SuppressLint("ApplySharedPref")
    @JvmStatic
    fun commit(block: SharedPreferences.Editor.() -> Unit) {
        val editor = sp.edit()
        block(editor)
        editor.commit()
    }

    @JvmStatic
    fun apply(block: SharedPreferences.Editor.() -> Unit) {
        val editor = sp.edit()
        block(editor)
        editor.apply()
    }
}