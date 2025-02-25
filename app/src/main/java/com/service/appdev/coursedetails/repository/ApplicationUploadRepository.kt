package com.service.appdev.coursedetails.repository

import android.util.Log
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApplicationFormResponseWrapper
import com.service.appdev.coursedetails.models.CoursesAvailableList

class ApplicationUploadRepository(private val apiService: ApiService) {
    suspend fun saveApplicationDetails(
        firstName: String, lastName: String,
        parentFirstName: String, parentLastName: String,
        fullSchoolName: String, twelfthMarks: String,
        tenthMarks: String, selectEntranceType: String,
        jointEntranceRank: String, streetAddress1: String,
        streetAddress2: String, city: String,
        state: String, pinCodeStr: String,
        phoneNumber: String, courseWillingToStudy: String,
        studentEmail : String,
        username: String,
        selectedCollege: String
    ): ApplicationFormResponseWrapper {

        // Prepare the application data as a map
        val dataBody = mapOf(
            "firstname" to firstName,
            "lastname" to lastName,
            "parent_first_name" to parentFirstName,
            "parent_last_name" to parentLastName,
            "schoolName" to fullSchoolName,
            "twelfth_marks" to twelfthMarks,
            "madhyamik_marks" to tenthMarks,
            "entrance_test_type" to selectEntranceType,
            "enter_rank" to jointEntranceRank,
            "street_address_1" to streetAddress1,
            "street_address_2" to streetAddress2,
            "city" to city,
            "state" to state,
            "pinCode" to pinCodeStr,
            "phoneNumber" to phoneNumber,
            "course_willing_to_study" to courseWillingToStudy,
            "studentEmail" to studentEmail,
            "username" to username,
            "selectedCollege" to selectedCollege
        );

        // Make the API call
        val response = apiService.saveApplicationDetails(dataBody);
        Log.i("API Response", response.toString())
        return response;
    }

    suspend fun getCourses(): CoursesAvailableList {
        return apiService.getCourses();
    }
}