package com.service.appdev.coursedetails.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.databinding.FragmentApplicationFormFilupBinding
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import com.service.appdev.coursedetails.upload.UploadScreen
import com.service.appdev.coursedetails.viewmodel.ApplicationFormViewModel
import com.service.appdev.coursedetails.viewmodel.ApplicationUploadState
import com.service.appdev.coursedetails.viewmodelfactory.ApplicationFormViewModelFactory


class ApplicationUpload  : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var entranceIdSelected: String;

    private  var _binding: FragmentApplicationFormFilupBinding? = null;
    private val binding get() = _binding;

    private val apiService = ApiServiceBuilder.apiService

    private val viewModel : ApplicationFormViewModel by viewModels{
       ApplicationFormViewModelFactory(ApplicationUploadRepository(apiService))
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicationFormFilupBinding.inflate(inflater, container, false);

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this


        return binding?.root;
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
         val studentFirstName = binding?.studentFirstName
        val studentLastName = binding?.studentLastName;
        val parentFirstName = binding?.parentFirstName;
        val parentLastName = binding?.parentLastName;
        val fullSchoolName = binding?.fullSchoolName;
        val full12thMarks = binding?.fullTwelfthMarks;
        val full10thMarks = binding?.fullTenthMarks;
        val selectEntrance = binding?.mySpinner;
        val fullJointMarks = binding?.fullJointMarks;
        val streetAddress1 = binding?.streetAddress1;
        val streetAddress2 = binding?.streetAddress2;
        val city = binding?.city;
        val state = binding?.state;
        val pinCode = binding?.pincode;
        val phoneNumber = binding?.phoneNumberInput;
        val courseWillingInput = binding?.courseWillingInput;
        val submitForm = binding?.formSubmit;


        // Create an ArrayAdapter using the string array and a default spinner layout.
        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.dropdown_items,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears.
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner.
            selectEntrance?.adapter = adapter
            selectEntrance?.onItemSelectedListener = this
        }
        // Specify the layout to use when the list of choices appears.

        submitForm?.setOnClickListener(View.OnClickListener {
            if(binding?.studentFirstName?.text.toString().isNotEmpty() && binding?.studentLastName?.text.toString().isNotEmpty() && binding?.parentFirstName?.text.toString().isNotEmpty()
                && binding?.parentLastName?.text.toString().isNotEmpty() && binding?.fullSchoolName?.text.toString().isNotEmpty() &&  binding?.fullTwelfthMarks?.text.toString().isNotEmpty()
                && binding?.fullTenthMarks?.text.toString().isNotEmpty() && binding?.fullJointMarks?.text.toString().isNotEmpty()) {

                //validateInputs()
                viewModel.saveApplicationDetails(
                    binding?.studentFirstName?.text.toString(),
                    binding?.studentLastName?.text.toString(),
                    binding?.parentFirstName?.text.toString(),
                    binding?.parentLastName?.text.toString(),
                    binding?.fullSchoolName?.text.toString(),
                    binding?.fullTwelfthMarks?.text.toString(),
                    binding?.fullTenthMarks?.text.toString(),
                    entranceIdSelected,
                    binding?.fullJointMarks?.text.toString(),
                    binding?.streetAddress1?.text.toString(),
                    binding?.streetAddress2?.text.toString(),
                    binding?.city?.text.toString(),
                    binding?.state?.text.toString(),
                    binding?.pincode?.text.toString(),
                    binding?.phoneNumberInput?.text.toString(),
                    binding?.courseWillingInput?.text.toString()
                )
            }
        })

//        val uploadScreen = Intent(requireActivity(), UploadScreen::class.java);
//        startActivity(uploadScreen);

        viewModel.applicationUploadState.observe(viewLifecycleOwner, Observer{ state ->
            when (state) {

                is ApplicationUploadState.Success -> {
                    Toast.makeText(requireContext(), "Application successfully Uploaded!", Toast.LENGTH_SHORT).show()
                }

                is ApplicationUploadState.Error -> {
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }
        })


    }
    private fun validateInputs(studentFirstName: String, studentsLastName: String, parentFirstName: String, parentLastName: String,
                               fullSchoolName: String, full12thMarks: String, full10thMarks: String, selectEntrance: String, jointEntranceMarks: String,
                               streetAddress1: String, streetAddress2: String, city: String, state: String, pinCode: String,
                               phoneNumber: String, courseWillingToTake: String){
        Log.i("Snath", selectEntrance)



    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
       entranceIdSelected = parent?.selectedItem.toString();
        Log.d("Snath ", entranceIdSelected)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {
        return;
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }

}