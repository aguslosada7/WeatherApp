package weatherapp.data.repository

import weatherapp.data.remote.api.WeatherApi
import weatherapp.data.remote.dto.WeatherDto
import weatherapp.domain.model.Weather
import weatherapp.domain.repository.WeatherRepository

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByCity(city: String): Result<Weather> =
        runCatching { api.getWeatherByCity(city).toDomain() }

    override suspend fun getWeatherByCoords(lat: Double, lon: Double): Result<Weather> =
        runCatching { api.getWeatherByCoords(lat, lon).toDomain() }
}

fun WeatherDto.toDomain() = Weather(
    cityName = name,
    country = sys.country,
    temperature = main.temp,
    feelsLike = main.feelsLike,
    humidity = main.humidity,
    description = weather.firstOrNull()?.description ?: "",
    iconCode = weather.firstOrNull()?.icon ?: "01d",
    windSpeed = wind.speed,
    tempMin = main.tempMin,
    tempMax = main.tempMax
)