package com.fintek.utils_androidx.throwable

import com.fintek.utils_androidx.FintekUtils

fun catchOrZero(
    defaultValue: Int = 0,
    block: () -> Int
): Int = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    defaultValue
}

fun catchOrZeroDouble(
    defaultValue: Double = 0.0,
    block: () -> Double
): Double = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    defaultValue
}

fun catchOrEmpty(
    defaultValue: String = "",
    block: () -> String,
): String = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    defaultValue
}

fun catchOrBoolean(
    defaultValue: Boolean = false,
    block: () -> Boolean
): Boolean = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    defaultValue
}

fun catchOrLong(
    defaultValue: Long = 0L,
    block: () -> Long
): Long = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    defaultValue
}

inline fun <T> safely(block: () -> T): T? = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    null
}

inline fun <T> safely(defaultValue: T, block: () -> T): T = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
    defaultValue
}

inline fun safelyVoid(block: () -> Unit) = try {
    block()
} catch (t: Throwable) {
    throwableWithDefault(t)
}

fun throwableWithDefault(t: Throwable) {
    if (FintekUtils.isThrowable) {
        Thread.getDefaultUncaughtExceptionHandler()?.uncaughtException(
            Thread.currentThread(),
            FintekSDKException(t)
        )
    }
}