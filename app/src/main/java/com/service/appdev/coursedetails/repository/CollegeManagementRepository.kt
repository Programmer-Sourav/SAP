package com.service.appdev.coursedetails.repository

import android.util.Log
import com.service.appdev.coursedetails.models.AnnouncementResponse
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.CollegeResponse
import com.service.appdev.coursedetails.models.CourseResponse
import com.service.appdev.coursedetails.models.ImagesResponse
import com.service.appdev.coursedetails.models.PostAnnouncementResponse

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

    suspend fun retrieveAnnouncementDetails() : AnnouncementResponse{
        val response = apiService.getAnnouncementsList();
        Log.i("API Response", response.toString())
        return response;
    }

    suspend fun postAnnouncementDetails(header: String, notice: String): PostAnnouncementResponse {
        val dataBody = mapOf("header" to header, "notice" to notice);
        val response = apiService.postAnnouncement(dataBody);
        Log.i("API Response", response.toString())
        return response;
    }
    suspend fun getImagesForSlider() : ImagesResponse {
        val response = apiService.getImagesList();
        return response;
    }
}