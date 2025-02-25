package com.service.appdev.coursedetails.models

data class BrochureResponse(
    val response: ResponseData2
)

data class ResponseData2(
    val status: Int,
    val message: String?,
    val data: ArrayList<CourseBrochure>
)