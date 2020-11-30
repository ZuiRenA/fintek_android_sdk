package com.fintek.utils_androidx.language

import com.fintek.utils_androidx.FintekUtils
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
    fun getIso3Language(): String = try {
        var language = ""

        getCurrentLocale()?.let {
            language = it.isO3Language
        }

        language
    } catch (e: Exception) {
        ""
    }

    /**
     * Return locale iso3country
     */
    @JvmStatic
    fun getIso3Country(): String = try {
        var country = ""
        getCurrentLocale()?.let {
            country = it.isO3Country
        }
        country
    } catch (e: Exception) {
        ""
    }

    /**
     * Return locale display language
     */
    @JvmStatic
    fun getDisplayLanguage(): String = try {
        var displayLanguage = ""
        getCurrentLocale()?.let {
            displayLanguage = it.displayLanguage
        }
        displayLanguage
    } catch (e: Exception) {
        ""
    }
}