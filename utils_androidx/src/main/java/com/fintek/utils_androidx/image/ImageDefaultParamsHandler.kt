package com.fintek.utils_androidx.image

import android.media.ExifInterface
import androidx.collection.SparseArrayCompat
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
        struct.latitude = attributes.get(ExifInterface.TAG_GPS_LATITUDE.paramIndex())
        struct.latitudeRef = attributes.get(ExifInterface.TAG_GPS_LATITUDE_REF.paramIndex())
        struct.dateTime = attributes.get(ExifInterface.TAG_DATETIME.paramIndex())

        struct.apply {
            latitude = attributes.get(ExifInterface.TAG_GPS_LATITUDE.paramIndex())
            latitudeRef = attributes.get(ExifInterface.TAG_GPS_LATITUDE_REF.paramIndex())
            longitude = attributes.get(ExifInterface.TAG_GPS_LONGITUDE.paramIndex())
            longitudeRef = attributes.get(ExifInterface.TAG_GPS_LONGITUDE_REF.paramIndex())
            gpsTimeStamp = attributes.get(ExifInterface.TAG_GPS_TIMESTAMP.paramIndex())
            make = attributes.get(ExifInterface.TAG_MAKE.paramIndex())

            if (filePath != null) {
                size = "${File(filePath).length()} byte"
            }
        }

        return struct
    }
}