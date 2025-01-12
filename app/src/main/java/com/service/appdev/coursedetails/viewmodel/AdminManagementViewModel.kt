package com.service.appdev.coursedetails.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.service.appdev.coursedetails.models.ImageData
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import kotlinx.coroutines.launch

class AdminManagementViewModel(private val collegeManagementRepository: CollegeManagementRepository) : ViewModel() {

    private val announcementMutableLiveData = MutableLiveData<AdminState>();
    val announcementState : LiveData<AdminState> = announcementMutableLiveData;

    private val imagesListMutableLiveData = MutableLiveData<ImageState>();
    val imageListState : LiveData<ImageState> = imagesListMutableLiveData;


    fun saveAnnouncementDetails(header: String, notice: String, announcementId: String){
         viewModelScope.launch {
             try{
               val response = collegeManagementRepository.postAnnouncementDetails(header, notice, announcementId);
                announcementMutableLiveData.value = AdminState.Success(response.message);
             }
             catch (e: Exception){
               announcementMutableLiveData.value = AdminState.Error(e.message);
             }
         }
    }

    fun getImagesSlider() {
        viewModelScope.launch {
            try{
              val response = collegeManagementRepository.getImagesForSlider();
              imagesListMutableLiveData.value = ImageState.Success(response.response.data);
              }
            catch (e: Exception){
              imagesListMutableLiveData.value = ImageState.Error(e.message);
            }

        }
    }
}

sealed class AdminState(){
    data class Success(val message: String? ) : AdminState();
    data class Error(val error: String?) : AdminState();
}

sealed class ImageState(){
    data class Success(val images: ArrayList<ImageData>) : ImageState()
    data class Error(val error: String?) : ImageState()
}