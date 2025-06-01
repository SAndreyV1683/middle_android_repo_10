package ru.yandex.buggyweatherapp.weather.data.response

import ru.yandex.buggyweatherapp.weather.data.dto.WeatherResponse

data class WeatherDataResponse(
    val dataDto: WeatherResponse
): Response()