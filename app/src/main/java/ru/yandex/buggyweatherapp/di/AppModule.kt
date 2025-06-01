package ru.yandex.buggyweatherapp.di

import dagger.Module
import ru.yandex.buggyweatherapp.location.di.LocationRepositoryModule
import ru.yandex.buggyweatherapp.weather.di.WeatherRepositoryModule

@Module(
    includes = [
        WeatherRepositoryModule::class,
        ViewModelModule::class,
        CoroutineDispatchersModule::class,
        LocationRepositoryModule::class
    ]
)
class AppModule