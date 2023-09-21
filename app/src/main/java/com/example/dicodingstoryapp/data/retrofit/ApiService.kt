package com.example.dicodingstoryapp.data.retrofit

import com.example.dicodingstoryapp.data.response.AddStoryResponse
import com.example.dicodingstoryapp.data.response.ListStoryItem
import com.example.dicodingstoryapp.data.response.ListStoryResponse
import com.example.dicodingstoryapp.data.response.LoginResponse
import com.example.dicodingstoryapp.data.response.RegisterResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    @FormUrlEncoded
    @POST("register")
    suspend fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<RegisterResponse>

    @FormUrlEncoded
    @POST("login")
    suspend fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Response<LoginResponse>

//    @GET("stories")
//    fun getStories(@Header("Authorization") authToken: String,@Query("page") page:Int?): Call<ListStoryResponse>

   @GET("stories")
   suspend fun getStories(@Header("Authorization") authToken: String,@Query("page") page:Int?): Response<ListStoryResponse>
//@GET("stories")
//suspend fun getStories(@Header("Authorization") authToken: String,@Query("page") page:Int?): List<ListStoryItem>
    @Multipart
    @POST("stories")
    fun uploadNewStory(
        @Header("Authorization") authToken: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody
    ): Call<AddStoryResponse>

}