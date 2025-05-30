package ru.yandex.buggyweatherapp.data.impl

import ru.yandex.buggyweatherapp.data.api.NetworkClient
import ru.yandex.buggyweatherapp.data.api.WeatherApiService
import ru.yandex.buggyweatherapp.data.request.Request
import ru.yandex.buggyweatherapp.data.response.Response
import ru.yandex.buggyweatherapp.data.response.WeatherDataResponse
import javax.inject.Inject

class NetworkClientImpl @Inject constructor(
    val apiService: WeatherApiService,
) : NetworkClient {

    override suspend fun doRequest(request: Request): Response? {
        return try {
            when (request) {
                is Request.WeatherByLocation -> {
                    val result = apiService.getCurrentWeather(
                        latitude = request.latitude,
                        longitude = request.longitude,
                        apiKey = request.apikey,
                        units = request.units
                    )
                    if (result.isSuccessful) {
                        val response = WeatherDataResponse(result.body()!!)
                        response.apply { this.resultCode = result.code() }
                    } else {
                        Response().apply {
                            resultCode = result.code()
                            errorMessage = result.errorBody()?.string().toString()
                        }
                    }
                }

                is Request.WeatherByCity -> {
                    val result = apiService.getWeatherByCity(
                        cityName = request.cityName,
                        apiKey = request.apikey,
                        units = request.units
                    )
                    if (result.isSuccessful) {
                        val response = WeatherDataResponse(result.body()!!)
                        response.apply { this.resultCode = result.code() }
                    } else {
                        result.errorBody()?.string()
                        Response().apply {
                            resultCode = result.code()
                            errorMessage = result.errorBody()?.string().toString()
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Response().apply {
                errorMessage = e.message.toString()
            }
        }
    }
}