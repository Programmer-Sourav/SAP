package com.service.appdev.coursedetails.admin.upload

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.fragments_and_activities.CustomDialogFragment
import java.io.File

class BrochureUploadScreen : AppCompatActivity() {

    private lateinit var progressBar: ProgressBar
    private lateinit var collegeNameEdt : EditText
    private lateinit var uploadWorkRequest: OneTimeWorkRequest
    private lateinit var inputData: Data;
    private lateinit var inputData1: Data;
    private lateinit var constraints: Constraints;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.upload_brochure_screen)

        collegeNameEdt = findViewById<EditText>(R.id.collegeNameEdt);
        progressBar = findViewById(R.id.progressBar);

        val btnSubmit = findViewById<Button>(R.id.btnSubmit);

        btnSubmit.setOnClickListener(View.OnClickListener {
            uploadFileOnButtonClick();
            showDialog();
        })

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
            val collegeName = collegeNameEdt.text.toString();
            // Input data for the worker
            inputData = Data.Builder().putStringArray("filePath",filePaths.toTypedArray()).putString("collegeName",collegeName).build();
            constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

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

    private fun uploadFileOnButtonClick(){
        //inputData = Data.Builder().putString("collegeName",collegeName).build();
        // Create a OneTimeWorkRequest
        uploadWorkRequest = OneTimeWorkRequestBuilder<BrochureUploadWorker>()
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
    }
    fun showDialog() {
        val progressDialog = CustomDialogFragment()
        progressDialog.show(supportFragmentManager, "ProgressDialog")
    }
}