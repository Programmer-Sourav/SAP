package com.service.appdev.coursedetails.models

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.service.appdev.coursedetails.R
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("post_application_form")
    suspend fun saveApplicationDetails( @Body dataBody : Map<String, String>) : ApplicationFormResponseWrapper

    @GET("available_colleges")
    suspend fun getAvailableCollegesList() : CollegeResponse

    @GET("retrieve_slider_images")
    suspend fun getImagesList() : ImagesResponse

    @GET("available_courses/{collegeName}")
    suspend fun getAvailableCourseList(@Path("collegeName") collegeName: String): CourseResponse

    @GET("available_brochures/{collegeName}")
    suspend fun getAvailableBrochureList(@Path("collegeName") collegeName: String): BrochureResponse


    @GET("available_colleges_by_course/{courseName}")
    suspend fun getAvailableCollegesList(@Path("courseName") collegeName: String): CollegeResponse

    @GET("get_annoucement")
    suspend fun getAnnouncementsList() : AnnouncementResponse
    @POST("post_annoucement")
    suspend fun postAnnouncement( @Body dataBody: Map<String, String>): PostAnnouncementResponse

    @POST("login")
    suspend fun attemptLogin(@Body dataBody: Map<String, String>): LoginResponse
    @POST("admin_login")
    suspend fun attemptAdminLogin(@Body dataBody: Map<String, String>): LoginResponse
    @POST("institute_login")
    suspend fun attemptInstituteLogin(@Body dataBody: Map<String, String>): LoginResponse

    @POST("register_user")
    suspend fun attemptRegistration(@Body dataBody: Map<String, String>): LoginResponse

    @POST("save_course")
    suspend fun addCourseName(@Body dataBody: Map<String, String>): AddedCourseResponse

    @GET("get_courses")
    suspend fun getCourses(): CoursesAvailableList

}


object ApiServiceBuilder {
    private const val BASE_URL = "https://www.travelsawari.com/index_course.php/" // Replace with your API URL

    fun createApiService(context: Context): ApiService {
        // Fetch the auth token
        val savedLp = getAuthToken(context)
        val splitedArray = savedLp?.split(",");
        var authToken  = "";
        if (splitedArray != null) {
            if(splitedArray.size>1)
                authToken = splitedArray[2];
        }


        Log.i("Snath", "AuthToken "+authToken);
        // Interceptor to add headers conditionally based on endpoints
        val headerInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()

            // Add Authorization header only for specific endpoints
            when {
                originalRequest.url.encodedPath.contains("post_application_form") ||
                        originalRequest.url.encodedPath.contains("available_colleges") ||
                        originalRequest.url.encodedPath.contains("available_courses")  ||
                        originalRequest.url.encodedPath.contains("available_brochures")
                        -> {
                    authToken.let {
                        requestBuilder.addHeader("Authorization", "Bearer $it")
                    }
                }
            }

            chain.proceed(requestBuilder.build())
        }

        // Logging Interceptor for debugging
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        // OkHttpClient with interceptors
        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(headerInterceptor)
            .build()

        // Retrofit instance
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        return retrofit.create(ApiService::class.java)
    }

    // Helper function to get the auth token from SharedPreferences
    private fun getAuthToken(context: Context): String? {
        val sharedPref: SharedPreferences = context.getSharedPreferences(
            context.getString(R.string.save_login_details),
            Context.MODE_PRIVATE
        )
        return sharedPref.getString(context.getString(R.string.save_login_details), "defaultValue")
    }
}
