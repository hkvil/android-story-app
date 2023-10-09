package com.example.dicodingstoryapp.data.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.dicodingstoryapp.data.database.UserPreference
import kotlinx.coroutines.launch

class PreferencesViewModel(private val pref: UserPreference) : ViewModel() {
    fun getUserToken(): LiveData<String> {
        return pref.getUserToken().asLiveData()
    }

    fun saveUserToken(token: String) {
        viewModelScope.launch {
            pref.saveUserToken(token)
        }
    }

    fun deleteUserToken() {
        viewModelScope.launch {
            pref.deleteToken()
        }
    }
}