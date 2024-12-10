package com.service.appdev.coursedetails.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.CustomSpinnerAdapter
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.models.CollegeDetails
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.CourseDetailsState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory

class CourseDetails : Fragment() {
    private lateinit var view: View;

    private val apiService = ApiServiceBuilder.apiService

    private val viewModel: CourseDetailsViewModel by viewModels{
        CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
    }
    private lateinit var myAvailableColleges : Spinner;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_course_details, container, false)

        myAvailableColleges = view.findViewById<Spinner>(R.id.my_available_colleges);
        val collegeNotSelected = view.findViewById<TextView>(R.id.collegeNotSelected);

        viewModel.getCollegesList();

        viewModel.courseDetailsState.observe(viewLifecycleOwner, Observer { state->
            when(state){
                is CourseDetailsState.Success->{
                   val collegeList = state.collegeList.map{it.collegeName}
                    setupSpinner(collegeList) // Pass the list to the spinner setup method
                }

                is CourseDetailsState.Error->{
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }

        })
        val coursesGridView = view.findViewById<GridView>(R.id.gridView);
        return view;
    }

    private fun setupSpinner(collegeList: List<String>) {
        val updatedCollegeList = mutableListOf("No course selected")
        updatedCollegeList.addAll(collegeList)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            updatedCollegeList
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        myAvailableColleges.adapter = adapter
        myAvailableColleges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedCollege = collegeList[position]
                if (selectedCollege != null) {
                    Log.d("SelectedCourse", selectedCollege)
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }
    }

}