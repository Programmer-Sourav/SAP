package com.service.appdev.coursedetails.repository

import android.util.Log
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApplicationFormResponseWrapper
import com.service.appdev.coursedetails.models.CoursesAvailableList

class ApplicationUploadRepository(private val apiService: ApiService) {
    suspend fun saveApplicationDetails(
        firstName: String, lastName: String,
        parentFirstName: String, parentLastName: String,
        mothersFirstName: String,
        mothersLastName: String,
        twelfthMarks: String,
        tenthMarks: String, selectEntranceType: String,
        jointEntranceRank: String, streetAddress1: String,
        streetAddress2: String, city: String,
        state: String, pinCodeStr: String,
        phoneNumber: String, courseWillingToStudy: String,
        studentEmail: String,
        username: String,
        selectedCollege: String,
        schoolName: String,
        passingYear: String,
        rollNumber: String,
        totalMarks: String,
        obtainedMarks: String,
        scoreTenth: String,
        schoolName12th: String,
        passingYear12th: String,
        rollNumber12th: String,
        totalMarks12th: String,
        obtainedMarks12th: String,
        score12th: String,
        selectedRadioText: String,
    ): ApplicationFormResponseWrapper {

        // Prepare the application data as a map
        val dataBody = mapOf(
            "firstname" to firstName,
            "lastname" to lastName,
            "parent_first_name" to parentFirstName,
            "parent_last_name" to parentLastName,
            "mothers_first_name" to mothersFirstName,
            "mothers_last_name" to mothersLastName,
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
            "college_willing_to_study" to selectedCollege,
            "studentEmail" to studentEmail,
            "username" to username,
            "schoolName" to schoolName,
            "passingYear" to passingYear,
            "rollNumber" to rollNumber,
            "totalMarks" to totalMarks,
            "obtainedMarks" to obtainedMarks,
            "scoreTenth" to scoreTenth,
            "schoolName12th" to schoolName12th,
            "passingYear12th" to passingYear12th,
            "rollNumber12th" to rollNumber12th,
            "totalMarks12th" to totalMarks12th,
            "obtainedMarks12th" to obtainedMarks12th,
            "score12th" to score12th,
            "caste" to selectedRadioText
        );
       Log.d("Snath ", "Databody "+dataBody);
        // Make the API call
        val response = apiService.saveApplicationDetails(dataBody);
        Log.i("API Response", response.toString())
        return response;
    }

    suspend fun getCourses(): CoursesAvailableList {
        return apiService.getCourses();
    }
}