package com.fintek.utils_androidx.network

import android.view.inputmethod.InputMethodSubtype
import java.nio.charset.Charset
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern


/**
 * Come from OkHttp
 */
class MediaType private constructor(
    private val mediaType: String,

    /**
     * Returns the high-level media type, such as "text", "image", "audio", "video", or "application".
     */
    @get:JvmName("type") val type: String,

    /**
     * Returns a specific media subtype, such as "plain" or "png", "mpeg", "mp4" or "xml".
     */
    @get:JvmName("subtype") val subtype: String,

    /** Alternating parameter names with their values, like `["charset', "utf-8"]`. */
    private val parameterNamesAndValues: Array<String>
) {

    @JvmOverloads
    fun charset(defaultValue: Charset? = null): Charset? {
        val charset = parameter("charset") ?: return defaultValue
        return try {
            Charset.forName(charset)
        } catch (_: IllegalArgumentException) {
            defaultValue // This charset is invalid or unsupported. Give up.
        }
    }

    /**
     * Returns the parameter [name] of this media type, or null if this media type does not define
     * such a parameter.
     */
    fun parameter(name: String): String? {
        for (i in parameterNamesAndValues.indices step 2) {
            if (parameterNamesAndValues[i].equals(name, ignoreCase = true)) {
                return parameterNamesAndValues[i + 1]
            }
        }
        return null
    }

    override fun toString(): String = mediaType

    override fun equals(other: Any?): Boolean = other is MediaType && other.mediaType == this.mediaType

    override fun hashCode(): Int = mediaType.hashCode()

    companion object {
        private const val TOKEN = "([a-zA-Z0-9-!#$%&'*+.^_`{|}~]+)"
        private const val QUOTED = "\"([^\"]*)\""
        private val TYPE_SUBTYPE = Pattern.compile("$TOKEN/$TOKEN")
        private val PARAMETER = Pattern.compile(";\\s*(?:$TOKEN=(?:$TOKEN|$QUOTED))?")

        /**
         * Returns a media type for this string.
         *
         * @throws IllegalArgumentException if this is not a well-formed media type.
         */
        @JvmStatic
        fun String.toMediaType(): MediaType {
            val typeSubtype = TYPE_SUBTYPE.matcher(this)
            require(typeSubtype.lookingAt()) { "No subtype found for: \"$this\"" }
            val type = typeSubtype.group(1).toLowerCase(Locale.US)
            val subtype = typeSubtype.group(2).toLowerCase(Locale.US)

            val parameterNamesAndValues = mutableListOf<String>()
            val parameter = PARAMETER.matcher(this)
            var s = typeSubtype.end()
            while (s < length) {
                parameter.region(s, length)
                require(parameter.lookingAt()) {
                    "Parameter is not formatted correctly: \"${substring(s)}\" for: \"$this\""
                }

                val name = parameter.group(1)
                if (name == null) {
                    s = parameter.end()
                    continue
                }

                val token = parameter.group(2)
                val value = when {
                    token == null -> {
                        // Value is "double-quoted". That's valid and our regex group already strips the quotes.
                        parameter.group(3)
                    }
                    token.startsWith("'") && token.endsWith("'") && token.length > 2 -> {
                        // If the token is 'single-quoted' it's invalid! But we're lenient and strip the quotes.
                        token.substring(1, token.length - 1)
                    }
                    else -> token
                }

                parameterNamesAndValues += name
                parameterNamesAndValues += value
                s = parameter.end()
            }

            return MediaType(this, type, subtype, parameterNamesAndValues.toTypedArray())
        }

        /** Returns a media type for this, or null if this is not a well-formed media type. */
        @JvmStatic
        fun String.toMediaTypeOrNull(): MediaType? {
            return try {
                toMediaType()
            } catch (_: IllegalArgumentException) {
                null
            }
        }

        fun get(mediaType: String): MediaType = mediaType.toMediaType()

        fun parse(mediaType: String): MediaType? = mediaType.toMediaTypeOrNull()
    }
}