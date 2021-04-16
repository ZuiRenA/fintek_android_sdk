package com.fintek.utils_androidx.model

/**
 * Created by ChaoShen on 2021/4/16
 */
data class Calendar(
    val eventTitle: String,

    val eventId: Int,

    val endTime: Long,

    val startTime: Long,

    val des: String,

    val reminders: List<Reminder>
)

data class Reminder(
    val eventId: Int,

    val method: Int,

    val minutes: Int,

    val reminderId: Int,
)