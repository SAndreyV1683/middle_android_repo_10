package ru.yandex.buggyweatherapp.location.data.dto

data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val name: String? = null
) {
    override fun toString(): String {
        var result = ""
        result += "Latitude: $latitude, "
        result += "Longitude: $longitude"
        name?.let {
            result += ", Name: $it"
        }
        return result
    }

    override fun equals(other: Any?): Boolean {
        if (other !is LocationDto) return false
        return latitude == other.latitude && longitude == other.longitude
    }

    override fun hashCode(): Int {
        var result = latitude.hashCode()
        result = 31 * result + longitude.hashCode()
        result = 31 * result + (name?.hashCode() ?: 0)
        return result
    }
}
