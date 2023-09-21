package com.example.dicodingstoryapp.data.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.dicodingstoryapp.data.response.LoginResponse
import com.example.dicodingstoryapp.data.retrofit.ApiConfig

class LoginViewModel : ViewModel() {
    private val _responseBody: MutableLiveData<LoginResponse?> by lazy {
        MutableLiveData<LoginResponse?>()
    }
    val responseBody: LiveData<LoginResponse?> get() = _responseBody


    suspend fun postLogin(email: String, password: String) {
        val response = ApiConfig.getApiService().login(email, password)
        _responseBody.value = response.body()
        Log.d("VM login", response.toString())
    }


}

