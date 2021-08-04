package com.fintek.utils_mexico.location

import android.location.Address
import android.location.Geocoder
import android.location.Location
import com.fintek.utils_androidx.throwable.safelyVoid
import com.fintek.utils_mexico.FintekMexicoUtils
import com.fintek.utils_mexico.model.MexicoAddress
import com.squareup.moshi.Moshi
import java.util.*

/**
 * Created by ChaoShen on 2021/4/16
 */
object LocationMexicoUtils {
    private val moshi = Moshi.Builder().build()

    @JvmStatic
    fun getAddress(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return null

        val geocoder = Geocoder(FintekMexicoUtils.requiredApplication)
        safelyVoid {
            val result = geocoder.getFromLocation(latitude, longitude, 1)

            return result[0].getAddressLine(0)
        }

        return null
    }

    @JvmStatic
    fun getAddressDetail(latitude: Double?, longitude: Double?): String? {
        if (latitude == null || longitude == null) return null

        val geocoder = Geocoder(FintekMexicoUtils.requiredApplication)
        safelyVoid {
            val result = geocoder.getFromLocation(latitude, longitude, 1)

            return addressConvert(result.getOrNull(0))
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

    @JvmStatic
    private fun addressConvert(address: Address?): String {
        if (address == null) return ""

        var mexicoAddress: MexicoAddress? = null
        safelyVoid {
            mexicoAddress = MexicoAddress(
                address = address.getAddressLine(0),
                adminArea = address.adminArea,
                countryCode = address.countryCode,
                countryName = address.countryName,
                featureName = address.featureName,
                locality = address.locality
            )
        }
        if (mexicoAddress == null) return ""
        return moshi.adapter(MexicoAddress::class.java).toJson(mexicoAddress)
    }
}