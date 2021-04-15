package com.fintek.utils_mexico.ext

fun catchOrZero(
    block: () -> Int
): Int = try {
    block()
} catch (e: Exception) {
    0
} catch (t: Throwable) {
    0
}