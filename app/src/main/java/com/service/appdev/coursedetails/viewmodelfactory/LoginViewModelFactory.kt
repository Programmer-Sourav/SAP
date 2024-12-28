package com.service.appdev.coursedetails.viewmodelfactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.repository.LoginManagementRepository
import com.service.appdev.coursedetails.viewmodel.LoginViewModel

class LoginViewModelFactory (private val loginManagementRepository: LoginManagementRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(LoginViewModel::class.java)){
            return LoginViewModel(loginManagementRepository) as T
        }
        throw  IllegalArgumentException("Unknown Viewmodel Class")
    }
}