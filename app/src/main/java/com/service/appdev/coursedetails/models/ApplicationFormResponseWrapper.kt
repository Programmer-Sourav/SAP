package com.service.appdev.coursedetails.models

data class ApplicationFormResponseWrapper(
    val response: ApplicationFormResponse
)

data class ApplicationFormResponse(
    val status: Int,
    val success: Boolean,
    val message: String,
    val error: String
)
