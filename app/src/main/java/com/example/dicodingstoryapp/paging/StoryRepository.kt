package com.example.dicodingstoryapp.paging

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.example.dicodingstoryapp.data.paging.StoryPagingSource
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.data.response.ListStoryResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig
import com.example.dicodingstoryapp.data.retrofit.ApiService
import kotlinx.coroutines.flow.Flow

class StoryRepository(private val token: String) {


    fun getStoryStream(): LiveData<PagingData<ListStoryItem>> {
        val apiService = ApiConfig.getApiService()
        return Pager(
            config = PagingConfig(10, enablePlaceholders = false),
            pagingSourceFactory = { StoryPagingSource(apiService, token) }
        ).liveData
    }
}