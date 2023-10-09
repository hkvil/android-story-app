package com.example.dicodingstoryapp.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingstoryapp.data.response.RegisterResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig

class RegisterViewModel : ViewModel() {
    private val _responseBody: MutableLiveData<RegisterResponse?> by lazy {
        MutableLiveData<RegisterResponse?>()
    }
    val responseBody: LiveData<RegisterResponse?> get() = _responseBody

    suspend fun postRegister(name: String, email: String, password: String) {
        val response = ApiConfig.getApiService().register(name, email, password)
        _responseBody.value = response.body()
        Log.d("VM register", response.toString())
    }
}