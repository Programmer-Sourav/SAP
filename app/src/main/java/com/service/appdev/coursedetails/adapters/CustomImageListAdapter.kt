package com.service.appdev.coursedetails.adapters

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.compose.ui.graphics.Color
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.service.appdev.coursedetails.R
import java.net.URLEncoder

class CustomImageListAdapter(private val arrayListOfImages : ArrayList<String>, private val context: Context) :
    RecyclerView.Adapter<CustomImageListAdapter.ImageVH>() {


    inner class ImageVH(itemView: View) : ViewHolder(itemView){
        val prospectImage = itemView.findViewById<WebView>(R.id.brochureImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageVH {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.brochure_list_item, parent, false)
        return ImageVH(itemView);
    }

    override fun getItemCount(): Int {
        return arrayListOfImages.size;
    }

    override fun onBindViewHolder(holder: ImageVH, position: Int) {
        val currentItem = arrayListOfImages[position];
        //holder.prospectImage.setImageURI(Uri.parse(currentItem))
       //Picasso.get().load(currentItem).into(holder.prospectImage);
        //holder.prospectImage.webViewClient = WebViewClient()
       // val encodedUrl = URLEncoder.encode(currentItem, "UTF-8")
        holder.prospectImage.settings.javaScriptEnabled = true
//        holder.prospectImage.settings.allowFileAccess = true
//        holder.prospectImage.settings.builtInZoomControls = true
//        holder.prospectImage.settings.displayZoomControls = false
//        holder.prospectImage.scrollBarStyle = WebView.SCROLLBARS_INSIDE_OVERLAY
        //holder.prospectImage.setBackgroundColor(android.graphics.Color.TRANSPARENT);
        val urlToLoad = "https://docs.google.com/gview?embedded=true&url=$currentItem";
        holder.prospectImage.loadUrl(urlToLoad);
    }
}