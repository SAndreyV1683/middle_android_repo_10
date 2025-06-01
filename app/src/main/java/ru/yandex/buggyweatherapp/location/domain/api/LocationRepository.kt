package ru.yandex.buggyweatherapp.location.domain.api

import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.weather.domain.Resource

interface LocationRepository {
    suspend fun getCurrentLocation(): Resource<Location>
    suspend fun getCityNameFromLocation(location: Location): Resource<String?>
}