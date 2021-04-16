package com.fintek.utils_mexico.location

import android.location.Geocoder
import android.location.Location
import com.fintek.utils_mexico.FintekMexicoUtils
import java.util.*

/**
 * Created by ChaoShen on 2021/4/16
 */
object LocationMexicoUtils {

    @JvmStatic
    fun getAddress(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return null

        val geocoder = Geocoder(FintekMexicoUtils.requiredApplication)
        try {
            val result = geocoder.getFromLocation(latitude, longitude, 1)

            return result[0].getAddressLine(0)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmStatic
    fun getAddressDetail(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return null

        val geocoder = Geocoder(FintekMexicoUtils.requiredApplication)
        try {
            val result = geocoder.getFromLocation(latitude, longitude, 1)

            return result[0].toString()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @JvmStatic
    fun getAddressDetail(location: Location?): String? {
        if (location == null) return null
        return getAddressDetail(location.latitude, location.longitude)
    }

    @JvmStatic
    fun getAddress(location: Location?): String? {
        if (location == null) return null
        return getAddress(location.latitude, location.longitude)
    }
}