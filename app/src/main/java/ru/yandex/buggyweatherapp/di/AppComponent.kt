package ru.yandex.buggyweatherapp.di

import android.content.Context
import dagger.BindsInstance
import dagger.Component
import ru.yandex.buggyweatherapp.presentation.WeatherViewModel
import javax.inject.Singleton

@Component(
    modules = [AppModule::class]
)
@Singleton
interface AppComponent {
    @Component.Builder
    interface Builder {
        fun build(): AppComponent

        @BindsInstance
        fun context(context: Context): Builder
    }

    fun getWeatherViewModel(): WeatherViewModel
}