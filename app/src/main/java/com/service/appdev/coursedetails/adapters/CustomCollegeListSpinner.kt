package com.service.appdev.coursedetails.adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.CheckBox
import com.service.appdev.coursedetails.R

class CustomCollegeListSpinner(
    private val context: Context,
    private val layoutResource: Int,
    private val colleges: ArrayList<String>,
    private val selectedColleges: HashMap<String, Boolean>
) : BaseAdapter() {

    override fun getCount(): Int = colleges.size

    override fun getItem(position: Int): String = colleges[position]

    override fun getItemId(position: Int): Long = position.toLong()

    // This method is used to show the currently selected item in the spinner (collapsed state)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        val checkBox: CheckBox = view.findViewById(R.id.selectCollegeCb)
        // Display a summary of selected items instead of the current position
        val selectedCount = selectedColleges.count { it.value }

        if (selectedCount == 0) {
            checkBox.text = "No college selected"
        } else {
            checkBox.text = "$selectedCount college(s) selected"
        }
        // Don't show this as checked - it's just a display
        checkBox.isChecked = false
        checkBox.isClickable = false

        return view
    }

    // This method is used to populate the dropdown with checkboxes for user interaction
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(layoutResource, parent, false)
        val checkBox: CheckBox = view.findViewById(R.id.selectCollegeCb)
        val collegeName = getItem(position)
        // Set checkbox text to the current item
        checkBox.text = collegeName
        // Important fix: Remove the listener before setting checked state to avoid recursive calls
        checkBox.setOnCheckedChangeListener(null)
        // Set checkbox checked state based on selectedColleges map
        checkBox.isChecked = selectedColleges[collegeName] == true
        // Handle checkbox state changes (when the user clicks the checkbox)
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            // Update only this specific college's selection state
            selectedColleges[collegeName] = isChecked
            // Notify the adapter to refresh the spinner view
            notifyDataSetChanged()

            Log.d("Snath", "Selected College: $collegeName, isChecked: $isChecked")
            Log.d("Snath", "Selected Colleges: ${selectedColleges.filter { it.value }.keys}")
        }

        return view
    }

    // Method to get the selected colleges
    fun getSelectedItems(): List<String> {
        return selectedColleges.filter { it.value }.keys.toList()
    }
}
