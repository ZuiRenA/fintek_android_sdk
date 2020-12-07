package com.fintek.model

/**
 * Created by ChaoShen on 2020/12/2
 */

data class AppConfigReq(
    /**
     * 这个的值必须是[AppConfigReq.AppConfigTypeEnum]的enumName
     */
    val appConfigTypeEnumsReqs: List<String>
) {
    sealed class AppConfigTypeEnum(@get:JvmName("enumName") val enumName: String) {
        object CurrentHtmlVersion : AppConfigTypeEnum("current_html_version")
        object AlertFlag : AppConfigTypeEnum("alert_flag")
        object AlertContent : AppConfigTypeEnum("alert_content")
    }
}