package com.service.appdev.coursedetails.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import kotlinx.coroutines.launch

class ApplicationFormViewModel(private val applicationUploadRepository: ApplicationUploadRepository) : ViewModel() {

    private val mutableLiveData =  MutableLiveData<ApplicationUploadState>()
    val applicationUploadState: LiveData<ApplicationUploadState>  = mutableLiveData;


    @SuppressLint("SuspiciousIndentation")
    fun saveApplicationDetails(firstName: String, lastName: String,
                               parentFirstName: String, parentLastName: String,
                               fullSchoolName: String, twelfthMarks: String,
                               tenthMarks: String, selectEntranceType: String,
                               jointEntranceRank: String, streetAddress1: String,
                               streetAddress2: String, city: String,
                               state: String, pinCodeStr: String,
                               phoneNumber: String, courseWillingToStudy: String){
        viewModelScope.launch {
            try{
              val responseWrapper = applicationUploadRepository.saveApplicationDetails(firstName, lastName, parentFirstName,
                  parentLastName,
                  fullSchoolName,  twelfthMarks,
                  tenthMarks, selectEntranceType,
                  jointEntranceRank, streetAddress1,
                  streetAddress2, city, state, pinCodeStr,
                  phoneNumber, courseWillingToStudy)

                if (responseWrapper.response.success) {
                    mutableLiveData.value = ApplicationUploadState.Success(responseWrapper.response.message);
                } else {
                    //if in case add any other data
                    if (responseWrapper.response.message !== null)
                        mutableLiveData.value = ApplicationUploadState.Error(responseWrapper.response.message)

                    Log.i("Snath", responseWrapper.toString())
                }
            }
            catch (e : Exception){
                mutableLiveData.value = ApplicationUploadState.Error("Application Upload Failed ${e.message}");
            }
        }
    }

}

sealed class ApplicationUploadState(){
    data class Success(val message: String?) : ApplicationUploadState()
    data class Error(val error: String?) : ApplicationUploadState()
}