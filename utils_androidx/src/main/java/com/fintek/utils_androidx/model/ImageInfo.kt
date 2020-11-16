package com.fintek.utils_androidx.model

import android.os.Parcel
import android.os.Parcelable
import com.fintek.utils_androidx.Optional

/**
 * Created by ChaoShen on 2020/11/16
 */
data class ImageInfo(
    /**
     * latitude，e.g. 31/1,58/1,253560/10000 or null
     */
    @Optional("31/1,58/1,253560/10000")
    var latitude: String? = null,

    /**
     * latitudeRef, it will only in ["N", "S"] or null
     */
    @Optional(anyOf = ["N", "S"])
    var latitudeRef: String? = null,

    /**
     * longitude e.g. 118/1,44/1,491207/10000 or null
     */
    @Optional("118/1,44/1,491207/10000")
    var longitude: String? = null,

    /**
     * longitudeRef, it will only in ["E", "W"] or null
     */
    @Optional(anyOf = ["E", "W"])
    var longitudeRef: String? = null,

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
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(latitude)
        parcel.writeString(latitudeRef)
        parcel.writeString(longitude)
        parcel.writeString(longitudeRef)
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