package ru.yandex.buggyweatherapp.presentation

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.location.domain.models.Location
import ru.yandex.buggyweatherapp.ui.WeatherScreenState
import ru.yandex.buggyweatherapp.weather.domain.Resource
import ru.yandex.buggyweatherapp.weather.domain.api.WeatherRepository

class WeatherViewModel(
    private val weatherRepository: WeatherRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {

    var cityName = ""
    var currentLocation: Location? = null
    val screenState = mutableStateOf<WeatherScreenState>(WeatherScreenState.Loading)
    private var refreshJob: Job? = null

    init {
        startAutoRefresh()
    }

    fun setScreenState(state: WeatherScreenState) {
        screenState.value = state
    }
    
    fun fetchCurrentLocationWeather() {
        viewModelScope.launch {
            screenState.value = WeatherScreenState.Loading
            val resource = locationRepository.getCurrentLocation()
            when(resource) {
                is Resource.Success -> {
                    currentLocation = resource.data
                    if (currentLocation != null) {
                        val cityNameResource = locationRepository.getCityNameFromLocation(
                            currentLocation!!
                        )
                        when(cityNameResource) {
                            is Resource.Success -> {
                                val name = cityNameResource.data
                                name?.let {
                                    cityName = it
                                }
                            }

                            is Resource.Error -> {
                                val error = cityNameResource.message
                                error?.let { cityName = it }
                            }
                        }
                        getWeatherForLocation(currentLocation!!)
                    }
                }

                is Resource.Error -> {
                    resource.message?.let {
                        screenState.value = WeatherScreenState.Error(it)
                    }
                }
            }
        }
    }
    
    fun getWeatherForLocation(location: Location) {
        viewModelScope.launch {
            val resource = weatherRepository.getWeatherData(location)
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { data ->
                        screenState.value = WeatherScreenState.Content(cityName, data)
                    }
                }

                is Resource.Error -> {
                    screenState.value = WeatherScreenState.Error(
                        resource.message ?: "Unknown error"
                    )
                }
            }
        }
    }
    
    fun searchWeatherByCity(city: String) {
        if (city.isBlank()) {
            fetchCurrentLocationWeather()
            return
        }

        viewModelScope.launch {
            val resource = weatherRepository.getWeatherByCity(city)
            when (resource) {
                is Resource.Success -> {
                    resource.data?.let { data ->
                        screenState.value = WeatherScreenState.Content(data.cityName, data)
                    }
                }

                is Resource.Error -> {
                    screenState.value = WeatherScreenState.Error(
                        resource.message ?: "Unknown error"
                    )
                }
            }
        }
    }
    
    
    private fun startAutoRefresh() {
        refreshJob = viewModelScope.launch {
            while (isActive) {
                delay(REFRESH_DELAY)
                currentLocation?.let { location ->
                    getWeatherForLocation(location)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        refreshJob?.cancel()
    }

    companion object {
        const val REFRESH_DELAY = 60000L
    }
}