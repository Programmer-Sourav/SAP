package com.service.appdev.coursedetails.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.AdminManagementViewModel


class AdminManagementViewModelFactory (private val collegeManagementRepository: CollegeManagementRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(AdminManagementViewModel::class.java)){
            return AdminManagementViewModel(collegeManagementRepository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}
