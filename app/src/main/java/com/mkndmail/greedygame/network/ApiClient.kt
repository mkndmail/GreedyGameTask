package com.mkndmail.greedygame.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

/**
 * Created by Mukund, mkndmail@gmail.com on 27, June, 2020
 */

private const val BASE_URL = "https://www.reddit.com/r/"

enum class Status{
    LOADING,
    SUCCESS,
    ERROR
}
private val moshi: Moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

/*A retrofit client to convert network request into a Interface*/
val retrofit: Retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()


interface ApiService {

    /*A get request which returns the response from the URL*/
    @GET("images/hot.json")
    suspend fun getRedditImages(): ApiResponse
}

object Api {

    /*A retrofit service to interact with the ApiService interface*/
    val retrofitService: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }
}