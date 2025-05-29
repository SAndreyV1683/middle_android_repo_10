package ru.yandex.buggyweatherapp.data.converters

import ru.yandex.buggyweatherapp.data.dto.WeatherResponse
import ru.yandex.buggyweatherapp.domain.models.WeatherData

class WeatherDataConverter {
    fun convertWeatherDataDto(dataDto: WeatherResponse): WeatherData {
        return WeatherData(
            cityName = dataDto.name,
            country = dataDto.sys.country,
            temperature = dataDto.main.temp,
            feelsLike = dataDto.main.feelsLike,
            minTemp = dataDto.main.tempMin,
            maxTemp = dataDto.main.tempMin,
            humidity = dataDto.main.humidity,
            pressure = dataDto.main.pressure ,
            windSpeed = dataDto.wind.speed,
            windDirection = dataDto.wind.deg,
            description = if (dataDto.weather.isNotEmpty()) {
                dataDto.weather[0].description
            } else {
                ""
            } ,
            icon = if (dataDto.weather.isNotEmpty()) {
                dataDto.weather[0].icon
            } else {
                ""
            },
            cloudiness = dataDto.clouds.all,
            sunriseTime = dataDto.sys.sunrise,
            sunsetTime = dataDto.sys.sunset,
            timezone = dataDto.timezone,
            timestamp = dataDto.dt,
            rawApiData = "",
            rain = if (dataDto.rain != null && dataDto.rain.oneHour != null) {
                dataDto.rain.oneHour
            } else {
                null
            },
            snow = 0.0
        )
    }
}