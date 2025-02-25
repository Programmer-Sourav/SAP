package com.service.appdev.coursedetails.models

data class AddedCourseResponse(val response: AddedCourseResponseChild) {
}

data class AddedCourseResponseChild(val status: String, val success: String, val message: String, val data : AddedCourseData){}

data class AddedCourseData(val coursename: String){}