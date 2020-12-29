package com.fintek.utils_androidx.upload.internal

import com.fintek.utils_androidx.upload.UploadUtils
import java.util.*

/**
 * Created by ChaoShen on 2020/12/29
 */
sealed class TimeCycleUnit {
    data class DateTime(
        val year: Int,
        val month: Int,
        val week: Int,
        val day: Int,
        val hour: Int,
        val second: Int,
    )

    abstract fun isSameTime(dateTime: DateTime?): Boolean

    override fun toString(): String = this::class.java.simpleName

    object Second : TimeCycleUnit() {
        override fun isSameTime(dateTime: DateTime?): Boolean {
            if (dateTime == null) return false
            val now = Calendar.getInstance()
            return now.get(Calendar.YEAR) == dateTime.year && now.get(Calendar.MONTH) == dateTime.month &&
                    now.get(Calendar.WEEK_OF_MONTH) == dateTime.week && now.get(Calendar.DAY_OF_MONTH) == dateTime.day &&
                    now.get(Calendar.HOUR_OF_DAY) == dateTime.hour && now.get(Calendar.SECOND) == dateTime.second
        }
    }
    object Hour : TimeCycleUnit() {
        override fun isSameTime(dateTime: DateTime?): Boolean {
            if (dateTime == null) return false
            val now = Calendar.getInstance()
            return now.get(Calendar.YEAR) == dateTime.year && now.get(Calendar.MONTH) == dateTime.month &&
                    now.get(Calendar.WEEK_OF_MONTH) == dateTime.week && now.get(Calendar.DAY_OF_MONTH) == dateTime.day &&
                    now.get(Calendar.HOUR_OF_DAY) == dateTime.hour
        }
    }
    object Day : TimeCycleUnit() {
        override fun isSameTime(dateTime: DateTime?): Boolean {
            if (dateTime == null) return false
            val now = Calendar.getInstance()
            return now.get(Calendar.YEAR) == dateTime.year && now.get(Calendar.MONTH) == dateTime.month &&
                    now.get(Calendar.WEEK_OF_MONTH) == dateTime.week && now.get(Calendar.DAY_OF_MONTH) == dateTime.day
        }
    }

    object Week : TimeCycleUnit() {
        override fun isSameTime(dateTime: DateTime?): Boolean {
            if (dateTime == null) return false
            val now = Calendar.getInstance()
            return now.get(Calendar.YEAR) == dateTime.year && now.get(Calendar.MONTH) == dateTime.month &&
                    now.get(Calendar.WEEK_OF_MONTH) == dateTime.week
        }
    }

    object Month : TimeCycleUnit() {
        override fun isSameTime(dateTime: DateTime?): Boolean {
            if (dateTime == null) return false
            val now = Calendar.getInstance()
            return now.get(Calendar.YEAR) == dateTime.year && now.get(Calendar.MONTH) == dateTime.month
        }
    }

    object Year : TimeCycleUnit() {
        override fun isSameTime(dateTime: DateTime?): Boolean {
            if (dateTime == null) return false
            val now = Calendar.getInstance()
            return now.get(Calendar.YEAR) == dateTime.year
        }
    }
}