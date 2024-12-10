package com.service.appdev.coursedetails.models

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("post_application_form")
    suspend fun saveApplicationDetails( @Body dataBody : Map<String, String>) : ApplicationFormResponseWrapper

    @GET("available_colleges")
    suspend fun getAvailableCollegesList() : CollegeResponse
}


object ApiServiceBuilder {
    private const val BASE_URL = "https://www.travelsawari.com/index_course.php/" // Replace with your API URL


    val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .client(okHttpClient)// Use GSON or another converter
        .build()

    val apiService: ApiService = retrofit.create(ApiService::class.java)
}