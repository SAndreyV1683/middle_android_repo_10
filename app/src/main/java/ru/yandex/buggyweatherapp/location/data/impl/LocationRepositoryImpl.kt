package ru.yandex.buggyweatherapp.location.data.impl

import kotlinx.coroutines.flow.first
import ru.yandex.buggyweatherapp.location.data.api.LocationClient
import ru.yandex.buggyweatherapp.location.data.converter.LocationConverter
import ru.yandex.buggyweatherapp.location.data.results.CityNameResult
import ru.yandex.buggyweatherapp.location.data.results.LocationResult
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.weather.domain.Resource
import javax.inject.Inject

class LocationRepositoryImpl @Inject constructor(
    private val locationClient: LocationClient,
    private val locationConverter: LocationConverter
) : LocationRepository {
    override suspend fun getCurrentLocation(): Resource<Location> {
        val result = locationClient.getCurrentLocation().first()
        return when (result) {
            is LocationResult.Success -> {
                result.data?.let {
                    Resource.Success(locationConverter.convertLocationDto(result.data))
                } ?: Resource.Error("N/A", null)
            }

            is LocationResult.Error -> {
                Resource.Error(result.message, null)
            }
        }
    }


    override suspend fun getCityNameFromLocation(location: Location): Resource<String?> {
        val result = locationClient.getCityNameFromLocation(
            locationConverter.convertLocation(location)
        )
        return when (result) {
            is CityNameResult.Success -> {
                Resource.Success(result.name)
            }

            is CityNameResult.Error -> {
                Resource.Error(result.message)
            }
        }
    }


    fun startLocationTracking() {
        //LocationTracker.getInstance(context).startTracking()
    }


}