package com.fintek.utils_androidx.language

import com.fintek.utils_androidx.FintekUtils
import com.fintek.utils_androidx.throwable.catchOrEmpty
import java.util.*

/**
 * Created by ChaoShen on 2020/11/18
 */
object LanguageUtils {

    /**
     * Return global locale
     *
     * @return [Locale] used
     */
    @JvmStatic
    fun getCurrentLocale(): Locale = FintekUtils.requiredContext.resources.configuration.locale

    /**
     * Return locale iso3language
     */
    @JvmStatic
    fun getIso3Language(): String = catchOrEmpty {
        var language: String

        getCurrentLocale().let {
            language = it.isO3Language
        }

        language
    }

    /**
     * Return locale iso3country
     */
    @JvmStatic
    fun getIso3Country(): String = catchOrEmpty {
        var country: String
        getCurrentLocale().let {
            country = it.isO3Country
        }
        country
    }

    /**
     * Return locale display language
     */
    @JvmStatic
    fun getDisplayLanguage(): String = catchOrEmpty {
        var displayLanguage: String
        getCurrentLocale().let {
            displayLanguage = it.displayLanguage
        }
        displayLanguage
    }
}