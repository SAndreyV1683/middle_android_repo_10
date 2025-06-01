package ru.yandex.buggyweatherapp.ui

import ru.yandex.buggyweatherapp.weather.domain.models.WeatherData

sealed interface WeatherScreenState {
    data class Content(
        val cityName: String = "",
        val weatherData: WeatherData
    ): WeatherScreenState
    data object Loading : WeatherScreenState
    data class Error(val errorMessage: String): WeatherScreenState
}