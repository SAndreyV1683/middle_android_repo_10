package ru.yandex.buggyweatherapp

import android.app.Application
import android.content.Context
import ru.yandex.buggyweatherapp.di.AppComponent
import ru.yandex.buggyweatherapp.di.DaggerAppComponent
import ru.yandex.buggyweatherapp.utils.ImageLoader
import ru.yandex.buggyweatherapp.utils.LocationTracker

class WeatherApplication : Application() {
    
    lateinit var appComponent: AppComponent
    
    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .builder()
            .context(this)
            .build()
        appContext = this
        ImageLoader.initialize(this)
        LocationTracker.getInstance(this)
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is WeatherApplication -> appComponent
        else -> applicationContext.appComponent
    }