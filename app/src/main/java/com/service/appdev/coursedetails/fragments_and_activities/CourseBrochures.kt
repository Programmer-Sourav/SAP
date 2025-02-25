package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.CustomImageListAdapter
import com.service.appdev.coursedetails.adapters.CustomSpinnerAdapter
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.models.BrochureResponse
import com.service.appdev.coursedetails.models.CourseBrochure
import com.service.appdev.coursedetails.models.CourseDetails
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.BrochureDataState
import com.service.appdev.coursedetails.viewmodel.CourseDataState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory
import java.net.URLEncoder

class CourseBrochures : Fragment() {
    private lateinit var view: View;

    // private val apiService = ApiServiceBuilder.apiService
//    private val viewModel: CourseDetailsViewModel by viewModels{
//        CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
//    }
    private lateinit var viewModel: CourseDetailsViewModel;
    private lateinit var myAvailableColleges : Spinner;

    private lateinit var customAdapter: CustomSpinnerAdapter

    private var brochureList : ArrayList<CourseBrochure> = ArrayList();
    private var courseList : ArrayList<CourseDetails> = ArrayList();

    private var selectedCollege : String = "";

    private lateinit var coursesBrochureList : RecyclerView;

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.fragment_brochure_details, container, false)

        myAvailableColleges = view.findViewById<Spinner>(R.id.my_available_colleges);
        val collegeNotSelected = view.findViewById<TextView>(R.id.collegeNotSelected);
        coursesBrochureList = view.findViewById<RecyclerView>(R.id.brochureList);
        val shareAppLink = view.findViewById<TextView>(R.id.shareAppLink);

        //val webView = view.findViewById<WebView>(R.id.webView);


        val apiService = ApiServiceBuilder.createApiService(requireContext())
        viewModel = ViewModelProvider(
            this,
            CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
        )[CourseDetailsViewModel::class.java]



        viewModel.brochureDetailsState.observe(viewLifecycleOwner, Observer { state->
            when(state){
                is BrochureDataState.Success->{
                    val brochureImagesList = state.brochureDetails.map{it.brochureImages}
                    //setupSpinner(brochureImagesList) // Pass the list to the spinner setup method
                    setUpRecyclerView(brochureImagesList as ArrayList<String>)
                }

                is BrochureDataState.Error->{
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }

        })

        viewModel.getCollegesList();

        viewModel.courseDetailsState.observe(viewLifecycleOwner, Observer { state->
            when(state){
                is CourseDetailsState.Success->{
                    val collegeList = state.collegeList.map{it.collegeName}
                    setupSpinner(collegeList) // Pass the list to the spinner setup method
                }

                is CourseDetailsState.Error->{
                    Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                }
            }

        })

//       val encodedUrl = URLEncoder.encode("https://www.travelsawari.com/brochures/812476e5e876cfc7.pdf", "UTF-8")
//        webView.settings.javaScriptEnabled = true
//        webView.settings.allowFileAccess = true
//        webView.settings.builtInZoomControls = true
//        webView.settings.displayZoomControls = false
//        webView.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
//        webView.loadUrl("https://docs.google.com/gview?embedded=true&url=$encodedUrl");
        shareAppLink.setOnClickListener(View.OnClickListener {
            /*Create an ACTION_SEND Intent*/
            val intent = Intent(Intent.ACTION_SEND)
            /*This will be the actual content you wish you share.*/
            val applink = "https://play.google.com/store/apps/details?id=com.service.appdev.coursedetails&hl=en"
            val shareBody = "Make your college admission easier. Download our app now- $applink"
            /*The type of the content is text, obviously.*/
            intent.setType("text/plain")
            /*Applying information Subject and Body.*/
            intent.putExtra(Intent.EXTRA_SUBJECT, getString(com.service.appdev.coursedetails.R.string.share_subject))
            intent.putExtra(Intent.EXTRA_TEXT, shareBody)
            /*Fire!*/
            startActivity(Intent.createChooser(intent, getString(com.service.appdev.coursedetails.R.string.share_using)))
        })
        //       val dummyCourseList = ArrayList<CourseData>();
//        dummyCourseList.add(CourseData("CSE", R.drawable.cse));
//        dummyCourseList.add(CourseData("ECE", R.drawable.ece));
//        dummyCourseList.add(CourseData("IT", R.drawable.ites));



        return view;
    }

    private fun setUpRecyclerView(brochureList: ArrayList<String>) {
      val adapter = CustomImageListAdapter( brochureList, requireActivity())
        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        coursesBrochureList.adapter = adapter;
        coursesBrochureList.layoutManager = layoutManager
    }


    private fun setupSpinner(collegeList: List<String>) {
        val updatedCollegeList = mutableListOf("No college selected")
        updatedCollegeList.addAll(collegeList)

        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            updatedCollegeList
        ).also { it.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) }

        myAvailableColleges.adapter = adapter
        myAvailableColleges.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                selectedCollege = updatedCollegeList[position]
                Log.i("Snath ", "position "+position)
                if (selectedCollege != null) {
                    Log.d("SelectedCourse", selectedCollege)
                    //viewModel.getCourseListByCollege(selectedCollege);
                    viewModel.getBrochureListByCollege(selectedCollege);
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle no selection
            }
        }
    }

}



