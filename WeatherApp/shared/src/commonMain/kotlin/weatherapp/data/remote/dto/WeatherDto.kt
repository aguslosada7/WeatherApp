package weatherapp.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WeatherDto(
    val name: String,
    val main: MainDto,
    val weather: List<WeatherDescriptionDto>,
    val wind: WindDto,
    val sys: SysDto
)

@Serializable
data class MainDto(
    val temp: Double,
    @SerialName("feels_like") val feelsLike: Double,
    val humidity: Int,
    @SerialName("temp_min") val tempMin: Double,
    @SerialName("temp_max") val tempMax: Double
)

@Serializable
data class WeatherDescriptionDto(
    val id: Int,
    val main: String,
    val description: String,
    val icon: String
)

@Serializable
data class WindDto(val speed: Double)

@Serializable
data class SysDto(val country: String)