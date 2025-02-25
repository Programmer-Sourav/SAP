package com.service.appdev.coursedetails.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.service.appdev.coursedetails.models.CoursesAvailableData

class CustomeCourseAdapter(
    context: Context,
    private val resource: Int,
    private val courseList: List<CoursesAvailableData>
) : ArrayAdapter<CoursesAvailableData>(context, resource, courseList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getView(position, convertView, parent)
        (view as TextView).text = getItem(position).coursename
        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        (view as TextView).text = getItem(position).coursename
        return view
    }

    override fun getCount(): Int {
        return courseList.size;
    }

    override fun getItem(position: Int): CoursesAvailableData {
        return courseList[position]
    }
}