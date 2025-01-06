package com.service.appdev.coursedetails.models

data class ImagesResponse(val response : ImageResponseData) {}
data class ImageResponseData(val message: String, val data: ArrayList<ImageData>){}
data class ImageData(val filePath: String){}