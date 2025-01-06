package com.service.appdev.coursedetails.models

import android.os.Message

data class AnnouncementResponse (val response: AnnouncementDataResponse) {}

data class AnnouncementDataResponse(val message: String, val data: ArrayList<AnnouncementData>){}