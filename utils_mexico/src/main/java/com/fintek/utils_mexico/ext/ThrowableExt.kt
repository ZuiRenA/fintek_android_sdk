package com.fintek.utils_mexico.ext

fun catchOrZero(
    defaultValue: Int = 0,
    block: () -> Int
): Int = try {
    block()
} catch (e: Exception) {
    defaultValue
} catch (t: Throwable) {
    defaultValue
}

fun catchOrZeroDouble(
    defaultValue: Double = 0.0,
    block: () -> Double
): Double = try {
    block()
} catch (e: Exception) {
    defaultValue
} catch (t: Throwable) {
    defaultValue
}

fun catchOrEmpty(
    defaultValue: String = "",
    block: () -> String,
): String = try {
    block()
} catch (e: Exception) {
    defaultValue
} catch (e: Throwable) {
    defaultValue
}