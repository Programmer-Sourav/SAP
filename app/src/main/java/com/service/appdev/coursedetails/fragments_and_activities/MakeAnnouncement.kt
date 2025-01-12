package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.admin.upload.UploadScreen
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.AdminManagementViewModel
import com.service.appdev.coursedetails.viewmodel.AdminState
import com.service.appdev.coursedetails.viewmodelfactory.AdminManagementViewModelFactory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MakeAnnouncement : AppCompatActivity() {

    //private val apiService = ApiServiceBuilder.apiService;
//    val apiService = ApiServiceBuilder.createApiService(applicationContext)
//    private val viewModel : AdminManagementViewModel by viewModels {
//        AdminManagementViewModelFactory(CollegeManagementRepository(apiService))
//    }

    private lateinit var viewModel: AdminManagementViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_upload_news);

        val announcementHeader: EditText = findViewById(R.id.announcement_header);
        val announcementDescription: EditText = findViewById(R.id.announcement_to_make);
        val postAnnouncement : Button = findViewById(R.id.post_announcement);
        val uploadBtn : TextView = findViewById(R.id.uploadPdf);

        var announcementId = generateRandomId();

        val apiService = ApiServiceBuilder.createApiService(applicationContext)

        viewModel = ViewModelProvider(
            this,
            AdminManagementViewModelFactory(CollegeManagementRepository(apiService))
        )[AdminManagementViewModel::class.java]

        postAnnouncement.setOnClickListener({
            viewModel.saveAnnouncementDetails(announcementHeader.text.toString(), announcementDescription.text.toString(), announcementId)
        })

        viewModel.announcementState.observe(this, Observer { state ->
            when(state){
                is AdminState.Success -> {
                    Toast.makeText(this, "Announcement Successfully Posted", Toast.LENGTH_SHORT).show();
                    val intent = Intent(this, SuccessResponse::class.java);
                    startActivity(intent)
                }
                is AdminState.Error ->{
                    Toast.makeText(this, "Some error occurred while posting announcement", Toast.LENGTH_SHORT).show();
                }
            }
        })

        uploadBtn.setOnClickListener(View.OnClickListener {
            val uploadScreen = Intent(this, UploadScreen::class.java);
            uploadScreen.putExtra("uploaderType", "documentuploader" );
            uploadScreen.putExtra("announcementId", announcementId);
            startActivity(uploadScreen);
        })
    }
    fun generateRandomId(): String {
        // Get the current date and time
        val currentDateTime = SimpleDateFormat("yyyyMMddHHmmssSSS", Locale.getDefault()).format(Date())
        // Generate a random number between 100 and 999
        val randomNumber = (100..9999).random()
        // Combine date-time string and random number to form an ID
        return "ID_${currentDateTime}_$randomNumber"
    }
}