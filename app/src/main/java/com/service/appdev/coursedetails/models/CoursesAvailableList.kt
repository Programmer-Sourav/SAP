package com.service.appdev.coursedetails.models

data class CoursesAvailableList(val response: CoursesResponseChild) {}

data class CoursesResponseChild(val status: String,  val message: String, val data : ArrayList<CoursesAvailableData>){}

data class CoursesAvailableData(val id: String, val coursename: String){}