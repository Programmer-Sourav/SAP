package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.NewsAdapter
import com.service.appdev.coursedetails.interfaces.OnClickListenerForPosition
import com.service.appdev.coursedetails.models.AnnouncementData
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.AnnouncementDataState
import com.service.appdev.coursedetails.viewmodel.CourseDetailsViewModel
import com.service.appdev.coursedetails.viewmodelfactory.CourseDetailsViewModelFactory

class ShowNews : AppCompatActivity(), OnClickListenerForPosition {

    private var currentSelectedPositon : Int? = null;

    //val apiService: ApiService = ApiServiceBuilder.apiService;
//    val apiService = ApiServiceBuilder.createApiService(applicationContext)
//    private val viewModel: CourseDetailsViewModel by viewModels {
//        CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
//    }
    private lateinit var viewModel: CourseDetailsViewModel;
    var newsList = ArrayList<AnnouncementData>();


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news)

        val viewPager : ViewPager2 = findViewById(R.id.viewPagerParent);
        val apiService = ApiServiceBuilder.createApiService(applicationContext)

        val prevButton = findViewById<Button>(R.id.prevButton);
        val nextButton = findViewById<Button>(R.id.nextBtn);

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                // Update the current position whenever a new page is selected
                currentSelectedPositon = position
                Log.i("ViewPager2", "Current page: $currentSelectedPositon")
            }
        })

        viewModel = ViewModelProvider(
            this,
            CourseDetailsViewModelFactory(CollegeManagementRepository(apiService))
        )[CourseDetailsViewModel::class.java]

        viewModel.getAnnouncementList();



        val newsAdapter = NewsAdapter(newsList, this);
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

        prevButton.setOnClickListener {
            if (currentSelectedPositon != null) {
                if (currentSelectedPositon!! > 0) {
                    viewPager.currentItem = currentSelectedPositon!! - 1
                } else {
                    Toast.makeText(this, "No previous item available", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
            }
        }

        nextButton.setOnClickListener {
            if (currentSelectedPositon != null) {
                if (currentSelectedPositon!! < newsList.size - 1) {
                    viewPager.currentItem = currentSelectedPositon!! + 1
                } else {
                    Toast.makeText(this, "No next item available", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "No item selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onItemPositionListener(position: Int) {
        val linkAtPos = newsList.get(position).filePath;
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(linkAtPos));
        startActivity(intent);
    }

}