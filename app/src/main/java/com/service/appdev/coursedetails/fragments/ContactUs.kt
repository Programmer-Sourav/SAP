package com.service.appdev.coursedetails.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.service.appdev.coursedetails.R

class ContactUs : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment and return it
        return inflater.inflate(R.layout.fragment_contact_us, container, false)
    }
}
