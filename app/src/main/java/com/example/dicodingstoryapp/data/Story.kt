package com.example.dicodingstoryapp.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Story(
    val title: String,
    val desc: String,
    val imageUrl: String
) : Parcelable