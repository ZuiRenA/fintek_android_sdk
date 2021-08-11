package com.fintek.util

import android.util.Log
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.fintek.utils_androidx.log.TimberUtil

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.pow

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    @Test
    fun useAppContext() {
        // Context of the app under test.
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.fintek.util", appContext.packageName)
    }

    @Test
    fun testBigInt2Long() {
        val millisError: Int = System.currentTimeMillis().toInt()
        val date = with(DATE_FORMAT) {
            format(Date(fac(millisError)))
        }

        val dateTest = with(DATE_FORMAT) {
            val dateInternal = Date(121, 6, 25, 13, 24, 5)
            val dateMills: Int = dateInternal.time.toInt()
            format(Date(fac(dateMills)))
        }

        val dateTest2 = with(DATE_FORMAT) {
            val dateInternal = Date(109, 3, 21, 19, 48, 13)
            val dateMillis: Int = dateInternal.time.toInt()
            format(Date(fac(dateMillis)))
        }

        TimberUtil.e(
            "date: $date",
            "dateTest: $dateTest",
            "dateTest2: $dateTest2"
        )
    }

    private fun fac(n: Int): Long {
        return n + 379 * INT_FLOW
    }

    companion object {
        private val DATE_FORMAT = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        private const val INT_FLOW: Long = (Int.MAX_VALUE.toLong() + 1) * 2
    }
}