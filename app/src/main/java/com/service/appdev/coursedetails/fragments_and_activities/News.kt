package com.service.appdev.coursedetails.fragments_and_activities

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.adapters.NewsAdapter
import com.service.appdev.coursedetails.models.NewsItem

class News : Fragment() {

    private lateinit var view : View;
    private lateinit var newsListUi : RecyclerView;
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        view = inflater.inflate(R.layout.news_list_ui, container, false)
        newsListUi = view.findViewById<RecyclerView>(R.id.newsList);

        val dummyNewsList = ArrayList<NewsItem>();
        dummyNewsList.add(NewsItem("Admission Starts", "12-27-2024 08:42 PM","Hello Guys, nothing here."))
        dummyNewsList.add(NewsItem("Admission Closed", "12-27-2024 08:42 PM","Hello Guys, nothing here."))

        val newsAdapter : NewsAdapter = NewsAdapter(dummyNewsList);
        val layoutManager = LinearLayoutManager(activity);
        layoutManager.orientation = LinearLayoutManager.VERTICAL;
        newsListUi.adapter = newsAdapter;
        newsListUi.layoutManager = layoutManager;
        return view;
    }
}