package com.example.dicodingstoryapp.data.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.liveData
import com.example.dicodingstoryapp.data.paging.StoryPagingSource
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.data.response.ListStoryResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig
import com.example.dicodingstoryapp.data.retrofit.ApiService
import com.example.dicodingstoryapp.paging.StoryRepository
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeViewModel(storyRepository: StoryRepository) : ViewModel() {
    private val _responseBody: MutableLiveData<ListStoryResponse> by lazy {
        MutableLiveData<ListStoryResponse>()
    }


    val responseBody: LiveData<ListStoryResponse> get() = _responseBody

    suspend fun getStories(token: String) {
        viewModelScope.launch {
            val response = ApiConfig.getApiService().getStories("Bearer $token", 1)
            _responseBody.value = response.body()
        }
    }


    val story : LiveData<PagingData<ListStoryItem>> =  storyRepository.getStoryStream().cachedIn(viewModelScope)

    class HomeViewModelFactory(private val storyRepository: StoryRepository):ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if(modelClass.isAssignableFrom(HomeViewModel::class.java)){
                return HomeViewModel(storyRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }

//    fun getStories(token:String){
//        val token = " Bearer $token"
//        val client = ApiConfig.getApiService().getStories(token,null)
//
//        client.enqueue(object:Callback<ListStoryResponse>{
//            override fun onResponse(
//                call: Call<ListStoryResponse>,
//                response: Response<ListStoryResponse>
//            ) {
//                _responseBody.value = response.body()
//            }
//
//            override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
//
//            }
//
//        })
//    }
}