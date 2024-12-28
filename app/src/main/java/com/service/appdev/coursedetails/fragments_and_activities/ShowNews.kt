package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.NewsAdapter
import com.service.appdev.coursedetails.interfaces.OnClickListenerForPosition
import com.service.appdev.coursedetails.models.NewsItem

class ShowNews : AppCompatActivity() {

    private var currentSelectedPositon : Int? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.news)

        val viewPager : ViewPager2 = findViewById(R.id.viewPagerParent);
//        val prevButton : Button = findViewById<Button>(R.id.prevButton);
//        val nextButton : Button = findViewById<Button>(R.id.nextBtn)

        val dummyNewsList = ArrayList<NewsItem>();
        dummyNewsList.add(NewsItem("Admission Starts", "12-27-2024 08:42 PM","Hello Guys, nothing here."))
        dummyNewsList.add(NewsItem("Admission Closed", "12-27-2024 08:42 PM","Hello Guys, nothing here."))

        val newsAdapter = NewsAdapter(dummyNewsList);
        viewPager.adapter = newsAdapter;

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