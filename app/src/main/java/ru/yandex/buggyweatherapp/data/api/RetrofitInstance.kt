package ru.yandex.buggyweatherapp.data.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

import ru.yandex.buggyweatherapp.BuildConfig

object RetrofitInstance {
    
    private val retrofit by lazy {
        val client = OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        ).build()
        Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val weatherApi: WeatherApiService = retrofit.create(WeatherApiService::class.java)
    
}