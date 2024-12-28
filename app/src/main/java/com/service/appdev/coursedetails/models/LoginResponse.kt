package com.service.appdev.coursedetails.models

data class LoginResponse (val status: String, val message: String, val userDetails: UserData) {}

data class UserData(val authToken: String, val userId: String, val username: String){}