package com.fintek.utils_androidx.image

import android.media.ExifInterface
import androidx.collection.SparseArrayCompat
import com.fintek.utils_androidx.log.TimberUtil
import com.fintek.utils_androidx.model.ImageInfo
import java.io.File

/**
 * Created by ChaoShen on 2020/11/16
 */
internal class ImageDefaultParamsHandler(
    private val filePath: String? = null
) : IImageStruct<ImageInfo> {
    override fun queryAttributeParams(): Array<String> = arrayOf(
        ExifInterface.TAG_GPS_LATITUDE, ExifInterface.TAG_GPS_LATITUDE_REF,
        ExifInterface.TAG_GPS_LONGITUDE, ExifInterface.TAG_GPS_LONGITUDE_REF,
        ExifInterface.TAG_DATETIME, ExifInterface.TAG_GPS_TIMESTAMP,
        ExifInterface.TAG_MAKE
    )

    override fun structHandler(attributes: SparseArrayCompat<String>): ImageInfo {
        val struct = ImageInfo()

        struct.apply {
            val latitudeRef = attributes.get(ExifInterface.TAG_GPS_LATITUDE_REF.paramIndex())
            val latitudeInternal = attributes.get(ExifInterface.TAG_GPS_LATITUDE.paramIndex())
            latitude = if (latitudeRef == "N") convertToDegree(latitudeInternal) else 0 - convertToDegree(latitudeInternal)

            val longitudeRef = attributes.get(ExifInterface.TAG_GPS_LONGITUDE_REF.paramIndex())
            val longitudeInternal = attributes.get(ExifInterface.TAG_GPS_LONGITUDE.paramIndex())
            longitude = if (longitudeRef == "E") convertToDegree(longitudeInternal) else 0 - convertToDegree(longitudeInternal)

            gpsTimeStamp = attributes.get(ExifInterface.TAG_GPS_TIMESTAMP.paramIndex())
            make = attributes.get(ExifInterface.TAG_MAKE.paramIndex())
            struct.dateTime = attributes.get(ExifInterface.TAG_DATETIME.paramIndex())

            if (filePath != null) {
                size = "${File(filePath).length()} byte"
            }
        }

        return struct
    }

    private fun convertToDegree(stringDMS: String?): Float {
        if (stringDMS.isNullOrEmpty()) return 0f
        val dms = stringDMS.split(",".toRegex(), 3).toTypedArray()
        val stringD = dms[0].split("/".toRegex(), 2).toTypedArray()
        val d0: Double = stringD[0].toDouble()
        val d1: Double = stringD[1].toDouble()
        val floatD = d0 / d1
        val stringM = dms[1].split("/".toRegex(), 2).toTypedArray()
        val m0: Double = stringM[0].toDouble()
        val m1: Double = stringM[1].toDouble()
        val floatM = m0 / m1
        val stringS = dms[2].split("/".toRegex(), 2).toTypedArray()
        val s0: Double = stringS[0].toDouble()
        val s1: Double = stringS[1].toDouble()
        val floatS = s0 / s1
        return (floatD + floatM / 60 + floatS / 3600).toFloat()
    }
}