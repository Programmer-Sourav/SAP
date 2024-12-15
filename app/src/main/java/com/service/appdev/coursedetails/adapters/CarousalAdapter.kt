package com.service.appdev.coursedetails.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.service.appdev.coursedetails.R

class CarousalAdapter(private val images : List<Int>) : RecyclerView.Adapter<CarousalAdapter.CarousalViewHolder>() {
    class CarousalViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
      val imageView : ImageView = itemView.findViewById<ImageView>(R.id.carousalImg);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarousalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.corousel_item, parent, false)
        return  CarousalViewHolder(view);
    }

    override fun onBindViewHolder(holder: CarousalViewHolder, position: Int) {
        holder.imageView.setImageResource(images[position])
    }

    override fun getItemCount(): Int {
        return images.size;
    }
}