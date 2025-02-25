package com.service.appdev.coursedetails.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.fragments_and_activities.CourseBrochures
import com.service.appdev.coursedetails.models.AnnouncementData
import com.service.appdev.coursedetails.models.CollegeDetails
import com.service.appdev.coursedetails.models.CourseBrochure
import com.service.appdev.coursedetails.models.CourseDetails
import com.service.appdev.coursedetails.models.CoursesAvailableList
import com.service.appdev.coursedetails.models.UserData
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CourseDetailsViewModel(private val collegeManagementRepository: CollegeManagementRepository) : ViewModel() {


    private val mutableLiveData = MutableLiveData<CourseDetailsState>()
    val courseDetailsState: LiveData<CourseDetailsState> = mutableLiveData;

    private val mutableBrochureLiveData = MutableLiveData<BrochureDataState>()
    val brochureDetailsState: LiveData<BrochureDataState> = mutableBrochureLiveData;

    private val mutableCollegeLiveData = MutableLiveData<CourseDataState>()
    val courseDataState : LiveData<CourseDataState> = mutableCollegeLiveData;

    private val announcementLiveData = MutableLiveData<AnnouncementDataState>();
    val announcementDataState : LiveData<AnnouncementDataState> = announcementLiveData

    private val courseSavedLiveData = MutableLiveData<CollegeSavedState>();
    val courseSavedDataState : LiveData<CollegeSavedState> = courseSavedLiveData;




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


    fun saveCourse(coursename: String){
        viewModelScope.launch ( Dispatchers.IO ){
            try {
                val response = collegeManagementRepository.addCourseDetails(coursename);
                withContext(Dispatchers.Main) {
                 courseSavedLiveData.value = CollegeSavedState.Success(response.response.message);
                    Log.i("Snath ", response.response.message + ", "+ response.toString())
                }
            }
            catch (ex: Exception){
                withContext(Dispatchers.Main){
                    courseSavedLiveData.value = CollegeSavedState.Error(ex.message!!);
                }
            }
        }
    }

    fun getCollegeListByCourse(selectedCourse : String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = collegeManagementRepository.retrieveCollegeDetails(selectedCourse);
                withContext(Dispatchers.Main) {
                    mutableLiveData.value = CourseDetailsState.Success(response.response.data);
                    Log.i("Snath ", "College Details" + response.response.data)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    mutableLiveData.value =
                        CourseDetailsState.Error("Fetching Course Details Failed ${e.message}");
                }
            }
        }
    }
        fun getBrochureListByCollege(selectedCollege : String){
            viewModelScope.launch(Dispatchers.IO) {
                try{
                    val response = collegeManagementRepository.retrieveBrochureDetails(selectedCollege);
                    withContext(Dispatchers.Main) {
                        mutableBrochureLiveData.value = BrochureDataState.Success(response.response.data);
                        Log.i("Snath", "Course Details "+ response.response.data)
                    }
                }
                catch (e : Exception){
                    withContext(Dispatchers.Main) {
                        mutableBrochureLiveData.value =
                            BrochureDataState.Error("Fetching Course Details Failed ${e.message}");
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

sealed class BrochureDataState{
    data class Success (val brochureDetails: ArrayList<CourseBrochure>) : BrochureDataState()
    data class Error(val error: String?) : BrochureDataState()
}

sealed class  CollegeSavedState(){
    data class Success(val message: String) : CollegeSavedState()
    data class Error (val message: String) : CollegeSavedState()
}


