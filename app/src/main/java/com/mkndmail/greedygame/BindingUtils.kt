package com.mkndmail.greedygame

import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mkndmail.greedygame.adapter.ImageAdapter
import com.mkndmail.greedygame.network.Children
import com.mkndmail.greedygame.network.Status
import com.mkndmail.myimageloader.MyImageLoader

/**
 * Created by Mukund, mkndmail@gmail.com on 27, June, 2020
 */

/*Sets the data to ImageAdapter */

@BindingAdapter("imagesList", "imageAdapter")
fun RecyclerView.setImages(
    imagesList: List<Children>?,
    imageAdapter: ImageAdapter
) {
    imagesList?.let {
        imageAdapter.submitList(it)
    }
}

/*Laod images from url into guven imageview*/
@BindingAdapter("loadImage")
fun ImageView.loadImage(urlImage: String?) {
    urlImage?.let { imageUrl ->
        MyImageLoader.get(this.context).loadImageFromUrl(imageUrl, this)
    }
}

//Show the progress bar loading status based on the condition
@BindingAdapter("progressStatus")
fun ProgressBar.setStatus(status: Status) {
    when (status) {
        Status.LOADING -> this.visibility == VISIBLE
        else -> this.visibility = GONE
    }
}

