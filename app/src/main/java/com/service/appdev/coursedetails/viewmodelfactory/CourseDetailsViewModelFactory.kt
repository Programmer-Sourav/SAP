package com.service.appdev.coursedetails.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.ApplicationFormViewModel
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel

class CourseDetailsViewModelFactory (private val collegeManagementRepository: CollegeManagementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(CourseDetailsViewModel::class.java)){
            return CourseDetailsViewModel(collegeManagementRepository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}