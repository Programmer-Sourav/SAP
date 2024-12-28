package com.service.appdev.coursedetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.interfaces.OnClickListenerForPosition
import com.service.appdev.coursedetails.models.NewsItem

class NewsAdapter(private val listOfNews: ArrayList<NewsItem>) : RecyclerView.Adapter<NewsAdapter.NewsVH>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsVH {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.news_fragment, parent, false);
        return NewsVH(itemView)
    }

    override fun getItemCount(): Int {
        return listOfNews.size;
    }

    override fun onBindViewHolder(holder: NewsVH, position: Int) {
        val currentItem = listOfNews[position]

        holder.headerView.text = currentItem.header
        holder.announcementText.text = currentItem.notice
        holder.announcementDate.text = currentItem.announcementDate
    }


    inner class NewsVH(itemView: View) : ViewHolder(itemView) {

        val headerView = itemView.findViewById<TextView>(R.id.announcement_header);
        val announcementText = itemView.findViewById<TextView>(R.id.announcement_notice);
        val announcementDate = itemView.findViewById<TextView>(R.id.announcement_date);

//        init {
//            itemView.setOnClickListener(View.OnClickListener {
//                val position = absoluteAdapterPosition;
//                if (position != RecyclerView.NO_POSITION) {
//                    listener.onItemPositionListener(position);
//                }
//            })
//        }
    }
}

