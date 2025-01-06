package com.service.appdev.coursedetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.models.UserData
import com.service.appdev.coursedetails.repository.LoginManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginViewModel(private val loginManagementRepository: LoginManagementRepository) : ViewModel(){
    private val mutableLiveData  = MutableLiveData<LoginState>()
    val loginLiveData : LiveData<LoginState> = mutableLiveData;


    fun userLogin( username: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = loginManagementRepository.attemptLogin(username, password);
                withContext(Dispatchers.Main){
                    mutableLiveData.value = LoginState.Success(response.userDetails)
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    mutableLiveData.value = e.message?.let { LoginState.Error(it) }
                }
            }
        }
    }

    fun adminLogin( username: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = loginManagementRepository.attemptAdminLogin(username, password);
                withContext(Dispatchers.Main){
                    mutableLiveData.value = LoginState.Success(response.userDetails)
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    mutableLiveData.value = e.message?.let { LoginState.Error(it) }
                }
            }
        }
    }

    fun instituteLogin( username: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = loginManagementRepository.attemptInstituteLogin(username, password);
                withContext(Dispatchers.Main){
                    mutableLiveData.value = LoginState.Success(response.userDetails)
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    mutableLiveData.value = e.message?.let { LoginState.Error(it) }
                }
            }
        }
    }
    fun userSignUp(name: String, userPhone: String, password: String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response =
                        loginManagementRepository.attemptSignUp(name, userPhone, password);
                withContext(Dispatchers.Main) {
                    mutableLiveData.value = LoginState.Success(response.userDetails);
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main){
                    mutableLiveData.value = LoginState.Error(e.message!!);
                }
            }
        }
    }




    sealed class  LoginState(){
        data class Success(val message: UserData) : LoginState()
        data class Error (val message: String) : LoginState()
    }

}