package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.NewsAdapter
import com.service.appdev.coursedetails.interfaces.OnClickListenerForPosition
import com.service.appdev.coursedetails.models.AnnouncementData
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.models.NewsItem
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.AnnouncementDataState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory

class ShowNews : AppCompatActivity() {

    private var currentSelectedPositon : Int? = null;

    //val apiService: ApiService = ApiServiceBuilder.apiService;
//    val apiService = ApiServiceBuilder.createApiService(applicationContext)
//    private val viewModel: CourseDetailsViewModel by viewModels {
//        CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
//    }
    private lateinit var viewModel: CourseDetailsViewModel;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news)

        val viewPager : ViewPager2 = findViewById(R.id.viewPagerParent);

        val apiService = ApiServiceBuilder.createApiService(applicationContext)

        viewModel = ViewModelProvider(
            this,
            CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
        )[CourseDetailsViewModel::class.java]

        viewModel.getAnnouncementList();

        var newsList = ArrayList<AnnouncementData>();

        val newsAdapter = NewsAdapter(newsList);
        viewPager.adapter = newsAdapter;


        viewModel.announcementDataState.observe(this, Observer { state->
            when(state){
                is AnnouncementDataState.Success ->{
                    newsList.clear() // Clear old data
                    newsList.addAll(state.announcementDetails) // Add new data
                    newsList.reverse(); //to get the latest announcement on top
                    newsAdapter.notifyDataSetChanged()
                }
                is AnnouncementDataState.Error ->{
                    Toast.makeText(this, state.error, Toast.LENGTH_SHORT).show()
                }
            }
        })

        /*prevButton.setOnClickListener(View.OnClickListener {

            if (currentSelectedPositon != null) {
                Log.i("Snath, CSP ", ""+currentSelectedPositon);
                val selectedItem = dummyNewsList[currentSelectedPositon!!]
                Toast.makeText(this, "Clicked: $selectedItem", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
            }
        })*/

       /* nextButton.setOnClickListener(View.OnClickListener {
            if (currentSelectedPositon != null) {
                val selectedItem = dummyNewsList[currentSelectedPositon!!]
                Toast.makeText(this, "Clicked: $selectedItem", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
            }
        })*/

    }

}