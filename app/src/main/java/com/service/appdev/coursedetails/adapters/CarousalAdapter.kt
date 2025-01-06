package com.service.appdev.coursedetails.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.service.appdev.coursedetails.R
import com.service.appdev.coursedetails.models.ImageData
import com.squareup.picasso.Picasso

class CarousalAdapter(private val images : List<ImageData>) : RecyclerView.Adapter<CarousalAdapter.CarousalViewHolder>() {
    class CarousalViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
      val imageView : ImageView = itemView.findViewById<ImageView>(R.id.carousalImg);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarousalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.corousel_item, parent, false)
        return  CarousalViewHolder(view);
    }

    override fun onBindViewHolder(holder: CarousalViewHolder, position: Int) {
//        holder.imageView.setImageURI(Uri.parse(images[position].filePath))
//        Log.i("Snath ", "Images1 "+images.get(position).filePath)
        Picasso.get().load(images[position].filePath).into(holder.imageView)
    }

    override fun getItemCount(): Int {
        Log.i("Snath ", "Item Count "+images.size)
        return images.size;
    }
}