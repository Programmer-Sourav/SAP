package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import com.service.appdev.coursedetails.R


class ContactUs : Fragment() {


    private lateinit var view: View;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // Inflate the layout for this fragment and return it
        view = inflater.inflate(R.layout.fragment_contact_us, container, false)
        val callBtn = view.findViewById<Button>(R.id.callNow);

        if (callBtn != null) {
            callBtn.setOnClickListener(View.OnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.setData(Uri.parse("tel:09774837504"))
                startActivity(intent)
            })
        }

        return view;
    }
}
