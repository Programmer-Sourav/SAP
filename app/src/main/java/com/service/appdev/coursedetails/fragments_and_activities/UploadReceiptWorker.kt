package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.work.Data
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.service.appdev.coursedetails.R
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File

class UploadReceiptWorker (context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    lateinit var  collegeName : String;
    lateinit var upiId : String;
    lateinit var dateTime : String;
    lateinit var status: String;
    lateinit var amountPaid: String;
    lateinit var studentUniqueId: String;

    private fun getSavedLoginDetails() : String? {
        val sharedPref: SharedPreferences = applicationContext.getSharedPreferences(applicationContext.getString(
            R.string.save_login_details),
            MODE_PRIVATE
        )
        val loginDetails = sharedPref.getString(applicationContext.getString(R.string.save_login_details), "defaultValue")
        return loginDetails;
    }
    override fun doWork(): Result {
        // Get input data (e.g., file path)
        val filePath = inputData.getStringArray("filePath") ?: return Result.failure();
        amountPaid = inputData.getString("amountPaid") ?: return Result.failure();
        upiId = inputData.getString("upiId") ?: return Result.failure();
        dateTime = inputData.getString("dateTime") ?: return Result.failure();
        status = inputData.getString("status") ?: return Result.failure();
        studentUniqueId = inputData.getString("userUniqueId") ?: return Result.failure();
        System.out.println("File Path "+ filePath);
        Log.i("Snath ", " User Receipt Data111 "+studentUniqueId + " " +amountPaid +" "+ upiId +" "+ dateTime+" "+status);
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

        val savedLogin = getSavedLoginDetails();
        val splitted = savedLogin?.split(",")
        var username  = "";
        if (splitted != null) {
            if(splitted.size>1)
                username = splitted[1];
        }

        val client = OkHttpClient()
        // Create a MultipartBody builder
        val requestBodyBuilder = MultipartBody.Builder().setType(MultipartBody.FORM)
        // Add username as a form-data part
        for (filePath in filePaths) {
            val file = File(filePath)
            // Check if the file exists before adding it to the request
            if (!file.exists()) {
                println("File does not exist: $filePath")
                continue
            }
            requestBodyBuilder.addFormDataPart("studentUniqueId", studentUniqueId)
            requestBodyBuilder.addFormDataPart("upiId", upiId)
            requestBodyBuilder.addFormDataPart("paymentDateTime", dateTime)
            requestBodyBuilder.addFormDataPart("status", status)
            requestBodyBuilder.addFormDataPart("paymentamount", amountPaid)

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
            .url("https://www.travelsawari.com/index_course.php/upload_screenshots")
            .post(requestBody)
            .build()
        // Execute the request
        return client.newCall(request).execute()
    }

}