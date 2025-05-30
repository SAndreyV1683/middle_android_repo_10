package ru.yandex.buggyweatherapp.weather.domain.api

import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.weather.domain.Resource
import ru.yandex.buggyweatherapp.weather.domain.models.WeatherData

interface WeatherRepository {
    suspend fun getWeatherData(location: Location): Resource<WeatherData>
    suspend fun getWeatherByCity(cityName: String): Resource<WeatherData>
}