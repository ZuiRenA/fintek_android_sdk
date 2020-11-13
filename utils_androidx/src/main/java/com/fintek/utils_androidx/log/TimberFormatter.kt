package com.fintek.utils_androidx.log

import android.content.ClipData
import android.content.Intent
import android.os.Build
import android.os.Bundle
import com.fintek.utils_androidx.UtilsBridge
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.StringReader
import java.io.StringWriter
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource

/**
 * Created by ChaoShen on 2020/11/12
 */
object TimberFormatter {

    fun object2String(any: Any?): String = object2String(any, -1)

    fun object2String(any: Any?, type: Int): String {
        if (any == null) return NULL
        if (any.javaClass.isArray) return array2String(any)
        if (any is Throwable) return UtilsBridge.getFullStackTrace(any)
        if (any is Bundle) return bundle2String(any)
        if (any is Intent) return object2String(any)
        when(type) {
            JSON -> object2String(any)
            XML -> formatXml(any.toString())
        }
        return any.toString()
    }

    private fun bundle2String(bundle: Bundle): String {
        val iterator = bundle.keySet().iterator()
        if (!iterator.hasNext()) return "Bundle {}"

        val sb = StringBuilder(128).append("Bundle { ")
        while (true) {
            val key = iterator.next()
            val value = bundle.get(key)
            sb.append(key).append("=")
            if (value is Bundle) {
                sb.append(if (value == bundle) "(this Bundle)" else bundle2String(value))
            } else {
                sb.append(TimberUtil.formatObject(value))
            }

            if (!iterator.hasNext()) return sb.append(" }").toString()
            sb.append(',').append(' ')
        }
    }

    private fun intent2String(intent: Intent): String {
        val sb = StringBuilder(128)
        sb.append("Intent {")
        var first = true

        val firstAppend = {
            if (!first) sb.append(' ')
            first = false
        }

        if (intent.action != null) {
            sb.append("action=").append(intent.action)
            first = false
        }

        val categories = intent.categories
        if (!categories.isNullOrEmpty()) {
            firstAppend()

            sb.append("categories=[")
            var firstCategory = true
            categories.forEach {
                if (!firstCategory) sb.append(',')
                sb.append(it)
                firstCategory = false
            }
            sb.append("]")
        }

        intent.run {

            data?.let {
                firstAppend()
                sb.append("data=").append(it)
            }

            type?.let {
                firstAppend()
                sb.append("type=").append(it)
            }

            if (flags != 0) {
                firstAppend()
                sb.append("flags=0x").append(flags.toHexString())
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
                `package`?.let {
                    firstAppend()
                    sb.append("package=").append(it)
                }
            }

            component?.let {
                firstAppend()
                sb.append("component=").append(it)
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR_MR1) {
                sourceBounds?.let {
                    firstAppend()
                    sb.append("sourceBounds=").append(it)
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                intent.clipData?.let {
                    firstAppend()
                    clipData2String(it, sb)
                }
            }

            extras?.let {
                firstAppend()
                sb.append("extras={").append(bundle2String(it)).append('}')
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
                intent.selector?.let {
                    firstAppend()
                    sb.append("selector={").append(
                        if (it == intent) "(this Intent)" else intent2String(
                            it
                        )
                    )
                        .append("}")
                }
            }

            sb.append(" }")
        }

        return sb.toString()
    }

    private fun object2Json(any: Any?): String {
        if (any is CharSequence) return UtilsBridge.formatJson(any.toString())
        return CONFIG.jsonConvert?.convert(any) ?: any.toString()
    }

    private fun formatJson(json: String): String {
        try {
            json.forEach {
                when {
                    it == '{' -> return JSONObject(json).toString(2)
                    it == '[' -> return JSONArray(json).toString(2)
                    !Character.isWhitespace(it) -> return json
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }

        return json
    }

    private fun formatXml(xml: String): String {
        var xmlShadow = xml
        try {
            val xmlInput: Source = StreamSource(StringReader(xmlShadow))
            val xmlOutput = StreamResult(StringWriter())
            val transformer = TransformerFactory.newInstance().newTransformer()
            transformer.setOutputProperty(OutputKeys.INDENT, "yes")
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
            transformer.transform(xmlInput, xmlOutput)
            xmlShadow = xmlOutput.writer.toString()
                .replaceFirst(">".toRegex(), ">$LINE_SEP")
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return xmlShadow
    }

    private fun array2String(any: Any?): String {
        return when (any) {
            is Array<*> -> any.contentDeepToString()
            is BooleanArray -> any.contentToString()
            is ByteArray -> any.contentToString()
            is CharArray -> any.contentToString()
            is DoubleArray -> any.contentToString()
            is FloatArray -> any.contentToString()
            is IntArray -> any.contentToString()
            is LongArray -> any.contentToString()
            is ShortArray -> any.contentToString()
            else -> throw IllegalArgumentException("Array has incompatible type: " + any?.javaClass)
        }
    }

    private fun clipData2String(clipData: ClipData, sb: java.lang.StringBuilder) {
        val item = clipData.getItemAt(0)
        if (item == null) {
            sb.append("ClipData.Item {}")
            return
        }
        sb.append("ClipData.Item { ")
        val mHtmlText = item.htmlText
        if (mHtmlText != null) {
            sb.append("H:")
            sb.append(mHtmlText)
            sb.append("}")
            return
        }
        val mText = item.text
        if (mText != null) {
            sb.append("T:")
            sb.append(mText)
            sb.append("}")
            return
        }
        val uri = item.uri
        if (uri != null) {
            sb.append("U:").append(uri)
            sb.append("}")
            return
        }
        val intent = item.intent
        if (intent != null) {
            sb.append("I:")
            sb.append(intent2String(intent))
            sb.append("}")
            return
        }
        sb.append("NULL")
        sb.append("}")
    }

    private fun Int.toHexString(): String = Integer.toHexString(this)
}