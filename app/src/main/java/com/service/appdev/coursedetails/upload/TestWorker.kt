package com.service.appdev.coursedetails.upload

import android.content.Context
import androidx.work.Worker
import androidx.work.WorkerParameters

//Worker takes two parameters (1) context (2) WorkerParameters
class TestWorker( appContext: Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters) {

    //doWork Runs in the background thread provided by WorkManager
    //Returns Result
    override fun doWork(): Result {
         //dummy call to api
        uploadImages();
        //Result type can be success(), failure() or retry()
        return Result.success();
    }

    private fun uploadImages() {
        TODO("Not yet implemented")
    }
}

