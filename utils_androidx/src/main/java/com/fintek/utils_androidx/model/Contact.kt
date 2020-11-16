package com.fintek.utils_androidx.model

import android.os.Build
import android.os.Parcel
import android.os.Parcelable


data class Contact(

    /**
     * The unique ID for a row.
     */
    var id: String? = null,

    /**
     * The display name for the contact.
     */
    var name: String? = null,

    /**
     * An indicator of whether this contact has at least one phone number. "1" if there is
     * at least one phone number, "0" otherwise.
     */
    var hasPhoneNumber: Int = 0,

    /**
     * An indicator of whether this contact is supposed to be visible in the UI.
     * "1" if the contact has at least one raw contact that belongs to a visible group; "0" otherwise.
     */
    var inVisibleGroup: Int = 0,

    /**
     * Flag that reflects whether this contact represents the user's
     * personal profile entry.
     */
    var isUserProfile: String? = null,

    /**
     * The last time a contact was contacted.
     * If you publish your app to the Google Play Store, this field is obsolete, regardless of Android version.
     * For more information,
     * @see [Contacts Provider]{https://developer.android.com/guide/topics/providers/contacts-provider#ObsoleteData} page.
     */
    @Deprecated("If you publish your app to the Google Play Store, this field is obsolete, regardless of Android version.")
    var lastTimeContacted: Int = 0,

    /**
     * Whether the contact should always be sent to voicemail. If missing,
     * defaults to false.
     * Type: INTEGER (0 for false, 1 for true)
     */
    var sendToVoiceMail: Int = 0,

    /**
     * An indicator for favorite contacts: '1' if favorite,
     * '0' otherwise. When raw contacts are aggregated,
     * this field is automatically computed: if any constituent raw contacts are starred,
     * then this field is set to '1'. Setting this field automatically changes the corresponding field on all constituent raw contacts.
     */
    var starred: Int = 0,

    /**
     * The number of times a contact has been contacted.
     */
    @Deprecated("Contacts affinity information is no longer supported as of" +
            "Android version Q[${Build.VERSION_CODES.Q}]. This column" +
            "always contains 0.")
    var timesContacted: Int = 0,

    /**
     * Timestamp (milliseconds since epoch) of when this contact was last updated.  This
     * includes updates to all data associated with this contact including raw contacts.  Any
     * modification (including deletes and inserts) of underlying contact data are also
     * reflected in this timestamp.
     */
    var upTime: String? = null,

    /**
     * Contact phone number, Null when there is no contact,
     * otherwise it is a list
     */
    var phone: List<String>? = null,
): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readString(),
        parcel.createStringArrayList()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeInt(hasPhoneNumber)
        parcel.writeInt(inVisibleGroup)
        parcel.writeString(isUserProfile)
        parcel.writeInt(lastTimeContacted)
        parcel.writeInt(sendToVoiceMail)
        parcel.writeInt(starred)
        parcel.writeInt(timesContacted)
        parcel.writeString(upTime)
        parcel.writeStringList(phone)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Contact> {
        override fun createFromParcel(parcel: Parcel): Contact {
            return Contact(parcel)
        }

        override fun newArray(size: Int): Array<Contact?> {
            return arrayOfNulls(size)
        }
    }
}