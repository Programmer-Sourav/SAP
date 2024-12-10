package com.service.appdev.coursedetails.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.models.CollegeDetails
import com.service.appdev.coursedetails.models.CollegeResponse
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailsViewModel(private val collegeManagementRepository: CollegeManagementRepository) : ViewModel() {


    private val mutableLiveData = MutableLiveData<CourseDetailsState>()
    val courseDetailsState: LiveData<CourseDetailsState> = mutableLiveData;

    fun getCollegesList(){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = collegeManagementRepository.retrieveCollegeDetails();
                withContext(Dispatchers.Main) {
                    mutableLiveData.value = CourseDetailsState.Success(response.response.data);
                    Log.i("Snath ", "College Details"+ response.response.data)
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main) {
                    mutableLiveData.value =
                        CourseDetailsState.Error("Fetching Course Details Failed ${e.message}");
                }
            }
        }
    }

}

sealed class CourseDetailsState {
    data class Success(val collegeList: ArrayList<CollegeDetails>) : CourseDetailsState()
    data class Error(val error: String?) : CourseDetailsState()
}
