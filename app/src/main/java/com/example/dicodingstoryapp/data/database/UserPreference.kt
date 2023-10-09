package com.example.dicodingstoryapp.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user")


class UserPreference private constructor(private val dataStore: DataStore<Preferences>) {
    private val TOKEN = stringPreferencesKey("user_token")

    companion object {
        @Volatile
        private var INSTANCE: UserPreference? = null

        @OptIn(InternalCoroutinesApi::class)
        fun getInstance(dataStore: DataStore<Preferences>): UserPreference {
            return INSTANCE ?: synchronized(this) {
                val instance = UserPreference(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getUserToken(): Flow<String> {
        return dataStore.data.map {
            it[TOKEN] ?: "NULL"
        }
    }

    suspend fun deleteToken() {
        dataStore.edit {
            it[TOKEN] = "NULL"
        }
    }

    suspend fun saveUserToken(token: String) {
        dataStore.edit {
            it[TOKEN] = token
        }
    }


}