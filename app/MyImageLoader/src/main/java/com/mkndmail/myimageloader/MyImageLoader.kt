package com.mkndmail.myimageloader

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Process
import android.util.LruCache
import android.widget.ImageView
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.net.URL
import java.util.*
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.ThreadFactory

/**
 * Created by Mukund, mkndmail@gmail.com on 27, June, 2020
 */

class MyImageLoader(context: Context) {
    private val maxCacheSize: Int = (Runtime.getRuntime().maxMemory() / 1024).toInt() / 8
    private val executorService: ExecutorService
    private val handler: Handler by lazy {
        Handler()
    }
    private val memoryCache: LruCache<String, Bitmap> by lazy {
        object : LruCache<String, Bitmap>(maxCacheSize) {
            override fun sizeOf(key: String, bitmap: Bitmap): Int {
                // The cache size will be measured in kilobytes rather than number of items.
                return bitmap.byteCount / 1024
            }
        }
    }

    private inner class ImageThreadFactory : ThreadFactory {
        override fun newThread(runnable: Runnable): Thread {
            return Thread(runnable).apply {
                name = BuildConfig.LIBRARY_PACKAGE_NAME
                priority = Process.THREAD_PRIORITY_BACKGROUND
            }
        }
    }

    init {
        executorService = Executors.newFixedThreadPool(5, ImageThreadFactory())
        val metrics = context.resources.displayMetrics
        screenWidth = metrics.widthPixels
        screenHeight = metrics.heightPixels
    }


    private val imageViewMap = Collections.synchronizedMap(WeakHashMap<ImageView, String>())
    fun loadImageFromUrl(imageUrl: String?, imageView: ImageView?) {

        require(imageView != null) {
            "ImageLoader:load - ImageView should not be null."
        }

        require(imageUrl != null && imageUrl.isNotEmpty()) {
            "ImageLoader:load - Image Url should not be empty"
        }
        imageView.setImageResource(0)
        imageViewMap[imageView] = imageUrl

        val bitmap = isImageInCache(imageUrl)
        bitmap?.let {
            drawaImageOnView(imageView, it, imageUrl)
        } ?: run {
            executorService.submit(PhotosLoaderTask(ImageRequest(imageUrl, imageView)))
        }
    }

    private sealed class ProcessImage {
        //Todo define image download process here
    }

    private fun drawaImageOnView(imageView: ImageView, bitmap: Bitmap?, imageUrl: String) {
        val scaledBitmap = scaleBitmapForLoad(bitmap, imageView.width, imageView.height)

        scaledBitmap?.let {
            if (!isImageViewInUse(ImageRequest(imageUrl, imageView))) imageView.setImageBitmap(
                scaledBitmap
            )
        }
    }

    inner class ImageRequest(var imgUrl: String, var imageView: ImageView)

    private fun isImageViewInUse(imageRequest: ImageRequest): Boolean {
        val tag = imageViewMap[imageRequest.imageView]
        return tag == null || tag != imageRequest.imgUrl
    }

    private fun scaleBitmapForLoad(bitmap: Bitmap?, width: Int, height: Int): Bitmap? {

        if (width == 0 || height == 0) return bitmap

        val stream = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, stream)
        val inputStream = BufferedInputStream(ByteArrayInputStream(stream.toByteArray()))

        // Scale Bitmap to required ImageView Size
        return scaleBitmap(inputStream, width, height)
    }

    private fun scaleBitmap(inputStream: BufferedInputStream, width: Int, height: Int): Bitmap? {
        return BitmapFactory.Options().run {
            inputStream.mark(inputStream.available())

            inJustDecodeBounds = true
            BitmapFactory.decodeStream(inputStream, null, this)

            inSampleSize = calculateInSampleSize(this, width, height)

            inJustDecodeBounds = false
            inputStream.reset()
            BitmapFactory.decodeStream(inputStream, null, this)
        }
    }

    private fun calculateInSampleSize(
        options: BitmapFactory.Options,
        reqWidth: Int,
        reqHeight: Int
    ): Int {

        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2
            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) inSampleSize *= 2
        }

        return inSampleSize
    }

    inner class PhotosLoaderTask(private var imageRequest: ImageRequest) : Runnable {

        override fun run() {

            if (isImageViewInUse(imageRequest)) return

            val bitmap = downloadBitmapFromURL(imageRequest.imgUrl)
            memoryCache.put(imageRequest.imgUrl, bitmap)

            if (isImageViewInUse(imageRequest)) return

            val displayBitmap = ShowBitmap(imageRequest)
            handler.post(displayBitmap)
        }


    }

    inner class ShowBitmap(private var imageRequest: ImageRequest) : Runnable {
        override fun run() {
            if (!isImageViewInUse(imageRequest)) drawaImageOnView(
                imageRequest.imageView,
                isImageInCache(imageRequest.imgUrl),
                imageRequest.imgUrl
            )
        }
    }

    private fun downloadBitmapFromURL(imageUrl: String): Bitmap? {
        val url = URL(imageUrl)
        val inputStream = BufferedInputStream(url.openConnection().getInputStream())

        // Scale Bitmap to Screen Size to store in Cache
        return scaleBitmap(inputStream, screenWidth, screenHeight)
    }

    private fun isImageInCache(imageUrl: String): Bitmap? = memoryCache.get(imageUrl)

    companion object {

        private lateinit var INSTANCE: MyImageLoader
        internal var screenWidth = 0
        internal var screenHeight = 0

        @Synchronized
        fun get(context: Context): MyImageLoader {
            return if (::INSTANCE.isInitialized) {
                INSTANCE
            } else {
                INSTANCE = MyImageLoader(context)
                INSTANCE
            }
        }

    }
}