package com.fintek.utils_androidx.json

import android.util.Log
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

/**
 * Created by ChaoShen on 2020/11/12
 */
object JsonUtils {
    private const val TYPE_BOOLEAN: Byte = 0x00
    private const val TYPE_INT: Byte = 0x01
    private const val TYPE_LONG: Byte = 0x02
    private const val TYPE_DOUBLE: Byte = 0x03
    private const val TYPE_STRING: Byte = 0x04
    private const val TYPE_JSON_OBJECT: Byte = 0x05
    private const val TYPE_JSON_ARRAY: Byte = 0x06


    fun getBoolean(
        jsonObject: JSONObject?,
        key: String?
    ): Boolean {
        return getBoolean(jsonObject, key, false)
    }

    fun getBoolean(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Boolean
    ): Boolean {
        return getValueByType(jsonObject, key, defaultValue, TYPE_BOOLEAN)
    }

    fun getBoolean(
        json: String?,
        key: String?
    ): Boolean {
        return getBoolean(json, key, false)
    }

    fun getBoolean(
        json: String?,
        key: String?,
        defaultValue: Boolean
    ): Boolean {
        return getValueByType(json, key, defaultValue, TYPE_BOOLEAN)
    }

    fun getInt(
        jsonObject: JSONObject?,
        key: String?
    ): Int {
        return getInt(jsonObject, key, -1)
    }

    fun getInt(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Int
    ): Int {
        return getValueByType(jsonObject, key, defaultValue, TYPE_INT)
    }

    fun getInt(
        json: String?,
        key: String?
    ): Int {
        return getInt(json, key, -1)
    }

    fun getInt(
        json: String?,
        key: String?,
        defaultValue: Int
    ): Int {
        return getValueByType(json, key, defaultValue, TYPE_INT)
    }

    fun getLong(
        jsonObject: JSONObject?,
        key: String?
    ): Long {
        return getLong(jsonObject, key, -1)
    }

    fun getLong(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Long
    ): Long {
        return getValueByType(jsonObject, key, defaultValue, TYPE_LONG)
    }

    fun getLong(
        json: String?,
        key: String?
    ): Long {
        return getLong(json, key, -1)
    }

    fun getLong(
        json: String?,
        key: String?,
        defaultValue: Long
    ): Long {
        return getValueByType(json, key, defaultValue, TYPE_LONG)
    }

    fun getDouble(
        jsonObject: JSONObject?,
        key: String?
    ): Double {
        return getDouble(jsonObject, key, -1.0)
    }

    fun getDouble(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: Double
    ): Double {
        return getValueByType(jsonObject, key, defaultValue, TYPE_DOUBLE)
    }

    fun getDouble(
        json: String?,
        key: String?
    ): Double {
        return getDouble(json, key, -1.0)
    }

    fun getDouble(
        json: String?,
        key: String?,
        defaultValue: Double
    ): Double {
        return getValueByType(json, key, defaultValue, TYPE_DOUBLE)
    }

    fun getString(
        jsonObject: JSONObject?,
        key: String?
    ): String {
        return getString(jsonObject, key, "")
    }

    fun getString(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: String
    ): String {
        return getValueByType(jsonObject, key, defaultValue, TYPE_STRING)
    }

    fun getString(
        json: String?,
        key: String?
    ): String {
        return getString(json, key, "")
    }

    fun getString(
        json: String?,
        key: String?,
        defaultValue: String
    ): String {
        return getValueByType(json, key, defaultValue, TYPE_STRING)
    }

    fun getJSONObject(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: JSONObject
    ): JSONObject {
        return getValueByType(jsonObject, key, defaultValue, TYPE_JSON_OBJECT)
    }

    fun getJSONObject(
        json: String?,
        key: String?,
        defaultValue: JSONObject
    ): JSONObject {
        return getValueByType(json, key, defaultValue, TYPE_JSON_OBJECT)
    }

    fun getJSONArray(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: JSONArray
    ): JSONArray {
        return getValueByType(jsonObject, key, defaultValue, TYPE_JSON_ARRAY)
    }

    fun getJSONArray(
        json: String?,
        key: String?,
        defaultValue: JSONArray
    ): JSONArray {
        return getValueByType(json, key, defaultValue, TYPE_JSON_ARRAY)
    }

    private fun <T> getValueByType(
        jsonObject: JSONObject?,
        key: String?,
        defaultValue: T,
        type: Byte
    ): T {
        return if (jsonObject == null || key == null || key.length == 0) {
            defaultValue
        } else try {
            val ret: Any
            ret = if (type == TYPE_BOOLEAN) {
                jsonObject.getBoolean(key)
            } else if (type == TYPE_INT) {
                jsonObject.getInt(key)
            } else if (type == TYPE_LONG) {
                jsonObject.getLong(key)
            } else if (type == TYPE_DOUBLE) {
                jsonObject.getDouble(key)
            } else if (type == TYPE_STRING) {
                jsonObject.getString(key)
            } else if (type == TYPE_JSON_OBJECT) {
                jsonObject.getJSONObject(key)
            } else if (type == TYPE_JSON_ARRAY) {
                jsonObject.getJSONArray(key)
            } else {
                return defaultValue
            }
            ret as T
        } catch (e: JSONException) {
            Log.e("JsonUtils", "getValueByType: ", e)
            defaultValue
        }
    }

    private fun <T> getValueByType(
        json: String?,
        key: String?,
        defaultValue: T,
        type: Byte
    ): T {
        return if (json == null || json.length == 0 || key == null || key.length == 0) {
            defaultValue
        } else try {
            getValueByType(JSONObject(json), key, defaultValue, type)
        } catch (e: JSONException) {
            Log.e("JsonUtils", "getValueByType: ", e)
            defaultValue
        }
    }

    @JvmOverloads
    fun formatJson(json: String, indentSpaces: Int = 4): String {
        try {
            var i = 0
            val len = json.length
            while (i < len) {
                val c = json[i]
                if (c == '{') {
                    return JSONObject(json).toString(indentSpaces)
                } else if (c == '[') {
                    return JSONArray(json).toString(indentSpaces)
                } else if (!Character.isWhitespace(c)) {
                    return json
                }
                i++
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return json
    }

}