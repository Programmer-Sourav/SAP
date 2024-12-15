package com.service.appdev.coursedetails.fragments

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.service.appdev.coursedetails.HomeController
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.CarousalAdapter

class FrontPage  : AppCompatActivity() {

    override fun onCreate(savedInstanceState : Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.frontpage)

        val viewPager: ViewPager2 = findViewById(R.id.viewpager)
        val nextBtn : Button = findViewById(R.id.nextBtn);

        val listOfImages = listOf(R.drawable.iem, R.drawable.horizon, R.drawable.pes)

        val carousalAdapter: CarousalAdapter = CarousalAdapter(listOfImages);
        viewPager.adapter = carousalAdapter;

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = viewPager.currentItem
                viewPager.currentItem = (currentItem + 1) % listOfImages.size
                handler.postDelayed(this, 3000) // Change every 3 seconds
            }
        }
        handler.post(runnable)


        nextBtn.setOnClickListener(View.OnClickListener {
            val intent = Intent(this@FrontPage, HomeController::class.java);
            startActivity(intent);
        })

    }
}