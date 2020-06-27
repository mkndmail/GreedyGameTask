package com.mkndmail.greedygame.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.mkndmail.greedygame.network.Api
import com.mkndmail.greedygame.network.ApiResponse
import com.mkndmail.greedygame.network.ApiService
import com.mkndmail.greedygame.network.Status
import kotlinx.coroutines.*

class MainViewModel : ViewModel() {
    private val coroutineScope = CoroutineScope(Dispatchers.IO + SupervisorJob())
    private val apiService: ApiService by lazy {
        Api.retrofitService
    }
    private val _status = MutableLiveData<Status>()
    val status: LiveData<Status>
        get() = _status
    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?>
        get() = _error

    private val _response = MutableLiveData<ApiResponse>()
    val response: LiveData<ApiResponse>
        get() = _response


    init {
        loadImagesFromReddit()
    }

    fun loadImagesFromReddit() {
        _status.value = Status.LOADING
        coroutineScope.launch {
            try {
                val apiResponse = apiService.getRedditImages()
                _response.postValue(apiResponse)
                _status.postValue(Status.SUCCESS)
                _error.postValue(null)
            } catch (e: Exception) {
                _status.postValue(Status.ERROR)
                _error.postValue(e.message)
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        coroutineScope.cancel()
    }
}