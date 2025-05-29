package ru.yandex.buggyweatherapp.data.response

import ru.yandex.buggyweatherapp.data.dto.WeatherResponse

data class WeatherDataResponse(
    val dataDto: WeatherResponse
): Response()