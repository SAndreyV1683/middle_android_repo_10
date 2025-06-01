package ru.yandex.buggyweatherapp.location.data.results

import ru.yandex.buggyweatherapp.location.data.dto.LocationDto

sealed interface LocationResult {
    data class Success(val data: LocationDto?): LocationResult
    data class Error(val message: String): LocationResult
}