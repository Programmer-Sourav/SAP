package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.service.appdev.coursedetails.admin.upload.UploadScreen


class AdminPanel: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.service.appdev.coursedetails.R.layout.admin_panel)

        val uploadAnnouncement = findViewById<Button>(com.service.appdev.coursedetails.R.id.uploadNews);
        val uploadImages = findViewById<Button>(com.service.appdev.coursedetails.R.id.uploadImages);
        val logout = findViewById<Button>(com.service.appdev.coursedetails.R.id.logout);
        val shareAppLink = findViewById<Button>(com.service.appdev.coursedetails.R.id.shareAppLink);

        uploadAnnouncement.setOnClickListener(View.OnClickListener {
            val intent = Intent(this, MakeAnnouncement::class.java);
            startActivity(intent);
        })

        uploadImages.setOnClickListener({
            val uploadScreen = Intent(this, UploadScreen::class.java);
            uploadScreen.putExtra("uploaderType", "imageslider" );
            startActivity(uploadScreen);
        })

        shareAppLink.setOnClickListener(View.OnClickListener {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val applink = "https://play.google.com/store/apps/details?id=com.service.appdev.coursedetails&hl=en"
            val shareBody = "Make your college admission easier. Download our app now- $applink"
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain")
            /*Applying information Subject and Body.*/
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(com.service.appdev.coursedetails.R.string.share_subject))
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            /*Fire!*/
            startActivity(Intent.createChooser(intent, getString(com.service.appdev.coursedetails.R.string.share_using)))
        })

        logout.setOnClickListener(View.OnClickListener {
            resetSharedPreference();
            val intent = Intent(this@AdminPanel, FrontPage::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent)
        })
    }

    private fun resetSharedPreference () {
        val sharedPreferences : SharedPreferences = getSharedPreferences(getString(com.service.appdev.coursedetails.R.string.save_login_details), MODE_PRIVATE);
        val editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}