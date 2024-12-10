package com.service.appdev.coursedetails.upload

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.service.appdev.coursedetails.R

class TestWorkerRunner : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        //setContentView(R.layout.upload_screen)


        //creating a simple work  request
        val oneTimeWorkRequest : WorkRequest = OneTimeWorkRequestBuilder<TestWorker>().build();

        //submitting the work with workRequest to the WorkManager Service to execute
        WorkManager.getInstance(applicationContext).enqueue(oneTimeWorkRequest);
    }
}