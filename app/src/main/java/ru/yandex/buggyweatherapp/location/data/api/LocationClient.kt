package ru.yandex.buggyweatherapp.location.data.api

import kotlinx.coroutines.flow.Flow
import ru.yandex.buggyweatherapp.location.data.dto.LocationDto
import ru.yandex.buggyweatherapp.location.data.results.CityNameResult
import ru.yandex.buggyweatherapp.location.data.results.LocationResult

interface LocationClient {
    fun getCurrentLocation(): Flow<LocationResult>
    suspend fun getCityNameFromLocation(location: LocationDto): CityNameResult
}