package ru.yandex.buggyweatherapp.location.data.results

sealed interface CityNameResult {
    data class Success(val name: String?) : CityNameResult
    data class Error(val message: String) : CityNameResult
}