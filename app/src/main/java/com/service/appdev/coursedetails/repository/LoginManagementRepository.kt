package com.service.appdev.coursedetails.repository

import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.LoginResponse

class LoginManagementRepository(private val apiService: ApiService) {

    suspend fun attemptLogin(username: String, password: String): LoginResponse{
        val dataBody = mapOf("username" to username , "password" to password)
        val response = apiService.attemptLogin(dataBody);
        return response;
    }
    suspend fun attemptAdminLogin(username: String, password: String): LoginResponse{
        val dataBody = mapOf("username" to username, "password" to password)
        val response = apiService.attemptAdminLogin(dataBody);
        return response;
    }
    suspend fun attemptInstituteLogin(username: String, password: String): LoginResponse{
        val dataBody = mapOf("username" to username, "password" to password)
        val response = apiService.attemptInstituteLogin(dataBody);
        return response;
    }

    suspend fun attemptSignUp(name: String, userPhone: String, password: String) : LoginResponse{
        val dataBody = mapOf( "username" to name, "userPhone" to userPhone, "password" to password)
        val response = apiService.attemptRegistration(dataBody);
        return response;
    }
}