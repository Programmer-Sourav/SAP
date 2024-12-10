package com.service.appdev.coursedetails.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import com.service.appdev.coursedetails.viewmodel.ApplicationFormViewModel

class ApplicationFormViewModelFactory (private val applicationUploadRepository: ApplicationUploadRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(ApplicationFormViewModel::class.java)){
            return ApplicationFormViewModel(applicationUploadRepository) as T
        }
        throw IllegalArgumentException("Unknown Viewmodel Class")
    }
}