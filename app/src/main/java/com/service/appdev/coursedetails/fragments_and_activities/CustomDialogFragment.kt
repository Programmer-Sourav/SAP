package com.service.appdev.coursedetails.fragments_and_activities

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle

import androidx.fragment.app.DialogFragment
import com.service.appdev.coursedetails.R

class CustomDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.progress_bar_layout)
        dialog.setCancelable(false) // Prevent dismissing
        dialog.window?.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.transparent)))
        return dialog
    }
}