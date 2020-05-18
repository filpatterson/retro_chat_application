package com.example.retro_app_enhanced.common

import com.example.retro_app_enhanced.model.UserModel
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

/**
 * Interface setting communication methods with server
 */
interface ApiService {

    //  login service that allows registration if there is no such user
    @POST("/login")
    fun login(@Body body:RequestBody): Call<UserModel>

    //  send message service
    @POST("/send-message")
    fun sendMessage(@Body body:RequestBody): Call<String>

    //  getting all users from server
    @GET("/users")
    fun getUsers(): Call<List<UserModel>>
}