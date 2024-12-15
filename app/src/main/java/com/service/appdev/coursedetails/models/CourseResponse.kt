package com.service.appdev.coursedetails.models

import android.os.Parcel
import android.os.Parcelable

data class CourseResponse( val response: ResponseData)

data class ResponseData(val status: Int, val message: String, val error: String, val courses : ArrayList<CourseDetails>)

data class CourseDetails(val id: String, val courseId: String, val courseName: String, val courseDuration: String,
    val seatCapacity: String, val courseDescription: String, val collegeName: String, val img_url: String) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(courseId)
        parcel.writeString(courseName)
        parcel.writeString(courseDuration)
        parcel.writeString(seatCapacity)
        parcel.writeString(courseDescription)
        parcel.writeString(collegeName)
        parcel.writeString(img_url)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<CourseDetails> {
        override fun createFromParcel(parcel: Parcel): CourseDetails {
            return CourseDetails(parcel)
        }

        override fun newArray(size: Int): Array<CourseDetails?> {
            return arrayOfNulls(size)
        }
    }
}

