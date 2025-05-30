package ru.yandex.buggyweatherapp.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.weather.domain.models.WeatherData
import ru.yandex.buggyweatherapp.location.data.impl.LocationRepositoryImpl
import ru.yandex.buggyweatherapp.weather.domain.Resource
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.utils.ImageLoader
import java.util.Timer
import java.util.TimerTask

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    
    val weatherData = MutableLiveData<WeatherData>()
    val currentLocation = MutableLiveData<Location>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val cityName = MutableLiveData<String>()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var refreshTimer: Timer? = null

    init {
        fetchCurrentLocationWeather()
        startAutoRefresh()
    }
    
    fun fetchCurrentLocationWeather() {
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            val resource = locationRepository.getCurrentLocation()
            when(resource) {
                is Resource.Success -> {
                    val location = resource.data
                    if (location != null) {
                        currentLocation.value = location
                        val cityNameResource = locationRepository.getCityNameFromLocation(location)
                        when(cityNameResource) {
                            is Resource.Success -> {
                                val name = cityNameResource.data
                                name?.let { cityName.value = it }
                            }

                            is Resource.Error -> {
                                val error = cityNameResource.message
                                error?.let { cityName.value = it }
                            }
                        }
                        getWeatherForLocation(location)
                    }
                }

                is Resource.Error -> {
                    isLoading.value = false
                    resource.message?.let {
                        error.value = it
                    }
                }
            }
        }
    }
    
    fun getWeatherForLocation(location: Location) {
        isLoading.value = true
        error.value = null
        viewModelScope.launch {
            val resource = weatherRepository.getWeatherData(location)
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { data ->
                        weatherData.value = data
                    }
                }

                is Resource.Error -> {
                    error.value = resource.message ?: "Unknown error"
                }
            }
        }
    }
    
    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            error.value = "City name cannot be empty"
            return
        }
        
        isLoading.value = true
        error.value = null

        viewModelScope.launch {
            val resource = weatherRepository.getWeatherByCity(city)
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { data ->
                        weatherData.value = data
                        cityName.value = data.cityName
                        currentLocation.value = Location(0.0, 0.0, data.cityName)
                    }
                }

                is Resource.Error -> {
                    error.value = resource.message ?: "Unknown error"
                }
            }
        }
    }
    
    
    fun formatTemperature(temp: Double): String {
        return "${temp.toInt()}°C"
    }
    
    
    fun loadWeatherIcon(iconCode: String) {
        coroutineScope.launch {
            val iconUrl = "https://openweathermap.org/img/wn/$iconCode@2x.png"
            ImageLoader.loadImage(iconUrl)
        }
    }
    
    
    private fun startAutoRefresh() {
        refreshTimer = Timer()
        refreshTimer?.scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                currentLocation.value?.let { location ->
                    getWeatherForLocation(location)
                }
            }
        }, 60000, 60000)
    }
    
    
    fun toggleFavorite() {
        weatherData.value?.let {
            it.isFavorite = !it.isFavorite
            weatherData.value = it
        }
    }
}