package com.service.appdev.coursedetails.fragments_and_activities

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.Text
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.CustomCollegeListSpinner
import com.service.appdev.coursedetails.adapters.CustomeCourseAdapter
import com.service.appdev.coursedetails.databinding.FragmentApplicationFormFilupBinding
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.models.CoursesAvailableData
import com.service.appdev.coursedetails.repository.ApplicationUploadRepository
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.upload.UploadScreen
import com.service.appdev.coursedetails.viewmodel.ApplicationFormViewModel
import com.service.appdev.coursedetails.viewmodel.ApplicationUploadState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodel.CoursesAvailableState
import com.service.appdev.coursedetails.viewmodelfactory.ApplicationFormViewModelFactory
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date


class ApplicationUpload : Fragment(), AdapterView.OnItemSelectedListener {

    private lateinit  var adapter: CustomCollegeListSpinner
    private lateinit var entranceIdSelected: String;

    private var _binding: FragmentApplicationFormFilupBinding? = null;
    private val binding get() = _binding;

    //private val apiService = ApiServiceBuilder.apiService
    private lateinit var apiService: ApiService;
    private lateinit var viewModel: ApplicationFormViewModel;
    private lateinit var cdViewModel: CourseDetailsViewModel;
    private lateinit var courseAddedLL: LinearLayout;
    val addedItems = ArrayList<String>()
    val copiedItems = ArrayList<String>()
    private val coursesAvailable = ArrayList<CoursesAvailableData>();
    private var selectedCourse: String = "Select Course";
    lateinit var cwadapter: CustomeCourseAdapter;
    lateinit var courseWillingInput: Spinner;
    lateinit var myAvailableColleges: Spinner;
    private var selectedCollege: String = "";
    private var isLargeLayout: Boolean = false;
    private lateinit var uploadWorkRequest: OneTimeWorkRequest
    private lateinit var inputData: Data;
    private lateinit var inputData1: Data;
    private lateinit var constraints: Constraints;
    private lateinit var verificationStatus: TextView;
    private var  receiptData : HashMap<String, String> = HashMap<String, String>();
    private var upiId: String = "";
    private var amountPaid: String = "";
    private var dateTime : String = "";
    private var status: String = "";
    private var userUniqueId: String = "";
    private lateinit var progressBar: ProgressBar
    private lateinit var filePaths : ArrayList<String>;
    //ONE course to MANY colleges relationship
    private var collegesToCourse : HashMap<String, ArrayList<HashMap<String, Boolean>>> = HashMap<String, ArrayList<HashMap<String, Boolean>>>();
    lateinit var collegeList: List<String>;
    val selectedColleges = HashMap<String, Boolean>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentApplicationFormFilupBinding.inflate(inflater, container, false);
        val qrImage = _binding!!.qrcode;
        myAvailableColleges = _binding?.collegeWillingInput!!;
        val apiService = ApiServiceBuilder.createApiService(requireActivity())
        viewModel = ViewModelProvider(
            this,
            ApplicationFormViewModelFactory(ApplicationUploadRepository(apiService))
        )[ApplicationFormViewModel::class.java]

        cdViewModel = ViewModelProvider(
            this,
            CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
        )[CourseDetailsViewModel::class.java]

        binding?.viewModel = viewModel
        binding?.lifecycleOwner = this
        verificationStatus = binding?.verificationStatus!!;

        isLargeLayout = resources.getBoolean(R.bool.large_layout)
        viewModel.getCourses();

        progressBar = binding?.progressBar!!;

        val btnUploadScreenshot = _binding!!.btnSelectReceipt;

        Log.d("Snath ", "Selected Colleges Size "+selectedColleges.size)

        if (btnUploadScreenshot != null) {
            btnUploadScreenshot.setOnClickListener {
                openFilePicker()
            }
        }

//        Log.d("Snath ", "Selected College "+CollegeCompanionObject.selectedColleges.size)
        viewModel.courseAvailableState.observe(requireActivity(), Observer { state ->
            when (state) {
                is CoursesAvailableState.Error -> Toast.makeText(
                    requireContext(),
                    "Oops! Some Error Occurred!",
                    Toast.LENGTH_SHORT
                ).show()

                is CoursesAvailableState.Success -> {
                    coursesAvailable.addAll(state.courses);
                    Log.i("Snath ", "" + coursesAvailable.size)
                    setupSpinner(coursesAvailable)
                }
            }
        })

        cdViewModel.courseDetailsState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is CourseDetailsState.Success -> {
                    collegeList = state.collegeList.map { it.collegeName }
                    setupSpinnerForCollegeList(collegeList) // Pass the list to the spinner setup method
                    Log.d("Snath ", "CollegeList "+collegeList.size)
                }

                is CourseDetailsState.Error -> {
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }
        })


        return binding?.root;
    }

    private val filePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            filePaths = mutableListOf<String>() as ArrayList<String>

            // Handle multiple file selection
            intent?.clipData?.let { clipData ->
                for (i in 0 until clipData.itemCount) {
                    val uri = clipData.getItemAt(i).uri
                    val filePath = getFilePathFromUri(uri)
                    filePaths.add(filePath)
                }
            }

            // Handle single file selection
            intent?.data?.let { uri ->
                val filePath = getFilePathFromUri(uri)
                filePaths.add(filePath)
            }
            // Input data for the worker

            constraints = androidx.work.Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

        }
    }
    private fun getFilePathFromUri(uri: Uri): String {
        if(Build.VERSION.SDK_INT<29){
            var filePath: String = "";
            val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
            cursor?.use {
                val columnIndex = it.getColumnIndex(MediaStore.Files.FileColumns.DATA)
                if (columnIndex != -1) {
                    it.moveToFirst()
                    filePath = it.getString(columnIndex)
                }
            }
            return filePath;
        }
        else{
            try {
                val inputStream = requireActivity().contentResolver.openInputStream(uri) ?: return "no filepath found"
                val file = File(requireActivity().cacheDir, getFileName(uri))
                val outputStream = file.outputStream()
                inputStream.use { input ->
                    outputStream.use { output ->
                        input.copyTo(output)
                    }
                }
                return file.absolutePath
            } catch (e: Exception) {
                e.printStackTrace()
                return "no filepath found"
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var name = "temp_file"
        val cursor = requireActivity().contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                if (nameIndex >= 0) {
                    name = it.getString(nameIndex) ?: "temp_file"
                }
            }
        }
        validatePaymentScreenshot(uri);
        return name
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*" // MIME type for all files
            putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true) // Allow multiple file selection
        }
        filePickerLauncher.launch(intent)
    }
    // Track selected colleges

    private fun setupSpinnerForCollegeList(collegeList: List<String>) {
        val updatedCollegeList = mutableListOf("No course selected")
        updatedCollegeList.addAll(collegeList)

        Log.d("Snath", "Selected Colleges Size " + selectedColleges.size)

        adapter = CustomCollegeListSpinner(
            requireContext(),
            R.layout.multiselection_spinner,
            updatedCollegeList as ArrayList<String>,
            selectedColleges
        )

        // You no longer need to setDropDownViewResource, as we've handled dropdown in getDropDownView
        myAvailableColleges.adapter = adapter

        val selectedCollegesForTheCourse = ArrayList<HashMap<String, Boolean>>();
        selectedCollegesForTheCourse.addAll(listOf(selectedColleges))

        collegesToCourse.put(selectedCourse, selectedCollegesForTheCourse)
        // Optionally set up an item selected listener for the spinner (collapsed view)
        myAvailableColleges.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>?,
                    view: View?,
                    position: Int,
                    id: Long
                ) {
                    val itemAtPosition: String = myAvailableColleges.getItemAtPosition(position).toString()
                    Log.d("Snath", "Item At Position $itemAtPosition")

                    // Update the selectedColleges map based on the item selected
                    if (selectedColleges[itemAtPosition] == true) {
                        // If already selected, unselect it
                        selectedColleges[itemAtPosition] = false
                    } else {
                        // If not selected, select it
                        selectedColleges[itemAtPosition] = true
                    }

                    // Notify the adapter to update the spinner view
                    adapter.notifyDataSetChanged()

                    // Log the selected colleges
                    Log.d("Snath", "Selected Items: ${selectedColleges.keys}")

                }

                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Handle when nothing is selected (if needed)
                    val itemAtPosition: String =
                        myAvailableColleges.getItemAtPosition(0).toString()
                    selectedColleges.put(itemAtPosition, true)
                    val selectedItems = adapter.getSelectedItems()
                }
            }

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
        courseWillingInput = binding?.courseWillingInput!!;
        myAvailableColleges = binding?.collegeWillingInput!!

        courseAddedLL = binding?.linearLayout!!;
        val submitForm = binding?.formSubmit;

        val savedLogin = getSavedLoginDetails();
        val splitted = savedLogin?.split(",")
        var username = "";
        if (splitted != null) {
            if (splitted.size > 1)
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
        Log.i("Snath ", "Courses Available " + coursesAvailable.size)


        /*cwadapter = CustomeCourseAdapter(
            requireActivity(),
            android.R.layout.simple_spinner_item,
            coursesAvailable
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        courseWillingInput?.adapter = cwadapter
        // Set the `onItemSelectedListener` outside the adapter initialization block
        courseWillingInput?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedItem = parent?.getItemAtPosition(position).toString()
                Log.i("Snath ", "Selected Item "+selectedItem)
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
        }*/


//        val dynamicTextView : TextView = TextView(requireContext());
//        dynamicTextView.text = binding?.courseWillingInput?.selectedItem.toString()
//        courseAddedLL.addView(dynamicTextView);
//        if(binding?.courseWillingInput?.selectedItem.toString()!==""){
//            addedItems.add(courseWillingInput?.selectedItem.toString())
//        }


        submitForm?.setOnClickListener(View.OnClickListener {
//            var upiId: String = "";
//            var amountPaid: String = "";
//            var dateTime : String = "";
//            var status: String = "";
            if(receiptData.size>0) {
                for ((key, value) in receiptData.entries) {
                    if (key.equals("Amount"))
                        amountPaid = value
                    if (key.equals("UPI ID"))
                        upiId = value
                    if (key.equals("Date"))
                        dateTime = value
                    if (key.equals("status"))
                        status = value
                }
                if (amountPaid.equals("100") && upiId.contains("idwmtraininginstitute") && status.contains(
                        "Successful"
                    ) || status.contains("Completed")
                ) {
                    verificationStatus.text = "Payment Verification Successful."
                } else {
                    verificationStatus.text = "Manual Verification pending."
                }
            }
//            if (binding?.studentFirstName?.text.toString()
//                    .isNotEmpty() && binding?.studentLastName?.text.toString()
//                    .isNotEmpty() && binding?.parentFirstName?.text.toString().isNotEmpty()
//                && binding?.parentLastName?.text.toString()
//                    .isNotEmpty() && binding?.fullSchoolName?.text.toString()
//                    .isNotEmpty() && binding?.fullTwelfthMarks?.text.toString().isNotEmpty()
//                && binding?.fullTenthMarks?.text.toString()
//                    .isNotEmpty() && binding?.fullJointMarks?.text.toString().isNotEmpty() &&
//                binding?.studentEmailEt?.text.toString()
//                    .isNotEmpty()
//            )

                if (binding?.studentFirstName?.text!!.isEmpty()) {
                    binding?.studentFirstName?.error = "Please Enter Student's First Name";
                    showErrorToast();
                }
                else if (binding?.studentLastName?.text!!.isEmpty()) {
                    binding?.studentLastName?.error = "Please Enter Student's Last Name";
                    showErrorToast();
                }
                else if (binding?.parentFirstName?.text!!.isEmpty()) {
                    binding?.parentFirstName?.error = "Please Enter Student's Last Name";
                    showErrorToast();
                }
                else if (binding?.parentLastName?.text!!.isEmpty()) {
                    binding?.parentLastName?.error = "Please Enter Student's Last Name";
                    showErrorToast();
                }
                else if (binding?.fullSchoolName?.text!!.isEmpty()) {
                    binding?.fullSchoolName?.error = "Please Enter Student's Last Name";
                    showErrorToast();
                }
                else if (binding?.fullTwelfthMarks?.text!!.isEmpty()) {
                    binding?.fullTwelfthMarks?.error = "Please Enter Student's Last Name";
                    showErrorToast();
                }
                else if (binding?.fullTenthMarks?.text!!.isEmpty()) {
                    binding?.fullTenthMarks?.error = "Please Enter Student's Last Name";
                    showErrorToast();
                }
                else if(binding?.fullJointMarks?.text!!.isEmpty()){
                    binding?.fullJointMarks?.error = "Please Enter Marks"
                    showErrorToast();
                }
                else if(binding?.streetAddress1?.text!!.isEmpty()){
                    binding?.streetAddress1?.error = "Please Enter Address."
                    showErrorToast();
                }
                else if(binding?.streetAddress2?.text!!.isEmpty()){
                    binding?.streetAddress2?.error = "Please Enter Address"
                    showErrorToast();
                }
                else if(binding?.city?.text!!.isEmpty()){
                    binding?.city?.error = "Please Enter City"
                    showErrorToast();
                }
                else if(binding?.state?.text!!.isEmpty()){
                    binding?.state?.error = "Please Enter State"
                    showErrorToast();
                }
                else if(binding?.pincode?.text!!.isEmpty()){
                    binding?.pincode?.error = "Please Enter Pincode"
                    showErrorToast();
                }
                else if(binding?.phoneNumberInput?.text!!.isEmpty()){
                    binding?.phoneNumberInput?.error = "Please Enter Phone Number"
                    showErrorToast();
                }
                else if(binding?.studentEmailEt?.text!!.isEmpty()){
                    binding?.studentEmailEt?.error = "Please Enter Student's Email"
                    showErrorToast();
                }
                else if (!binding?.studentEmailEt?.text.toString()
                        .lowercase()
                        .matches(Regex("^[^@]+@[^@]+\\.[^@]+$"))) {
                    binding?.studentEmailEt?.error = "Please Enter Valid Email"
                    showErrorToast();
                }
                else
                 {
                     showInfoToast();
                Log.d(
                    "Snath ",
                    "Receipt Data " + amountPaid + ", " + upiId + ", " + dateTime + ", " + status
                )
                var listConvertedToString = "";

                for (item in copiedItems) {
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
                        username,
                        selectedCollege
                    )
                    showDialog();
                }
        })



        viewModel.applicationUploadState.observe(viewLifecycleOwner, Observer { state ->
            when (state) {

                is ApplicationUploadState.Success -> {
                    Toast.makeText(
                        requireContext(),
                        state.data,
                        Toast.LENGTH_SHORT
                    ).show()
                    userUniqueId = state.studentId;
                    uploadScreenshotOfPayment(userUniqueId, amountPaid, upiId, dateTime, status );
                    val uploadScreen = Intent(requireActivity(), UploadScreen::class.java);
                    startActivity(uploadScreen);
                }

                is ApplicationUploadState.Error -> {
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }
        })


    }

    private fun showErrorToast() {
       Toast.makeText(requireActivity(), "Please fill all the fields!", Toast.LENGTH_SHORT).show();
    }
    private fun showInfoToast() {
        Toast.makeText(requireActivity(), "Submitting Data!", Toast.LENGTH_SHORT).show();
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
            dynamicTextView.setBackgroundColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.purple_200
                )
            )
            dynamicTextView.gravity = Gravity.CENTER_VERTICAL // Center text vertically

            // Set padding
            dynamicTextView.setPadding(32, 16, 32, 16) // (left, top, right, bottom)

            // Set text appearance dynamically (size, color, etc.)
            dynamicTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18f) // Text size in SP
            dynamicTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            dynamicTextView.text = item

            // Add a delete icon to the right
            val deleteIcon = ContextCompat.getDrawable(
                requireContext(),
                R.drawable.ic_delete_bin
            ) // Replace with your delete icon
            deleteIcon?.setTint(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.white
                )
            ) // Optional: Tint the icon
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


    private fun getSavedLoginDetails(): String? {
        val sharedPref: SharedPreferences = requireContext().getSharedPreferences(
            getString(R.string.save_login_details),
            MODE_PRIVATE
        )
        val loginDetails =
            sharedPref.getString(getString(R.string.save_login_details), "defaultValue")
        return loginDetails;
    }

    private fun setupSpinner(coursesList: List<CoursesAvailableData>) {
        val updatedCollegeList = mutableListOf("Select Course")

        for (item in coursesList) {
            updatedCollegeList.addAll(listOf(item.coursename))
        }
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            updatedCollegeList
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        courseWillingInput.adapter = adapter
        courseWillingInput.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                selectedCourse = updatedCollegeList[position]
                Log.i("Snath ", "position " + position)
                // Avoid adding the default hint item or duplicates
                if ((!addedItems.contains(selectedCourse) && !copiedItems.contains(selectedCourse)) && selectedCourse != "Select Course") {
                    addedItems.clear();
                    addedItems.add(selectedCourse)
                    copiedItems.add(selectedCourse);
                    setTextViews()
                    if (selectedCourse != "Select Course") {
                        // cdViewModel.getCourseListByCollege(selectedCourse);
                        for (item in addedItems){
                            selectedCourse = "$selectedCourse, $item";
                        }
                        cdViewModel.getCollegeListByCourse(selectedCourse);
                    }
                }

            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }
    }

    fun showDialog() {
        val progressDialog = CustomDialogFragment()
        progressDialog.show(requireActivity().supportFragmentManager, "ProgressDialog")
    }

    private fun validatePaymentScreenshot( screenshotUri: Uri){
        val inputImage = InputImage.fromFilePath(requireActivity(), screenshotUri);
        val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        recognizer.process(inputImage).addOnSuccessListener {
            visionText ->Log.d("MLKit", "Recognized text: ${visionText.text}")
                val hmp : HashMap<String, String> = HashMap<String, String>();
               receiptData = extractReceiptData(visionText);
              Log.d("Snath ", "Receipt Data "+receiptData);
        }.addOnFailureListener{e->Log.e("MLKit", "Text recognition failed: ${e.message}")}

    }

    private fun isValidate(input: String): String {
        val dateInput = input;
        var date_s = input;
        if(dateInput.contains("on")){
            val dateArr = dateInput.split(" on ")
            Log.d("Snath ", dateArr.toString())
            val timePart = dateArr[0];
            Log.d("Snath ", timePart)
            val datePart = dateArr[1];
            Log.d("Snath ", "DatePart "+datePart)
            date_s = datePart+", "+timePart;
            Log.d("Snath ","Date "+ date_s)
        }
            // Define the date format
//            val dateFormat = SimpleDateFormat("dd MMM yyyy,hh:mm a", Locale.ENGLISH)
//            dateFormat.isLenient = false // Ensures strict date parsing

        return try {
            // Try parsing the input text as a date
           // dateFormat.parse(date_s)
            date_s

        } catch (e: Exception) {
            e.message.toString();
        }
    }

    fun uploadScreenshotOfPayment(
        userUniqueId: String,
        amountPaid: String,
        upiId: String,
        dateTime: String,
        status: String
    ) {
        Log.i("Snath ", " User Receipt Data "+userUniqueId + " " +amountPaid +" "+ upiId +" "+ dateTime+" "+status);
        uploadFileOnButtonClick()
    }

    fun extractReceiptData(visionText: Text): HashMap<String, String> {
        val receiptMap = HashMap<String, String>()

        // Iterate through recognized text blocks
        for (block in visionText.textBlocks) {
            for (line in block.lines) {
                val text = line.text
                var parsedDate = "";
                val sdf: SimpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                val sdfOriginal1 = SimpleDateFormat("dd MMM yyyy,hh:mm a")
                val sdfOriginal2 = SimpleDateFormat("dd MMM yyyy, h:mm a")
                val sdfOriginal3 = SimpleDateFormat("dd MMM yyyy, hh:mm a")

                val parsedDate1 : Date;
                val parsedDate2 : Date;
                val parsedDate3 : Date;
                val parsedDate4 : Date;
                // Parse Date (e.g., "16 Jan 2025")
                // || text.matches(Regex("\\d{2} [A-Za-z]{3} \\d{4} ,\\d{2}:\\d{2} (?i)(am|pm)"))
                if (text.matches(Regex("\\d{2} [A-Za-z]{3} \\d{4},\\d{2}:\\d{2} (?i)(am|pm)"))) {
                    parsedDate1 = sdfOriginal1.parse(text)!!
                    parsedDate = sdf.format(parsedDate1);
                    receiptMap["Date"] = parsedDate
                }
                else if(text.matches(Regex("\\d{2} [A-Za-z]{3} \\d{4}, \\d{1}:\\d{2} (?i)(am|pm)"))){
                    parsedDate2 = sdfOriginal2.parse(text)!!
                    parsedDate = sdf.format(parsedDate2);
                    receiptMap["Date"] = parsedDate
                }
                else if(text.matches(Regex("\\d{2} [A-Za-z]{3} \\d{4}, \\d{2}:\\d{2} (?i)(am|pm)"))){
                    parsedDate3 = sdfOriginal3.parse(text)!!
                    parsedDate = sdf.format(parsedDate3);
                    receiptMap["Date"] = parsedDate
                }
                else if(Character.isDigit(text.get(0)) && text.contains("on")){
                   parsedDate =  isValidate(text)
                    Log.i("Snath ", "Parsed Date "+parsedDate)
                    var parsedDate3 : Date = sdfOriginal3.parse(parsedDate)
                    parsedDate = sdf.format(parsedDate3);
                    receiptMap["Date"] = parsedDate
                }

                // Parse Amount (e.g., "₹1234.56")
//                if (text.contains("100")) {
//                    receiptMap["Amount"] = "100"
//                }

                if (text.contains("Completed") || text.contains("Successful")) {
                    receiptMap["status"] = text;
                }

                // Parse UPI ID (e.g., "example@upi")
                if (text.contains("idwmtraininginstitute")) {
                    receiptMap["UPI ID"] = text.replace(".\\s+","");
                }

                // Parse Recipient (assuming it's a name after a keyword like "To:" or "Paid to")
                if (text.startsWith("To:") || text.startsWith("Paid to") || text.startsWith("Banking Name:")) {
                    receiptMap["Recipient"] = text.substringAfter(":").trim()
                }
            }
        }

        return receiptMap
    }

    private fun uploadFileOnButtonClick(){
        inputData = Data.Builder().putStringArray("filePath", filePaths.toTypedArray())
            .putString("upiId",upiId)
            .putString("amountPaid",amountPaid)
            .putString("dateTime",dateTime)
            .putString("status",status)
            .putString("userUniqueId", userUniqueId)
            .build();
        // Create a OneTimeWorkRequest
        uploadWorkRequest = OneTimeWorkRequestBuilder<UploadReceiptWorker>()
            .setConstraints(constraints)
            .setInputData(inputData).build();

        // Enqueue the work
        WorkManager.getInstance(requireActivity()).enqueue(uploadWorkRequest);

        WorkManager.getInstance(requireActivity()).getWorkInfoByIdLiveData(uploadWorkRequest.id)
            .observe(requireActivity()){
                    workInfo ->
                if (workInfo!= null){
                    when(workInfo.state){
                        WorkInfo.State.SUCCEEDED ->{
                            // Upload succeeded
                            progressBar.visibility = View.INVISIBLE;
                            Toast.makeText(requireActivity(), "Upload successful!", Toast.LENGTH_SHORT).show()
                        }
                        WorkInfo.State.FAILED ->{
                            // Upload failed
                            progressBar.visibility = View.INVISIBLE;
                            Toast.makeText(requireActivity(), "Upload failed!", Toast.LENGTH_SHORT).show()
                        }
                        WorkInfo.State.RUNNING ->{
                            // Upload in progress
                            val progress = workInfo.progress.getInt("progress", 0)
                            progressBar.visibility = View.VISIBLE;
                            progressBar.progress = progress
                        }
                        else->{
                            // Other states: ENQUEUED, CANCELLED, BLOCKED
                        }
                    }
                }
            }
    }


}