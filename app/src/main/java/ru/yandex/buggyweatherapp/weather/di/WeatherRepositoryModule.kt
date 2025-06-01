package ru.yandex.buggyweatherapp.weather.di

import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient
import ru.yandex.buggyweatherapp.weather.data.api.WeatherApiService
import ru.yandex.buggyweatherapp.weather.data.impl.NetworkClientImpl
import ru.yandex.buggyweatherapp.weather.data.impl.WeatherRepositoryImpl
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository
import javax.inject.Singleton

@Module(
    includes = [WeatherRepositoryBindsModule::class]
)
class WeatherRepositoryModule {
    @Singleton
    @Provides
    fun provideWeatherApiService(): WeatherApiService {
        val client = OkHttpClient.Builder().addInterceptor(
            HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        ).build()
        val retrofit: Retrofit = Retrofit.Builder()
            .client(client)
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        return retrofit.create(WeatherApiService::class.java)
    }
}

@Module
interface WeatherRepositoryBindsModule {
    @Singleton
    @Binds
    fun bindsNetworkClient(networkClientImpl: NetworkClientImpl): NetworkClient

    @Singleton
    @Binds
    fun bindsWeatherRepository(weatherRepositoryImpl: WeatherRepositoryImpl): WeatherRepository
}