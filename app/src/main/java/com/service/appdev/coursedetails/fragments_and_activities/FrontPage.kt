package com.service.appdev.coursedetails.fragments_and_activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.service.appdev.coursedetails.HomeController
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.CarousalAdapter
import com.service.appdev.coursedetails.models.ApiService
import com.service.appdev.coursedetails.models.ApiServiceBuilder
import com.service.appdev.coursedetails.models.ImageData
import com.service.appdev.coursedetails.repository.CollegeManagementRepository
import com.service.appdev.coursedetails.viewmodel.AdminManagementViewModel
import com.service.appdev.coursedetails.viewmodel.ImageState
import com.service.appdev.coursedetails.viewmodelfactory.AdminManagementViewModelFactory
import java.util.stream.Collectors


class FrontPage  : AppCompatActivity() {

    //private val apiService = ApiServiceBuilder.apiService;
//    val apiService = ApiServiceBuilder.createApiService(applicationContext)
//    private lateinit var viewPager: ViewPager2;
//
//    private val viewModel : AdminManagementViewModel by viewModels {
//        AdminManagementViewModelFactory(CollegeManagementRepository(apiService))
//    }

    private lateinit var apiService: ApiService
    private lateinit var viewPager: ViewPager2
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPager3: ViewPager2

    private lateinit var viewModel: AdminManagementViewModel


    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        // Initialize ApiService
        apiService = ApiServiceBuilder.createApiService(applicationContext)

        // Initialize ViewModel
        viewModel = ViewModelProvider(
            this,
            AdminManagementViewModelFactory(CollegeManagementRepository(apiService))
        )[AdminManagementViewModel::class.java]


        viewPager = findViewById(R.id.viewpager)
        viewPager2 = findViewById(R.id.viewpager2)
        viewPager3 = findViewById(R.id.viewpager3)

        val nextBtn : Button = findViewById(R.id.nextBtn);
        val showNews : LinearLayout = findViewById(R.id.showNews);

        val listOfImages = ArrayList<ImageData>()
        val listOfImages1 = ArrayList<ImageData>()
        var carousalAdapter: CarousalAdapter = CarousalAdapter(listOfImages1);
        viewPager.adapter = carousalAdapter;

        val listOfImages2 = ArrayList<ImageData>()
        var carousalAdapter1: CarousalAdapter = CarousalAdapter(listOfImages2);
        viewPager2.adapter = carousalAdapter1;

        val listOfImages3 = ArrayList<ImageData>()
        var carousalAdapter3: CarousalAdapter = CarousalAdapter(listOfImages3);
        viewPager3.adapter = carousalAdapter3;


        viewModel.getImagesSlider();
        viewModel.imageListState.observe(this, Observer {  state->
            when(state){
                is ImageState.Success ->{
                    listOfImages.clear();
                    listOfImages.addAll(state.images);

//                    val partSize = (listOfImages.size + 2) / 3 // Divide total items into 3 parts (ceiling of division)
//                    val part1 = listOfImages.subList(0, minOf(partSize, listOfImages.size))
//                    val part2 = listOfImages.subList(
//                        part1.size,
//                        minOf(part1.size + partSize, listOfImages.size)
//                    )
//                    val part3 = listOfImages.subList(
//                        part1.size + part2.size,
//                        listOfImages.size
//                    )\
                    val part1 = listOfImages.stream()
                        .filter { imageItem -> imageItem.sliderLevel == "Level1" }
                        .collect(Collectors.toList())

                    val part2 = listOfImages.stream()
                        .filter { imageItem -> imageItem.sliderLevel == "Level2" }
                        .collect(Collectors.toList())

                    val part3 = listOfImages.stream()
                        .filter { imageItem -> imageItem.sliderLevel == "Level3" }
                        .collect(Collectors.toList())

                    listOfImages1.addAll(part1.toList());
                    listOfImages2.addAll(part2.toList());
                    listOfImages3.addAll(part3.toList());
                    Log.i("Snath ", "Images "+listOfImages.size +", "+listOfImages1.size +", "+listOfImages2.size +", "+listOfImages3.size)
                    carousalAdapter.notifyDataSetChanged();
                    carousalAdapter1.notifyDataSetChanged();
                    carousalAdapter3.notifyDataSetChanged();

                    runSlider(listOfImages1);
                    runSlider2(listOfImages2);
                    runSlider3(listOfImages3);

                }
                is ImageState.Error ->{
                    Toast.makeText(this, "Error fetching Images", Toast.LENGTH_SHORT).show();
                }
            }

        })

        nextBtn.setOnClickListener(View.OnClickListener {
            //skip for saved login details
            val savedLogin = getSavedLoginDetails();
            val splitted = savedLogin?.split(",")
            var authToken  = "";
            var userType = "";
            if (splitted != null) {
                if(splitted.size>1) {
                    authToken = splitted[2];
                    userType = splitted[3];
                }
            }
            if (savedLogin != null) {
                if(savedLogin != "defaultValue" ){
                    if(userType=="Student" && authToken!="") {
                        val intent = Intent(this@FrontPage, HomeController::class.java);
                        startActivity(intent);
                    }
                    else if(userType=="Admin") {
                        val intent = Intent(this@FrontPage, AdminPanel::class.java);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                    else if(userType=="Institute") {
                        //default panel
                        val intent = Intent(this@FrontPage, AdminPanel::class.java);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                    }
                }
                else {
                    val intent = Intent(this@FrontPage, BeforeLogin::class.java);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
            }

        })

        showNews.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@FrontPage, ShowNews::class.java);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        })

    }
   private fun runSlider(listOfImages : ArrayList<ImageData>) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                try {
                    val currentItem = viewPager.currentItem
                    viewPager.currentItem = (currentItem + 1) % listOfImages.size
                    handler.postDelayed(this, 2000) // Change every 3 seconds
                } catch (ex: RuntimeException) {
                    print("Divide By Zero Exception " + ex.printStackTrace());
                }
            }
        }
        handler.post(runnable)
    }

    private fun runSlider2(listOfImages : ArrayList<ImageData>) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                try {
                    val currentItem = viewPager2.currentItem
                    viewPager2.currentItem = (currentItem + 1) % listOfImages.size
                    handler.postDelayed(this, 4000) // Change every 3 seconds
                } catch (ex: RuntimeException) {
                    print("Divide By Zero Exception " + ex.printStackTrace());
                }
            }
        }
        handler.post(runnable)
    }

    private fun runSlider3(listOfImages : ArrayList<ImageData>) {
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                try {
                    val currentItem = viewPager3.currentItem
                    viewPager3.currentItem = (currentItem + 1) % listOfImages.size
                    handler.postDelayed(this, 3000) // Change every 3 seconds
                } catch (ex: RuntimeException) {
                    print("Divide By Zero Exception " + ex.printStackTrace());
                }
            }
        }
        handler.post(runnable)
    }

    private fun getSavedLoginDetails() : String? {
        val sharedPref: SharedPreferences = getSharedPreferences(getString(R.string.save_login_details),MODE_PRIVATE)
        val loginDetails = sharedPref.getString(getString(R.string.save_login_details), "defaultValue")
        return loginDetails;
    }
}