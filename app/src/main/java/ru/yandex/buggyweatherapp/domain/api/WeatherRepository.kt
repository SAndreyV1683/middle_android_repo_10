package ru.yandex.buggyweatherapp.domain.api

import ru.yandex.buggyweatherapp.data.dto.Location
import ru.yandex.buggyweatherapp.domain.Resource
import ru.yandex.buggyweatherapp.domain.models.WeatherData

interface WeatherRepository {
    suspend fun getWeatherData(location: Location): Resource<WeatherData>
    suspend fun getWeatherByCity(cityName: String): Resource<WeatherData>
}