package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.CourseDetails
import com.squareup.picasso.Picasso

class SingleCourseDetails : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.single_page_activity)
        val courseList = getIntent().getParcelableArrayListExtra<CourseDetails>("courseList")
        val position = intent.getStringExtra("position")
        Log.i("Snath", "courseList "+ courseList)

        val courseId = findViewById<TextView>(R.id.courseId)
        val courseName = findViewById<TextView>(R.id.courseName)
        val seatCapacity = findViewById<TextView>(R.id.seatCapacity)
        val courseDuration = findViewById<TextView>(R.id.courseDuration)
        val courseDescription = findViewById<TextView>(R.id.courseDescription)
        val courseImage = findViewById<ImageView>(R.id.courseImg);



        Picasso.get().load(courseList?.get(Integer.parseInt(position!!))?.img_url).into(courseImage)
        courseId.text = getString(R.string.course_code, courseList?.get(Integer.parseInt(position!!))?.courseId)
        courseName.text = getString(R.string.course_name, courseList?.get(Integer.parseInt(position!!))?.courseName)
        seatCapacity.text = getString(R.string.seat_capacity, courseList?.get(Integer.parseInt(position!!))?.seatCapacity)
        courseDuration.text = getString(R.string.course_duration, courseList?.get(Integer.parseInt(position!!))?.courseDuration)
        courseDescription.text = getString(R.string.course_description, courseList?.get(Integer.parseInt(position!!))?.courseDescription)


    }
}