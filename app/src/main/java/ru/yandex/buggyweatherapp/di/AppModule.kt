package ru.yandex.buggyweatherapp.di

import dagger.Module

@Module(
    includes = [
        WeatherRepositoryModule::class,
        ViewModelModule::class
    ]
)
class AppModule