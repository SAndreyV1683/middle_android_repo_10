package ru.yandex.buggyweatherapp.di

import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import ru.yandex.buggyweatherapp.domain.api.WeatherRepository
import ru.yandex.buggyweatherapp.presentation.WeatherViewModel

@Module
class ViewModelModule {
    @Provides
    fun provideRecordsViewModel(
        weatherRepository: WeatherRepository
    ): WeatherViewModel = WeatherViewModel(weatherRepository)
}

@Suppress("UNCHECKED_CAST")
@Composable
inline fun <reified T : ViewModel> daggerViewModel(
    key: String? = null,
    crossinline viewModelInstanceCreator: () -> T
): T =
    androidx.lifecycle.viewmodel.compose.viewModel(
        modelClass = T::class.java,
        key = key,
        factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return viewModelInstanceCreator() as T
            }
        }
    )