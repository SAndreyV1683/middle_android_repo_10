package ru.yandex.buggyweatherapp.data.api

import ru.yandex.buggyweatherapp.data.request.Request
import ru.yandex.buggyweatherapp.data.response.Response

interface NetworkClient {
    suspend fun doRequest(request: Request): Response?
}