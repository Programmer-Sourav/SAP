package com.service.appdev.coursedetails.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.models.CoursesAvailableData
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ApplicationFormViewModel(private val applicationUploadRepository: ApplicationUploadRepository) : ViewModel() {

    private val mutableLiveData =  MutableLiveData<ApplicationUploadState>()
    val applicationUploadState: LiveData<ApplicationUploadState>  = mutableLiveData;


    private val coursesAvailableLiveData = MutableLiveData<CoursesAvailableState>();
    val courseAvailableState : LiveData<CoursesAvailableState> = coursesAvailableLiveData;


    @SuppressLint("SuspiciousIndentation")
    fun saveApplicationDetails(firstName: String, lastName: String,
                               parentFirstName: String, parentLastName: String,
                               fullSchoolName: String, twelfthMarks: String,
                               tenthMarks: String, selectEntranceType: String,
                               jointEntranceRank: String, streetAddress1: String,
                               streetAddress2: String, city: String,
                               state: String, pinCodeStr: String,
                               phoneNumber: String, courseWillingToStudy: String,
                               studentEmail : String, username: String, selectedCollege: String){
        viewModelScope.launch {
            try{
              val responseWrapper = applicationUploadRepository.saveApplicationDetails(firstName, lastName, parentFirstName,
                  parentLastName,
                  fullSchoolName,  twelfthMarks,
                  tenthMarks, selectEntranceType,
                  jointEntranceRank, streetAddress1,
                  streetAddress2, city, state, pinCodeStr,
                  phoneNumber, courseWillingToStudy, studentEmail, username, selectedCollege)

                if (responseWrapper.response.success) {
                    mutableLiveData.value = ApplicationUploadState.Success(responseWrapper.response.message, responseWrapper.response.studentId);
                    Log.i("Snath ", "UniqueId "+responseWrapper.response.data.studentId)
                    Log.i("Snath ", "UniqueId "+responseWrapper.response.data.message)
                    Log.i("Snath ", "UniqueId "+responseWrapper.response.data.message)
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


    fun getCourses(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = applicationUploadRepository.getCourses();
                Log.i("Snath123 ", response.response.data.toString())
                withContext(Dispatchers.Main){
                    coursesAvailableLiveData.value = CoursesAvailableState.Success(response.response.data)
                }
            }
            catch (ex : Exception){
                withContext(Dispatchers.Main){
                    coursesAvailableLiveData.value = CoursesAvailableState.Error(ex.message.toString())
                }
            }
        }
    }

}


sealed class ApplicationUploadState(){
    data class Success(val data: String, val studentId: String) : ApplicationUploadState()
    data class Error(val error: String?) : ApplicationUploadState()
}

sealed class CoursesAvailableState{
    data class Success(val courses: ArrayList<CoursesAvailableData>) : CoursesAvailableState()
    data class Error (val message: String) : CoursesAvailableState()
}