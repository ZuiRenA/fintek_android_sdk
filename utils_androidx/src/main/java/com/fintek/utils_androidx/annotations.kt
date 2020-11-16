package com.fintek.utils_androidx


/**
 * Param nullable, most of the time used in struct
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
internal annotation class Optional(
    /**
     * example text
     */
    val example: String = "",

    /**
     * example texts
     */
    val anyOf: Array<String> = [],
)

/**
 * Param nullable, most of the time used in struct
 */
@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.SOURCE)
@MustBeDocumented
internal annotation class OptionalInt(
    /**
     * example text
     */
    val example: Int = 0,

    /**
     * example texts
     */
    val anyOf: IntArray = [],
)