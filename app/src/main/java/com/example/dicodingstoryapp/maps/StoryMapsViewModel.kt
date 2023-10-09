package com.example.dicodingstoryapp.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.response.ListStoryResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig
import kotlinx.coroutines.launch

class StoryMapsViewModel : ViewModel() {
    private val _responseBody: MutableLiveData<ListStoryResponse?> by lazy {
        MutableLiveData<ListStoryResponse?>()
    }
    val responseBody: LiveData<ListStoryResponse?> get() = _responseBody

    suspend fun getStoryWithLoc(token: String) {
        viewModelScope.launch {
            val response =
                ApiConfig.getApiService().getStories(
                    authToken = "Bearer $token",
                    null, 1,
                    100
                )
            if (response.isSuccessful) {
                _responseBody.value = response.body()
            }
        }
    }
}