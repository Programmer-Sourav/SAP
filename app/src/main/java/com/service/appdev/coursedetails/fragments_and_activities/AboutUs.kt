package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.service.appdev.coursedetails.R

class AboutUs : Fragment() {
    private lateinit var view: View;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_aboutus, container, false)
        //super.onCreateView(inflater, container, savedInstanceState)
        return view;
    }
}