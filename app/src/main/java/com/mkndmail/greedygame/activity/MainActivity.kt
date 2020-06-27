package com.mkndmail.greedygame.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.mkndmail.greedygame.adapter.ImageAdapter
import com.mkndmail.greedygame.adapter.ImageClickListner
import com.mkndmail.greedygame.databinding.ActivityMainBinding
import com.mkndmail.greedygame.viewmodel.MainViewModel
import com.mkndmail.myimageloader.MyImageLoader


/**
 * Created by Mukund, mkndmail@gmail.com on 27, June, 2020
 */
class MainActivity : AppCompatActivity() {
    private lateinit var activityMainBinding: ActivityMainBinding

    private val mainViewModel: MainViewModel by lazy {
        ViewModelProvider(this).get(MainViewModel::class.java)
    }

    companion object {
        const val IMAGE_DATA = "image_data"
    }

    private lateinit var imageAdapter: ImageAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityMainBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(activityMainBinding.root)
        imageAdapter = ImageAdapter(ImageClickListner {
            val intent = Intent(this, FullImageActivity::class.java).apply {
                putExtra(IMAGE_DATA, it)
            }
            startActivity(intent)
        })
        activityMainBinding.lifecycleOwner = this
        activityMainBinding.viewModel = mainViewModel
        activityMainBinding.imageAdapter = imageAdapter
        activityMainBinding.rvImages.adapter = imageAdapter
        mainViewModel.status.observe(this, Observer {
            activityMainBinding.status = it
        })
        mainViewModel.response.observe(this, Observer { apiResponse ->
            if (apiResponse.data.children.isNotEmpty()) {
                activityMainBinding.children = apiResponse.data.children
            }
        })
    }


}