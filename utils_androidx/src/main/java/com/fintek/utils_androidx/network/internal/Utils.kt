package com.fintek.utils_androidx.network.internal

import com.fintek.utils_androidx.network.Headers.Companion.headersOf
import com.fintek.utils_androidx.network.RequestBody.Companion.toRequestBody
import java.net.IDN
import java.net.InetAddress
import java.util.*

/**
 * Quick and dirty pattern to differentiate IP addresses from hostnames. This is an approximation
 * of Android's private InetAddress#isNumeric API.
 *
 * This matches IPv6 addresses as a hex string containing at least one colon, and possibly
 * including dots after the first colon. It matches IPv4 addresses as strings containing only
 * decimal digits and dots. This pattern matches strings like "a:.23" and "54" that are neither IP
 * addresses nor hostnames; they will be verified as IP addresses (which is a more strict
 * verification).
 */
private val VERIFY_AS_IP_ADDRESS =
    "([0-9a-fA-F]*:[0-9a-fA-F:.]*)|([\\d.]+)".toRegex()

@JvmField
internal val EMPTY_BYTE_ARRAY = ByteArray(0)
@JvmField
internal val EMPTY_HEADERS = headersOf()

@JvmField
internal val EMPTY_REQUEST = EMPTY_BYTE_ARRAY.toRequestBody()


/**
 * Returns the index of the first character in this string that contains a character in
 * [delimiters]. Returns endIndex if there is no such character.
 */
internal fun String.delimiterOffset(delimiters: String, startIndex: Int = 0, endIndex: Int = length): Int {
    for (i in startIndex until endIndex) {
        if (this[i] in delimiters) return i
    }
    return endIndex
}

/** Returns true if this string is not a host name and might be an IP address. */
internal fun String.canParseAsIpAddress(): Boolean {
    return VERIFY_AS_IP_ADDRESS.matches(this)
}

internal fun Char.parseHexDigit(): Int = when (this) {
    in '0'..'9' -> this - '0'
    in 'a'..'f' -> this - 'a' + 10
    in 'A'..'F' -> this - 'A' + 10
    else -> -1
}

/** GMT and UTC are equivalent for our purposes. */
@JvmField
internal val UTC = TimeZone.getTimeZone("GMT")!!

/** Returns a [Locale.US] formatted [String]. */
internal fun format(format: String, vararg args: Any): String {
    return String.format(Locale.US, format, *args)
}

/** Returns an immutable copy of this. */
internal fun <K, V> Map<K, V>.toImmutableMap(): Map<K, V> {
    return if (isEmpty()) {
        emptyMap()
    } else {
        Collections.unmodifiableMap(LinkedHashMap(this))
    }
}