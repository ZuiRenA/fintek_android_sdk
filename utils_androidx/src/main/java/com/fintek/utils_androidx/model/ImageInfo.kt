package com.fintek.utils_androidx.model

import android.os.Parcel
import android.os.Parcelable
import com.fintek.utils_androidx.Optional

/**
 * Created by ChaoShen on 2020/11/16
 */
data class ImageInfo(
    /**
     * latitude，e.g. 31.2377 or 0.0 or null
     */
    @Optional("31.2377")
    var latitude: Float? = null,

    /**
     * longitude e.g. 121.4256 or 0.0 or null
     */
    @Optional("121.4256")
    var longitude: Float? = null,

    /**
     * shooting time
     */
    @Optional("2020:09:15 17:22:34")
    var dateTime: String? = null,

    /**
     * timestamp e.g. 09:22:32
     */
    @Optional("09:22:32")
    var gpsTimeStamp: String? = null,

    /**
     * image size
     * byte
     */
    @Optional("1845971 byte")
    var size: String? = null,

    /**
     * shooting device, mobile phone manufacturer
     */
    @Optional("Smartisan")
    var make: String? = null
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeFloat(latitude ?: 0f)
        parcel.writeFloat(longitude ?: 0f)
        parcel.writeString(dateTime)
        parcel.writeString(gpsTimeStamp)
        parcel.writeString(size)
        parcel.writeString(make)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ImageInfo> {
        override fun createFromParcel(parcel: Parcel): ImageInfo {
            return ImageInfo(parcel)
        }

        override fun newArray(size: Int): Array<ImageInfo?> {
            return arrayOfNulls(size)
        }
    }


}