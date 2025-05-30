package ru.yandex.buggyweatherapp.presentation

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import ru.yandex.buggyweatherapp.data.dto.Location
import ru.yandex.buggyweatherapp.domain.models.WeatherData
import ru.yandex.buggyweatherapp.data.impl.LocationRepositoryImpl
import ru.yandex.buggyweatherapp.data.impl.WeatherRepositoryImpl
import ru.yandex.buggyweatherapp.domain.Resource
import ru.yandex.buggyweatherapp.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.utils.ImageLoader
import java.util.Timer
import java.util.TimerTask

class WeatherViewModel(
    private val weatherRepository: WeatherRepository
) : ViewModel() {
    
    
    private lateinit var activityContext: Context
    
    

    private val locationRepository by lazy { 
        LocationRepositoryImpl(activityContext)
    }
    
    
    val weatherData = MutableLiveData<WeatherData>()
    val currentLocation = MutableLiveData<Location>()
    val isLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()
    val cityName = MutableLiveData<String>()
    
    
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    
    
    private var refreshTimer: Timer? = null
    
    
    fun initialize(context: Context) {
        this.activityContext = context
        fetchCurrentLocationWeather()
        
        
        startAutoRefresh()
    }
    
    
    fun fetchCurrentLocationWeather() {
        isLoading.value = true
        error.value = null
        
        locationRepository.getCurrentLocation { location ->
            if (location != null) {
                currentLocation.value = location
                
                
                val cityNameFromLocation = locationRepository.getCityNameFromLocation(location)
                cityName.value = cityNameFromLocation
                
                getWeatherForLocation(location)
            } else {
                isLoading.value = false
                error.value = "Unable to get current location"
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
        /*weatherRepository.getWeatherData(location) { data, exception ->

            Handler(Looper.getMainLooper()).post {
                isLoading.value = false

                if (data != null) {
                    weatherData.value = data
                } else {
                    error.value = exception?.message ?: "Unknown error"
                }
            }
        }*/
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
        
        /*weatherRepository.getWeatherByCity(city) { data, exception ->

            isLoading.value = false

            if (data != null) {
                weatherData.value = data
                cityName.value = data.cityName
                currentLocation.value = Location(0.0, 0.0, data.cityName)
            } else {
                error.value = exception?.message ?: "Unknown error"
            }
        }*/
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
    
    override fun onCleared() {
        super.onCleared()
        
    }
}