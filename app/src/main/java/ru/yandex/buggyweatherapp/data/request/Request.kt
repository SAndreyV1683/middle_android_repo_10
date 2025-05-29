package ru.yandex.buggyweatherapp.data.request

sealed interface Request {
    val apikey: String
    val units: String
    data class WeatherByLocation(
        val latitude: Double,
        val longitude: Double,
        val name: String? = null,
        override val apikey: String,
        override val units: String
    ) : Request

    data class WeatherByCity(
        val cityName: String,
        override val apikey: String,
        override val units: String
    ) : Request
}