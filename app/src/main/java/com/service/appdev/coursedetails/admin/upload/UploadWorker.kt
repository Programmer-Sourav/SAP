package com.service.appdev.coursedetails.admin.upload

import android.content.Context
import android.util.Log
import androidx.work.Worker
import androidx.work.WorkerParameters
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
}