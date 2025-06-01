package ru.yandex.buggyweatherapp.location.data.impl

import android.location.Geocoder
import android.os.Build
import android.os.Looper
import android.util.Log
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.first
import ru.yandex.buggyweatherapp.location.data.api.LocationClient
import ru.yandex.buggyweatherapp.location.data.dto.LocationDto
import ru.yandex.buggyweatherapp.location.data.results.CityNameResult
import ru.yandex.buggyweatherapp.location.data.results.LocationResult
import javax.inject.Inject

class LocationClientImpl @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient,
    private val geoCoder: Geocoder
): LocationClient {

    private fun requestLocationUpdates(): Flow<LocationResult> {
        return callbackFlow {
            try {
                val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, INTERVAL)
                    .setWaitForAccurateLocation(false)
                    .setMinUpdateIntervalMillis(UPDATE_INTERVAL)
                    .build()

                val locationCallback = object : LocationCallback() {
                    override fun onLocationResult(
                        locationResult: com.google.android.gms.location.LocationResult
                    ) {
                        val result = locationResult.lastLocation?.let { location ->
                            val userLocation = LocationDto(
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                            LocationResult.Success(userLocation)
                        } ?: LocationResult.Success(null)
                        trySend(result)
                    }
                }
                fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
                )
            } catch (e: SecurityException) {
                Log.e("LocationRepository", "Location permission not granted", e)
                LocationResult.Error(e.message.toString())
            }
        }
    }

    override fun getCurrentLocation(): Flow<LocationResult> {
        return callbackFlow {
            try {
                fusedLocationClient.lastLocation
                    .addOnSuccessListener { location ->
                        if (location != null) {
                            val location = LocationDto(
                                latitude = location.latitude,
                                longitude = location.longitude
                            )
                            trySend(
                                LocationResult.Success(location)
                            )
                        } else {
                            requestLocationUpdates()
                        }
                    }
                    .addOnFailureListener { e ->
                        Log.e("LocationRepository", "Error getting location", e)
                        trySend(
                            LocationResult.Error(e.message.toString())
                        )
                    }
            } catch (e: SecurityException) {
                LocationResult.Error(e.message.toString())
            }
            awaitClose()
        }
    }

    override suspend fun getCityNameFromLocation(location: LocationDto): CityNameResult {
        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                callbackFlow<CityNameResult> {
                    geoCoder.getFromLocation(location.latitude, location.longitude, 1) { addresses ->
                        if (addresses.isNotEmpty()) {
                            val address = addresses[0]
                            val cityName = when {
                                address.locality != null -> address.locality
                                address.subAdminArea != null -> address.subAdminArea
                                else -> address.adminArea
                            }
                            trySend(CityNameResult.Success(cityName))
                        } else {
                            trySend(CityNameResult.Success(null))
                        }
                    }
                    awaitClose()
                }.first()
            } else {
                @Suppress("DEPRECATION")
                val addresses = geoCoder.getFromLocation(location.latitude, location.longitude, 1)
                if (addresses?.isNotEmpty() == true) {
                    val address = addresses[0]
                    val cityName = when {
                        address.locality != null -> address.locality
                        address.subAdminArea != null -> address.subAdminArea
                        else -> address.adminArea
                    }
                    CityNameResult.Success(cityName)
                } else {
                    CityNameResult.Success(null)
                }
            }
        } catch (e: Exception) {
            CityNameResult.Error(e.message.toString())
        }
    }

    companion object {
        const val INTERVAL = 10000L
        const val UPDATE_INTERVAL = 5000L
    }
}