package com.service.appdev.coursedetails.repository

import android.util.Log
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.CollegeResponse
import com.service.appdev.coursedetails.models.CourseResponse

class CollegeManagementRepository (private val apiService: ApiService) {
    suspend fun retrieveCollegeDetails() : CollegeResponse {
        val response = apiService.getAvailableCollegesList();
        Log.i("API Response", response.toString())
        return response;
    }

    suspend fun retrieveCourseDetails(selectedCollege: String): CourseResponse {
        val response = apiService.getAvailableCourseList(selectedCollege);
        Log.i("API Response", response.toString())
        return response;
    }
}