package ru.yandex.buggyweatherapp.weather.data.impl

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.weather.data.api.NetworkClient
import ru.yandex.buggyweatherapp.weather.data.converters.WeatherDataConverter
import ru.yandex.buggyweatherapp.weather.data.request.Request
import ru.yandex.buggyweatherapp.weather.data.response.WeatherDataResponse
import ru.yandex.buggyweatherapp.weather.domain.Resource
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.weather.domain.models.WeatherData
import javax.inject.Inject

class WeatherRepositoryImpl @Inject constructor(
    private val networkClient: NetworkClient,
    private val converter: WeatherDataConverter,
    private val ioDispatcher: CoroutineDispatcher
): WeatherRepository {

    override suspend fun getWeatherData(location: Location): Resource<WeatherData> {
        return withContext(ioDispatcher) {
            val response = networkClient.doRequest(
                Request.WeatherByLocation(
                    latitude = location.latitude,
                    longitude = location.longitude,
                    apikey = BuildConfig.API_KEY,
                    units = UNITS
                )
            )

            when(response?.resultCode) {
                200 -> {
                    Resource.Success(
                        converter.convertWeatherDataDto(
                            (response as WeatherDataResponse).dataDto
                        )
                    )
                }

                else -> {
                    Resource.Error(response?.errorMessage.toString(), null)
                }
            }
        }
    }

    override suspend fun getWeatherByCity(cityName: String): Resource<WeatherData> {
        return withContext(ioDispatcher) {
            val response = networkClient.doRequest(
                Request.WeatherByCity(
                    cityName = cityName,
                    apikey = BuildConfig.API_KEY,
                    units = UNITS
                )
            )

            when(response?.resultCode) {
                200 -> {
                    Resource.Success(converter.convertWeatherDataDto(
                        (response as WeatherDataResponse).dataDto)
                    )
                }

                else -> {
                    Resource.Error(response?.errorMessage.toString(), null)
                }
            }
        }
    }

    companion object {
        const val UNITS = "metric"
    }
}