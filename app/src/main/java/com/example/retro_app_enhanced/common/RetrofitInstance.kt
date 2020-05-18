package com.example.retro_app_enhanced.common

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 *  Special retrofit instance that is created for establishing connection to the server. Only one
 * connection instance is available for application
 */
class RetrofitInstance {

    companion object {
        val retrofit: ApiService by lazy {
            //  form http client for connection to server
            val httpClient = OkHttpClient.Builder()

            //  set server's url, converters for accepting and sending messages
            val builder = Retrofit.Builder()
                .baseUrl("http://10.0.2.2:5000/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())

            //  append all elements to the retrofit and start work
            val retrofit = builder
                .client(httpClient.build())
                .build()

            retrofit.create(ApiService::class.java)
        }
    }
}