package com.fintek.utils_mexico.language

import com.fintek.utils_androidx.language.LanguageUtils

/**
 * Created by ChaoShen on 2021/4/15
 */
object LanguageMexicoUtils {

    @JvmStatic
    fun getLanguage(): String = LanguageUtils.getCurrentLocale().language
}