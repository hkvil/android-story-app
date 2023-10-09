package com.example.dicodingstoryapp.data.viewmodel

import android.location.Location
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingstoryapp.data.helper.reduceFileImage
import com.example.dicodingstoryapp.data.response.AddStoryResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class AddStoryViewModel : ViewModel() {
    private val _responseBody: MutableLiveData<AddStoryResponse?> by lazy {
        MutableLiveData<AddStoryResponse?>()
    }
    val responseBody: LiveData<AddStoryResponse?> get() = _responseBody

    fun uploadStory(token: String, desc: String, photo: File, location: Location?) {
        val token = " Bearer $token"
        var photo = reduceFileImage(photo)
        Log.d(
            "LOCATION AT VIEWMODEL lon",
            "${location?.latitude.toString()},${location?.longitude.toString()}"
        )
        var lat:RequestBody? = null
        var lon:RequestBody? = null
        if (location!=null){
            val latString = location?.latitude.toString()
            val lonString = location?.longitude.toString()
            lat = latString.toRequestBody("text/plain".toMediaType())
            lon = lonString.toRequestBody("text/plain".toMediaType())
        }

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImageFile = photo.asRequestBody("image/jpeg".toMediaType())
        val imageMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo",
            photo.name,
            requestImageFile
        )
        val client =
            ApiConfig.getApiService().uploadNewStory(token, imageMultipart, description, lat, lon)
        client.enqueue(object : Callback<AddStoryResponse> {
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