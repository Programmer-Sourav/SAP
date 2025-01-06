package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.service.appdev.coursedetails.R
import com.squareup.picasso.Picasso

class SuccessResponse : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.success_response)
        val imageView = findViewById<ImageView>(R.id.gifImageView);

        Picasso.get().load(R.drawable.preview).into(imageView);
    }
}