package ru.yandex.buggyweatherapp.weather.data.api

import ru.yandex.buggyweatherapp.weather.data.request.Request
import ru.yandex.buggyweatherapp.weather.data.response.Response

interface NetworkClient {
    suspend fun doRequest(request: Request): Response?
}