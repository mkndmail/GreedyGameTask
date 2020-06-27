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

@BindingAdapter("imagesList", "imageAdapter")
fun RecyclerView.setImages(
    imagesList: List<Children>?,
    imageAdapter: ImageAdapter
) {
    imagesList?.let {
        imageAdapter.submitList(it)
    }
}

@BindingAdapter("loadImage")
fun ImageView.loadImage(urlImage: String?) {
    urlImage?.let { imageUrl ->
        MyImageLoader.get(this.context).loadImageFromUrl(imageUrl, this)
    }
}

@BindingAdapter("progressStatus")
fun ProgressBar.setStatus(status: Status) {
    when (status) {
        Status.LOADING -> this.visibility == VISIBLE
        else -> this.visibility = GONE
    }
}

