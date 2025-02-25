package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.CollegeSavedState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory

class AddCourses : AppCompatActivity() {

    private lateinit var viewModel: CourseDetailsViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.add_courses)
        val courseNameEdt = findViewById<EditText>(R.id.coursename);
        val addCourse = findViewById<Button>(R.id.addCourse);

        val apiService = ApiServiceBuilder.createApiService(applicationContext)

        viewModel = ViewModelProvider(this, CourseDetailsViewModelFactory(
            CollegeManagementRepository(apiService)
        ))[CourseDetailsViewModel::class.java]

        addCourse.setOnClickListener(View.OnClickListener {
            viewModel.saveCourse(courseNameEdt.text.toString())
            showDialog();
        })

        viewModel.courseSavedDataState.observe(this, Observer { state->
            when(state){
                is CollegeSavedState.Error -> Toast.makeText(this, "Some Error Occurred!", Toast.LENGTH_SHORT).show();
                is CollegeSavedState.Success -> Toast.makeText(this, state.message, Toast.LENGTH_SHORT).show();
            }
        })
    }
    fun showDialog() {
        val progressDialog = CustomDialogFragment()
        progressDialog.show(supportFragmentManager, "ProgressDialog")
    }
}