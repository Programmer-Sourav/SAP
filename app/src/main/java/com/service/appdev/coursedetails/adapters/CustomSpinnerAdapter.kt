package com.service.appdev.coursedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.CollegeDetails
import com.service.appdev.coursedetails.models.CollegeResponse

class CustomSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val collegeList: List<CollegeDetails>
) : ArrayAdapter<CollegeDetails>(context, resource, collegeList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val collegeNameTextView: TextView = view.findViewById(R.id.spinnerItemText)
        collegeNameTextView.text = collegeList[position].collegeName;

        return view;

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val collegeNameTextView: TextView = view.findViewById(R.id.spinnerItemText)
        collegeNameTextView.text = collegeList[position].collegeName;

        return view
    }

    override fun getCount(): Int {
        return collegeList.size;
    }

    override fun getItem(position: Int): CollegeDetails {
        return collegeList[position]
    }
}