package ru.yandex.buggyweatherapp.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import coil.request.ImageRequest
import ru.yandex.buggyweatherapp.R
import ru.yandex.buggyweatherapp.presentation.WeatherViewModel
import ru.yandex.buggyweatherapp.ui.WeatherScreenState
import ru.yandex.buggyweatherapp.utils.WeatherIconMapper
import ru.yandex.buggyweatherapp.weather.domain.models.WeatherData

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeatherScreen(viewModel: WeatherViewModel, modifier: Modifier = Modifier) {
    
    val screenState = remember { viewModel.screenState }
    var searchText = remember { mutableStateOf("") }
    val context = LocalContext.current
    val locationPermissionRequest = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true -> {
                viewModel.fetchCurrentLocationWeather()
            }
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true -> {
                viewModel.fetchCurrentLocationWeather()
            }
            else -> {
                viewModel.setScreenState(
                    WeatherScreenState.Error(context.getString(R.string.no_location_permission))
                )
            }
        }
    }

    LaunchedEffect(Unit) {
        val hasFineLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        if (!hasFineLocation && !hasCoarseLocation) {
            locationPermissionRequest.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        } else {
            viewModel.fetchCurrentLocationWeather()
        }
    }
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        OutlinedTextField(
            value = searchText.value,
            onValueChange = { searchText.value = it },
            label = { Text(text = stringResource(R.string.search_city)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.searchWeatherByCity(searchText.value)
                }) {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = { 
                viewModel.searchWeatherByCity(searchText.value)
            })
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        when(screenState.value) {
            is WeatherScreenState.Content -> {
                val content = screenState.value as WeatherScreenState.Content
                WeatherCard(
                    weatherData = content.weatherData,
                    cityName = content.cityName,
                )
            }

            is WeatherScreenState.Error -> {
                Text(
                    text = (screenState.value as WeatherScreenState.Error).errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }

            is WeatherScreenState.Loading -> {
                Text(text = stringResource(R.string.loading))
            }
        }
    }
}

@Composable
fun WeatherCard(
    weatherData: WeatherData,
    cityName: String?,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Заголовок
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = cityName ?: weatherData.cityName,
                    style = MaterialTheme.typography.headlineMedium
                )
            }

            // Иконка погоды и температура
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(WeatherIconMapper.getIconUrl(weatherData.icon))
                        .crossfade(true)
                        .build(),
                    contentDescription = weatherData.description,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.size(80.dp)
                )

                Text(
                    text = stringResource(R.string.temperature, weatherData.temperature.toInt()),
                    style = MaterialTheme.typography.headlineLarge
                )
            }

            Text(
                text = stringResource(
                    R.string.description,
                    weatherData.description.replaceFirstChar { it.uppercase() }
                ),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Детальная информация
            LazyColumn {
                // Создаем предварительно форматированные строки
                val feelsLikeLabel = context.getString(R.string.feels_like)
                val feelsLikeValue = context.getString(R.string.temperature, weatherData.feelsLike.toInt())
                val minMaxTemp = context.getString(R.string.min_max_temp, weatherData.minTemp.toInt(), weatherData.maxTemp.toInt())
                val humidity = context.getString(R.string.humidity, weatherData.humidity)
                val pressure = context.getString(R.string.pressure, weatherData.pressure)
                val wind = context.getString(R.string.wind, weatherData.windSpeed)
                val sunrise = context.getString(R.string.sunrise, WeatherIconMapper.formatTimestamp(weatherData.sunriseTime))
                val sunset = context.getString(R.string.sunset, WeatherIconMapper.formatTimestamp(weatherData.sunsetTime))

                val list = listOf(
                    feelsLikeLabel to feelsLikeValue,
                    minMaxTemp to "",
                    humidity to "",
                    pressure to "",
                    wind to "",
                    sunrise to "",
                    sunset to ""
                ).filter { it.first.isNotEmpty() }

                items(list) { (label, value) ->
                    if (value.isEmpty()) {
                        WeatherDataSingleRow(label)
                    } else {
                        WeatherDataRow(label, value)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
private fun WeatherDataRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun WeatherDataSingleRow(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.bodyMedium)
    }
}