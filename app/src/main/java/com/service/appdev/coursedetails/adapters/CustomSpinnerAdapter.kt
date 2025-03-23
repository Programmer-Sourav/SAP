package com.service.appdev.coursedetails.adapters

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.CourseDetails
import com.squareup.picasso.Picasso

class CustomSpinnerAdapter(
    context: Context,
    private val resource: Int,
    private val courseList: List<CourseDetails>
) : ArrayAdapter<CourseDetails>(context, resource, courseList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val collegeNameTextView: TextView = view.findViewById(R.id.grid_item_text)
        val courseImageView : ImageView = view.findViewById(R.id.grid_item_img)

        collegeNameTextView.text = courseList[position].courseName;
        Picasso.get()
            .load(courseList[position].img_url)
            .error(android.R.drawable.ic_media_ff)       // Optional: Add an error image
            .into(courseImageView)
        return view;

    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(resource, parent, false)

        val collegeNameTextView: TextView = view.findViewById(R.id.spinnerItemText)
        collegeNameTextView.text = courseList[position].courseName;

        return view

        /**
         * return getView(position, convertView, parent); // Reuse getView for dropdown
         */
    }

    override fun getCount(): Int {
        return courseList.size;
    }

    override fun getItem(position: Int): CourseDetails {
        return courseList[position]
    }
}