package com.service.appdev.coursedetails.upload

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
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
import com.service.appdev.coursedetails.HomeController
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

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val filePaths = mutableListOf<String>()

            // Handle multiple file selection
            intent?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val filePath = getFilePathFromUri(uri)
                    filePaths.add(filePath)
                }
            }

            // Handle single file selection
            intent?.data?.let { uri ->
                val filePath = getFilePathFromUri(uri)
                filePaths.add(filePath)
            }

            // Input data for the worker
            filePaths.forEach { filePath ->
                val inputData = Data.Builder().putString("filePath", filePath).build()
                // Do something with inputData, e.g., enqueue a Worker
                Log.d("FilePicker", "File Path: $filePath")
            }

            // Input data for the worker
            val inputData = Data.Builder().putStringArray("filePath",filePaths.toTypedArray()).build();
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
                                Toast.makeText(this, "Upload successful! Redirecting to Home...", Toast.LENGTH_LONG).show()
                                val returnIntent : Intent = Intent(applicationContext, HomeController::class.java);
                                startActivity(returnIntent);
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
        }
    }

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
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*" // MIME type for all files
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple file selection
        }
        filePickerLauncher.launch(intent)
    }

}