package com.service.appdev.coursedetails.upload

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    override fun doWork(): Result {
        // Get input data (e.g., file path)
        val filePath = inputData.getString("filePath") ?: return Result.failure();
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

    private  fun uploadFile(filePath: String) : okhttp3.Response{
        val file = File(filePath);
        val client = OkHttpClient();
        System.out.println("FILE "+file);
        //create request body
        val requestBody = MultipartBody.Builder().setType(MultipartBody.FORM)
            .addFormDataPart("file", file.name,
                file.asRequestBody("application/octet-stream".toMediaTypeOrNull())).build();

        //create request
        val request = Request.Builder()
            .url("https://www.travelsawari.com/index_course.php/upload_documents")
            .post(requestBody)
            .build();


        // Execute request
        return client.newCall(request).execute();
    }

}