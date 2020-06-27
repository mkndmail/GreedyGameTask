package com.mkndmail.greedygame.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import com.mkndmail.greedygame.R
import com.mkndmail.greedygame.activity.MainActivity.Companion.IMAGE_DATA
import com.mkndmail.greedygame.databinding.ActivityFullImageBinding
import com.mkndmail.greedygame.network.ImageData

/**
 * Created by Mukund, mkndmail@gmail.com on 27, June, 2020
 */
class FullImageActivity : AppCompatActivity() {
    private lateinit var binding:ActivityFullImageBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityFullImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val data:ImageData?=intent.extras?.getParcelable(IMAGE_DATA)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val title=data?.author?.let {author->
            "Author $author"
        }?:R.string.app_name
        supportActionBar?.title=title.toString()
        data?.let {
            binding.imageData=data
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId==android.R.id.home) super.onBackPressed()
        return super.onOptionsItemSelected(item)
    }
}