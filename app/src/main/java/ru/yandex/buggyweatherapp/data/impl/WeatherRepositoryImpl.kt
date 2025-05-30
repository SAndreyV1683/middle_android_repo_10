package ru.yandex.buggyweatherapp.data.impl

import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import ru.yandex.buggyweatherapp.BuildConfig
import ru.yandex.buggyweatherapp.data.api.NetworkClient
import ru.yandex.buggyweatherapp.data.converters.WeatherDataConverter
import ru.yandex.buggyweatherapp.data.dto.Location
import ru.yandex.buggyweatherapp.data.request.Request
import ru.yandex.buggyweatherapp.data.response.WeatherDataResponse
import ru.yandex.buggyweatherapp.domain.Resource
import ru.yandex.buggyweatherapp.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.domain.models.WeatherData
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


    private fun parseWeatherData(json: JsonObject, location: Location): WeatherData {

        val main = json.getAsJsonObject("main")
        val wind = json.getAsJsonObject("wind")
        val sys = json.getAsJsonObject("sys")
        val weather = json.getAsJsonArray("weather").get(0).asJsonObject
        val clouds = json.getAsJsonObject("clouds")

        return WeatherData(
            cityName = json.get("name").asString,
            country = sys.get("country").asString,
            temperature = main.get("temp").asDouble,
            feelsLike = main.get("feels_like").asDouble,
            minTemp = main.get("temp_min").asDouble,
            maxTemp = main.get("temp_max").asDouble,
            humidity = main.get("humidity").asInt,
            pressure = main.get("pressure").asInt,
            windSpeed = wind.get("speed").asDouble,
            windDirection = if (wind.has("deg")) wind.get("deg").asInt else 0,
            description = weather.get("description").asString,
            icon = weather.get("icon").asString,
            cloudiness = clouds.get("all").asInt,
            sunriseTime = sys.get("sunrise").asLong,
            sunsetTime = sys.get("sunset").asLong,
            timezone = json.get("timezone").asInt,
            timestamp = json.get("dt").asLong,
            rawApiData = json.toString(),
            rain = if (json.has("rain") && json.getAsJsonObject("rain").has("1h"))
                    json.getAsJsonObject("rain").get("1h").asDouble else null,
            snow = if (json.has("snow") && json.getAsJsonObject("snow").has("1h"))
                    json.getAsJsonObject("snow").get("1h").asDouble else null
        )
    }

    private fun extractLocationFromResponse(json: JsonObject): Location {
        val coord = json.getAsJsonObject("coord")
        val lat = coord.get("lat").asDouble
        val lon = coord.get("lon").asDouble
        val name = json.get("name").asString

        return Location(lat, lon, name)
    }

    companion object {
        const val UNITS = "metric"
    }
}