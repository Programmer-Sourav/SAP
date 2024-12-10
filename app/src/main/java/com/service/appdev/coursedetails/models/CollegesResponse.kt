package com.service.appdev.coursedetails.models


data class CollegeResponse(
    val response: Response
)

data class Response(
    val status: Int,
    val message: String?,
    val data: ArrayList<CollegeDetails>
)

data class CollegeDetails(
    val id: Int,
    val collegeId: String?,
    val collegeName: String,
    val collegeContact: String?,
    val collegeAddress: String?,
    val collegeFacilities: String?
)
