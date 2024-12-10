package com.service.appdev.coursedetails.upload

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.service.appdev.coursedetails.R
import java.io.File

class UploadScreen : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_screen)

        progressBar = findViewById(R.id.progressBar);
        val btnSelectFile = findViewById<Button>(R.id.btnSelectFile)
        btnSelectFile.setOnClickListener {
            openFilePicker()
        }
    }

private val filePickerLauncher  = registerForActivityResult(ActivityResultContracts.GetContent()){
    uri : Uri? ->
    if(uri !=null){
        val filePath = getFilePathFromUri(uri);

        // Input data for the worker
        val inputData = Data.Builder().putString("filePath",filePath).build();

        val constraints = androidx.work.Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create a OneTimeWorkRequest
        val uploadWorkRequest = OneTimeWorkRequestBuilder<UploadWorker>()
            .setConstraints(constraints)
            .setInputData(inputData).build();

        // Enqueue the work
        WorkManager.getInstance(this).enqueue(uploadWorkRequest);

        WorkManager.getInstance(this).getWorkInfoByIdLiveData(uploadWorkRequest.id)
            .observe(this){
                    workInfo ->
                if (workInfo!= null){
                    when(workInfo.state){
                        WorkInfo.State.SUCCEEDED ->{
                            // Upload succeeded
                            progressBar.visibility = View.INVISIBLE;
                            Toast.makeText(this, "Upload successful!", Toast.LENGTH_SHORT).show()
                        }
                        WorkInfo.State.FAILED ->{
                            // Upload failed
                            progressBar.visibility = View.INVISIBLE;
                            Toast.makeText(this, "Upload failed!", Toast.LENGTH_SHORT).show()
                        }
                        WorkInfo.State.RUNNING ->{
                            // Upload in progress
                            val progress = workInfo.progress.getInt("progress", 0)
                            progressBar.visibility = View.VISIBLE;
                            progressBar.progress = progress
                        }
                        else->{
                            // Other states: ENQUEUED, CANCELLED, BLOCKED
                        }
                    }
                }
            }
    }}

    private fun getFilePathFromUri(uri: Uri): String {
       if(Build.VERSION.SDK_INT<29){
           var filePath: String = "";
           val cursor = contentResolver.query(uri, null, null, null, null)
           cursor?.use {
               val columnIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
               if (columnIndex != -1) {
                   it.moveToFirst()
                   filePath = it.getString(columnIndex)
               }
           }
           return filePath;
       }
        else{
           try {
               val inputStream = contentResolver.openInputStream(uri) ?: return "no filepath found"
               val file = File(cacheDir, getFileName(uri))
               val outputStream = file.outputStream()
               inputStream.use { input ->
                   outputStream.use { output ->
                       input.copyTo(output)
                   }
               }
               return file.absolutePath
           } catch (e: Exception) {
               e.printStackTrace()
               return "no filepath found"
           }
       }
    }

    private fun getFileName(uri: Uri): String {
        var name = "temp_file"
        val cursor = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex) ?: "temp_file"
                }
            }
        }
        return name
    }

    private fun openFilePicker() {
        filePickerLauncher.launch("*/*") // Specify MIME type for all files
    }

}