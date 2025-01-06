package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.databinding.FragmentApplicationFormFilupBinding
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import com.service.appdev.coursedetails.upload.UploadScreen
import com.service.appdev.coursedetails.viewmodel.ApplicationFormViewModel
import com.service.appdev.coursedetails.viewmodel.ApplicationUploadState
import com.service.appdev.coursedetails.viewmodelfactory.ApplicationFormViewModelFactory


class ApplicationUpload : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit var entranceIdSelected: String;

    private var _binding: FragmentApplicationFormFilupBinding? = null;
    private val binding get() = _binding;

    //private val apiService = ApiServiceBuilder.apiService
    private lateinit var apiService : ApiService;
    private lateinit var viewModel: ApplicationFormViewModel;
    private lateinit var courseAddedLL : LinearLayout;
    val addedItems = ArrayList<String>()
    val copiedItems = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicationFormFilupBinding.inflate(inflater, container, false);

        val apiService = ApiServiceBuilder.createApiService(requireActivity())
        viewModel = ViewModelProvider(
            this,
            ApplicationFormViewModelFactory(ApplicationUploadRepository(apiService))
        )[ApplicationFormViewModel::class.java]

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
        val studentEmail = binding?.studentEmailEt;
        val city = binding?.city;
        val state = binding?.state;
        val pinCode = binding?.pincode;
        val phoneNumber = binding?.phoneNumberInput;
        val courseWillingInput = binding?.courseWillingInput;
        courseAddedLL = binding?.linearLayout!!;
        val submitForm = binding?.formSubmit;

        val savedLogin = getSavedLoginDetails();
        val splitted = savedLogin?.split(",")
        var username  = "";
        if (splitted != null) {
            if(splitted.size>1)
               username = splitted[1];
        }


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



        ArrayAdapter.createFromResource(
            requireActivity(),
            R.array.courses_items,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            courseWillingInput?.adapter = adapter
        }

// Set the `onItemSelectedListener` outside the adapter initialization block
        courseWillingInput?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()

                // Avoid adding the default hint item or duplicates
                if (!addedItems.contains(selectedItem) && selectedItem != "Select") {
                    addedItems.clear();
                    addedItems.add(selectedItem)
                    copiedItems.add(selectedItem);
                    setTextViews()
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }
        }


//        val dynamicTextView : TextView = TextView(requireContext());
//        dynamicTextView.text = binding?.courseWillingInput?.selectedItem.toString()
//        courseAddedLL.addView(dynamicTextView);
//        if(binding?.courseWillingInput?.selectedItem.toString()!==""){
//            addedItems.add(courseWillingInput?.selectedItem.toString())
//        }


        submitForm?.setOnClickListener(View.OnClickListener {
            if (binding?.studentFirstName?.text.toString()
                    .isNotEmpty() && binding?.studentLastName?.text.toString()
                    .isNotEmpty() && binding?.parentFirstName?.text.toString().isNotEmpty()
                && binding?.parentLastName?.text.toString()
                    .isNotEmpty() && binding?.fullSchoolName?.text.toString()
                    .isNotEmpty() && binding?.fullTwelfthMarks?.text.toString().isNotEmpty()
                && binding?.fullTenthMarks?.text.toString()
                    .isNotEmpty() && binding?.fullJointMarks?.text.toString().isNotEmpty() &&
                binding?.studentEmailEt?.text.toString()
                    .isNotEmpty()
            ) {

                var listConvertedToString  = "";

                for(item in copiedItems){
                   listConvertedToString = "$listConvertedToString, $item";
                }
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
                    listConvertedToString,
                    binding?.studentEmailEt?.text.toString(),
                    username
                )
            }
        })



        viewModel.applicationUploadState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {

                is ApplicationUploadState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        "Application successfully Uploaded!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val uploadScreen = Intent(requireActivity(), UploadScreen::class.java);
                    startActivity(uploadScreen);
                }

                is ApplicationUploadState.Error -> {
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    private fun validateInputs(
        studentFirstName: String,
        studentsLastName: String,
        parentFirstName: String,
        parentLastName: String,
        fullSchoolName: String,
        full12thMarks: String,
        full10thMarks: String,
        selectEntrance: String,
        jointEntranceMarks: String,
        streetAddress1: String,
        streetAddress2: String,
        city: String,
        state: String,
        pinCode: String,
        phoneNumber: String,
        courseWillingToTake: String
    ) {
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

    private fun setTextViews(): ArrayList<String> {
        val dynamicIds = ArrayList<Int>() // To store generated IDs for debugging or reference

        for (item in addedItems) {
            val dynamicTextView = TextView(requireContext())

            // Generate a unique ID for the TextView
            val dynamicId = View.generateViewId()
            dynamicTextView.id = dynamicId
            dynamicIds.add(dynamicId)

            // Set width, height, and layout parameters
            val layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, // Full width
                LinearLayout.LayoutParams.WRAP_CONTENT  // Height
            )
            layoutParams.setMargins(16, 16, 16, 16) // Add margins (left, top, right, bottom)
            dynamicTextView.layoutParams = layoutParams

            // Set background color
            dynamicTextView.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.purple_200))
            dynamicTextView.gravity = Gravity.CENTER_VERTICAL // Center text vertically

            // Set padding
            dynamicTextView.setPadding(32, 16, 32, 16) // (left, top, right, bottom)

            // Set text appearance dynamically (size, color, etc.)
            dynamicTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Text size in SP
            dynamicTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            dynamicTextView.text = item

            // Add a delete icon to the right
            val deleteIcon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete_bin) // Replace with your delete icon
            deleteIcon?.setTint(ContextCompat.getColor(requireContext(), R.color.white)) // Optional: Tint the icon
            dynamicTextView.setCompoundDrawablesWithIntrinsicBounds(
                null,        // Left icon
                null,        // Top icon
                deleteIcon,  // Right icon
                null         // Bottom icon
            )
            dynamicTextView.compoundDrawablePadding = 16 // Space between text and icon

            // Set click listener for the delete action
            dynamicTextView.setOnClickListener {
                // Remove this TextView from the parent layout
                courseAddedLL.removeView(dynamicTextView)
                // Optionally, remove the item from addedItems if needed
                addedItems.remove(item)
            }

            // Add the TextView to the parent layout
            courseAddedLL.addView(dynamicTextView)
        }

        return addedItems
    }



    private fun getSavedLoginDetails() : String? {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(getString(R.string.save_login_details),
            MODE_PRIVATE
        )
        val loginDetails = sharedPref.getString(getString(R.string.save_login_details), "defaultValue")
        return loginDetails;
    }
}