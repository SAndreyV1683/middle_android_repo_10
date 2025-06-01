package ru.yandex.buggyweatherapp

import android.app.Application
import android.content.Context
import ru.yandex.buggyweatherapp.di.AppComponent
import ru.yandex.buggyweatherapp.di.DaggerAppComponent

class WeatherApplication : Application() {
    
    lateinit var appComponent: AppComponent
    
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is WeatherApplication -> appComponent
        else -> applicationContext.appComponent
    }