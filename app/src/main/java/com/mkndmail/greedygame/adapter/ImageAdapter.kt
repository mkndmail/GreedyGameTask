package com.mkndmail.greedygame.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.mkndmail.greedygame.databinding.LayoutListImagesBinding
import com.mkndmail.greedygame.network.Children
import com.mkndmail.greedygame.network.ImageData

class ImageAdapter(private val clickListener: ImageClickListner) :
    ListAdapter<Children, RecyclerView.ViewHolder>(ImageDiffCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ImageViewHolder -> {
                val children = getItem(position)
                holder.bind(clickListener, children)
            }
        }
    }

}

class ImageViewHolder private constructor(val binding: LayoutListImagesBinding) :
    RecyclerView.ViewHolder(binding.root) {

    companion object {
        fun from(parent: ViewGroup): RecyclerView.ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val binding = LayoutListImagesBinding.inflate(layoutInflater, parent, false)
            return ImageViewHolder(binding)
        }
    }

    fun bind(
        clickListener: ImageClickListner,
        children: Children
    ) {
        binding.children = children
        binding.click = clickListener
    }
}

class ImageDiffCallBack : DiffUtil.ItemCallback<Children>() {
    override fun areItemsTheSame(oldItem: Children, newItem: Children): Boolean {
        return oldItem.imageData.thumbnail == newItem.imageData.thumbnail
    }

    override fun areContentsTheSame(oldItem: Children, newItem: Children): Boolean {
        return oldItem == newItem
    }
}

class ImageClickListner(val clickListener: (imageData: ImageData) -> Unit) {
    fun onClick(imageData: ImageData) = clickListener(imageData)
}