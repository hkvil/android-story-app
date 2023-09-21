package com.example.dicodingstoryapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingstoryapp.data.helper.reduceFileImage
import com.example.dicodingstoryapp.data.response.AddStoryResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel:ViewModel() {
    private val _responseBody: MutableLiveData<AddStoryResponse?> by lazy {
        MutableLiveData<AddStoryResponse?>()
    }
    val responseBody: LiveData<AddStoryResponse?> get() = _responseBody

    fun uploadStory(token:String,desc:String,photo:File){
        val token = " Bearer $token"

        var photo = reduceFileImage(photo)

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = photo.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestImageFile
        )
        val client = ApiConfig.getApiService().uploadNewStory(token,imageMultipart,description)
        client.enqueue(object: Callback<AddStoryResponse> {
            override fun onResponse(
                call: Call<AddStoryResponse>,
                response: Response<AddStoryResponse>
            ) {
                _responseBody.value = response.body()
            }

            override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {

            }

        })
    }
}