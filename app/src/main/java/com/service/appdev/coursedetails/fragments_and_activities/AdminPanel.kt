package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.admin.upload.UploadScreen


class AdminPanel: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.admin_panel)

        val uploadAnnouncement = findViewById<Button>(R.id.uploadNews);
        val uploadImages = findViewById<Button>(R.id.uploadImages);

        uploadAnnouncement.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MakeAnnouncement::class.java);
            startActivity(intent);
        })

        uploadImages.setOnClickListener({
            val uploadScreen = Intent(this, UploadScreen::class.java);
            startActivity(uploadScreen);
        })
    }
}