package com.fintek.utils_androidx.model

import android.os.Parcel
import android.os.Parcelable


/**
 * Sms struct it is [Parcelable]
 */
data class Sms(
    /**
     * sender name
     */
    var senderName: String = "",

    var phone: String = "",

    /**
     * receive or send flag
     */
    var type: Int = 0,

    /**
     * receiver name
     */
    var receiverName: String = "",

    var time: String = "",

    /**
     * sms content
     */
    var body: String = "",

) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(senderName)
        parcel.writeString(phone)
        parcel.writeInt(type)
        parcel.writeString(receiverName)
        parcel.writeString(time)
        parcel.writeString(body)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sms> {
        override fun createFromParcel(parcel: Parcel): Sms {
            return Sms(parcel)
        }

        override fun newArray(size: Int): Array<Sms?> {
            return arrayOfNulls(size)
        }
    }
}