package ru.yandex.buggyweatherapp.location.data.converter

import ru.yandex.buggyweatherapp.location.data.dto.LocationDto
import ru.yandex.buggyweatherapp.location.domain.models.Location
import javax.inject.Inject

class LocationConverter @Inject constructor() {
    fun convertLocationDto(locationDto: LocationDto): Location {
        return Location(
            latitude = locationDto.latitude,
            longitude = locationDto.longitude,
            name = locationDto.name
        )
    }

    fun convertLocation(location: Location): LocationDto {
        return LocationDto(
            latitude = location.latitude,
            longitude = location.longitude,
            name = location.name
        )
    }
}