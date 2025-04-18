package com.service.appdev.coursedetails.admin.upload

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.service.appdev.coursedetails.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // Get input data (e.g., file path)
        val filePath = inputData.getStringArray("filePath") ?: return Result.failure();
        System.out.println("File Path "+ filePath);
        //perform the upload
        return try{
            System.out.println("Beginning Of TRY UploadWorker");
            val response = uploadFile(filePath)
            if(response.isSuccessful){
                Result.success(); // Upload successful
            }
            else{
                Result.retry();  // Retry if failed
            }
        }
        catch (e : Exception){
            e.printStackTrace();
            Result.retry(); //Retry on exception
        }
    }


    private fun uploadFile(filePaths: Array<String>): okhttp3.Response {
        val level = getLevelOfSlider();
        val client = OkHttpClient()
        // Create a MultipartBody builder
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)

        for (filePath in filePaths) {
            val file = File(filePath)
            // Check if the file exists before adding it to the request
            if (!file.exists()) {
                println("File does not exist: $filePath")
                continue
            }
            requestBodyBuilder.addFormDataPart("level", level!!)
            //Log.d("Snath ", "Level "+level);
            // Add the file to the multipart body
            requestBodyBuilder.addFormDataPart(
                "file[]", file.name,
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())
            )
        }

        // Build the request body
        val requestBody = requestBodyBuilder.build()
        // Create the request
        val request = Request.Builder()
            .url("https://www.travelsawari.com/index_course.php/upload_images")
            .post(requestBody)
            .build()
        // Execute the request
        return client.newCall(request).execute()
    }

    private fun getLevelOfSlider(): String? {
       val sharedPreferences : SharedPreferences = applicationContext.getSharedPreferences(applicationContext.resources.getString(R.string.save_level), MODE_PRIVATE)
       val level = sharedPreferences.getString(applicationContext.getString(R.string.save_level), "");
       return level;
    }
}