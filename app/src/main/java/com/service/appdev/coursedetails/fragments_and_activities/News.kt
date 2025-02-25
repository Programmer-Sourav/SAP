package com.service.appdev.coursedetails.fragments_and_activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.NewsAdapter
import com.service.appdev.coursedetails.interfaces.OnClickListenerForPosition
import com.service.appdev.coursedetails.models.AnnouncementData
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.AnnouncementDataState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory

class News : Fragment(), OnClickListenerForPosition {

    private lateinit var view : View;
    private lateinit var newsListUi : RecyclerView;

    //private val apiService : ApiService = ApiServiceBuilder.apiService ;
//    val apiService = ApiServiceBuilder.createApiService(requireContext())
//
//    private val viewModel : CourseDetailsViewModel by viewModels {
//        CourseDetailsViewModelFactory(CollegeManagementRepository(apiService));
//    }

    private lateinit var viewModel: CourseDetailsViewModel;
    private  lateinit var textCountTv: TextView;
    var incomingNewsList = ArrayList<AnnouncementData>();

    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.news_list_ui, container, false)
        newsListUi = view.findViewById<RecyclerView>(R.id.newsList);
        textCountTv = view.findViewById(R.id.textCount);


        val newsAdapter : NewsAdapter = NewsAdapter(incomingNewsList, this);

        val layoutManager = LinearLayoutManager(activity)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        newsListUi.adapter = newsAdapter
        newsListUi.layoutManager = layoutManager


        val apiService = ApiServiceBuilder.createApiService(requireActivity())

        viewModel = ViewModelProvider(
            this,
            CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
        )[CourseDetailsViewModel::class.java]

        viewModel.getAnnouncementList();

        viewModel.announcementDataState.observe(viewLifecycleOwner, Observer { state->
             when(state){
                 is AnnouncementDataState.Success ->{
                     incomingNewsList.clear() // Clear old data
                     incomingNewsList.addAll(state.announcementDetails) // Add new data
                     incomingNewsList.reverse();
                     textCountTv.setText(incomingNewsList.size.toString());
                     newsAdapter.notifyDataSetChanged()
                 }
                 is AnnouncementDataState.Error ->{
                     Toast.makeText(requireContext(), state.error, Toast.LENGTH_SHORT).show()
                 }
             }
        })


        return view;
    }

    override fun onItemPositionListener(position: Int) {
        val linkAtPos = incomingNewsList[position].filePath;
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkAtPos));
        startActivity(intent);
    }
    fun showDialog() {
        val progressDialog = CustomDialogFragment()
        progressDialog.show(requireActivity().supportFragmentManager, "ProgressDialog")
    }
}