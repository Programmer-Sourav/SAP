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

class CarousalAdapter3(private val images : List<ImageData>) : RecyclerView.Adapter<CarousalAdapter3.CarousalViewHolder>() {
    class CarousalViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imageView : ImageView = itemView.findViewById<ImageView>(R.id.carousalImg);
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarousalViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.corousel_item, parent, false)
        return  CarousalViewHolder(view);
    }

    override fun onBindViewHolder(holder: CarousalViewHolder, position: Int) {
//        holder.imageView.setImageURI(Uri.parse(images[position].filePath))
        Log.i("Snath ", "Images3 " + images.get(position).filePath)
        Picasso.get().load(images[position].filePath)
            .error(android.R.drawable.stat_notify_error)
            .into(holder.imageView, object : com.squareup.picasso.Callback {
                override fun onSuccess() {
                    // Image loaded successfully
                    Log.i("Picasso", "Image loaded: ${images[position].filePath}")
                }

                override fun onError(e: Exception?) {
                    // Handle the error and print the stack trace or message
                    Log.e("Picasso", "Error loading image: ${images[position].filePath}", e)
                }
            })
    }

    override fun getItemCount(): Int {
        Log.i("Snath ", "Item Count3 "+images.size)
        return images.size;
    }
}