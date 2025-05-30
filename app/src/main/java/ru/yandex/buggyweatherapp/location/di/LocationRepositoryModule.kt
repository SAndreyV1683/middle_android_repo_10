package ru.yandex.buggyweatherapp.location.di

import android.content.Context
import android.location.Geocoder
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import dagger.Binds
import dagger.Module
import dagger.Provides
import ru.yandex.buggyweatherapp.location.data.api.LocationClient
import ru.yandex.buggyweatherapp.location.data.impl.LocationClientImpl
import ru.yandex.buggyweatherapp.location.domain.api.LocationRepository
import ru.yandex.buggyweatherapp.location.data.impl.LocationRepositoryImpl
import java.util.Locale

@Module(
    includes = [LocationRepositoryBindsModule::class]
)
class LocationRepositoryModule {
    @Provides
    fun provideFusedLocationProviderClient(context: Context): FusedLocationProviderClient {
        return LocationServices.getFusedLocationProviderClient(context)
    }

    @Provides
    fun provideGeocoder(context: Context): Geocoder {
        return Geocoder(context, Locale.getDefault())
    }
}

@Module
interface LocationRepositoryBindsModule {
    @Binds
    fun bindsLocationClient(locationClientImpl: LocationClientImpl): LocationClient

    @Binds
    fun bindsLocationRepository(locationRepositoryImpl: LocationRepositoryImpl): LocationRepository
}