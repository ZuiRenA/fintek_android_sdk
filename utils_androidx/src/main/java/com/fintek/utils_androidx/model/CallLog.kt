package com.fintek.utils_androidx.model

import android.os.Parcel
import android.os.Parcelable
import com.fintek.utils_androidx.Optional
import com.fintek.utils_androidx.OptionalInt

/**
 * @see <a href="https://developer.android.com/reference/android/provider/CallLog.Calls#top_of_page">
 */
data class CallLog(
    /**
     * The type of the call (incoming, outgoing or missed).
     * e.g. incoming = 1,
     *
     * e.g. outgoing = 2,
     *
     * e.g. missed   = 3,
     */
    @OptionalInt(anyOf = [0, 1, 2, 3])
    var type: Int = 0,

    /**
     * The cached name associated with the phone number, if it exists.
     *
     * This value is typically filled in by the dialer app for the caching purpose,
     * so it's not guaranteed to be present,
     * and may not be current if the contact information associated with this number has changed.
     */
    var name: String? = null,

    /**
     * The phone number as the user entered it.
     */
    var phone: String? = null,


    /**
     * The date the call occured, in milliseconds since the epoch
     */
    var time: Long = 0L,

    /**
     * The duration of the call in seconds
     */
    var duration: Int = 0
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString(),
        parcel.readString(),
        parcel.readLong(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(type)
        parcel.writeString(name)
        parcel.writeString(phone)
        parcel.writeLong(time)
        parcel.writeInt(duration)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CallLog> {
        override fun createFromParcel(parcel: Parcel): CallLog {
            return CallLog(parcel)
        }

        override fun newArray(size: Int): Array<CallLog?> {
            return arrayOfNulls(size)
        }
    }

}