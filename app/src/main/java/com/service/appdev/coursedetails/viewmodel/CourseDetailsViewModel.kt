package com.service.appdev.coursedetails.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.models.AnnouncementData
import com.service.appdev.coursedetails.models.CollegeDetails
import com.service.appdev.coursedetails.models.CourseDetails
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailsViewModel(private val collegeManagementRepository: CollegeManagementRepository) : ViewModel() {


    private val mutableLiveData = MutableLiveData<CourseDetailsState>()
    val courseDetailsState: LiveData<CourseDetailsState> = mutableLiveData;

    private val mutableCollegeLiveData = MutableLiveData<CourseDataState>()
    val courseDataState : LiveData<CourseDataState> = mutableCollegeLiveData;

    private val announcementLiveData = MutableLiveData<AnnouncementDataState>();
    val announcementDataState : LiveData<AnnouncementDataState> = announcementLiveData

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

    fun getCourseListByCollege(selectedCollege : String){
        viewModelScope.launch(Dispatchers.IO) {
            try{
                val response = collegeManagementRepository.retrieveCourseDetails(selectedCollege);
                withContext(Dispatchers.Main) {
                    mutableCollegeLiveData.value = CourseDataState.Success(response.response.courses);
                    Log.i("Snath", "Course Details "+ response.response.courses)
                }
            }
            catch (e : Exception){
                withContext(Dispatchers.Main) {
                    mutableCollegeLiveData.value =
                        CourseDataState.Error("Fetching Course Details Failed ${e.message}");
                }
            }
        }
    }

    fun getAnnouncementList(){
        viewModelScope.launch (Dispatchers.IO){
            try{
                val response = collegeManagementRepository.retrieveAnnouncementDetails();
                withContext(Dispatchers.Main){
                    announcementLiveData.value = AnnouncementDataState.Success(response.response.data);
                    Log.i("Snath ", "Announcement Live "+response.response.data);
                }
            }
            catch(e : Exception){
                withContext(Dispatchers.Main){
                    announcementLiveData.value = AnnouncementDataState.Error(e.message)
                }
            }
        }
    }

}

sealed class CourseDetailsState {
    data class Success(val collegeList: ArrayList<CollegeDetails>) : CourseDetailsState()
    data class Error(val error: String?) : CourseDetailsState()
}

sealed class CourseDataState{
    data class Success (val courseDetails: ArrayList<CourseDetails>) : CourseDataState()
    data class Error(val error: String?) : CourseDataState()
}

sealed class AnnouncementDataState{
    data class Success(val announcementDetails: ArrayList<AnnouncementData>) : AnnouncementDataState()
    data class Error (val error: String?) : AnnouncementDataState()
}