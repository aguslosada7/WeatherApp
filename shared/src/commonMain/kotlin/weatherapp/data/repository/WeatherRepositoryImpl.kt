package weatherapp.data.repository

import weatherapp.data.remote.api.WeatherApi
import weatherapp.data.remote.dto.ForecastItemDto
import weatherapp.data.remote.dto.WeatherDto
import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.model.Weather
import weatherapp.domain.repository.WeatherRepository
import kotlinx.datetime.toLocalDateTime

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByCity(city: String): Result<Weather> =
        runCatching { api.getWeatherByCity(city).toDomain() }

    override suspend fun getWeatherByCoords(lat: Double, lon: Double): Result<Weather> =
        runCatching { api.getWeatherByCoords(lat, lon).toDomain() }

    override suspend fun getForecastByCity(city: String): Result<List<HourlyForecast>> =
        runCatching { api.getForecastByCity(city).list.map { it.toHourlyForecast() } }

    override suspend fun getForecastByCoords(lat: Double, lon: Double): Result<List<HourlyForecast>> =
        runCatching { api.getForecastByCoords(lat, lon).list.map { it.toHourlyForecast() } }
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

fun ForecastItemDto.toHourlyForecast(): HourlyForecast {
    val hour = kotlinx.datetime.Instant.fromEpochSeconds(dt)
        .toLocalDateTime(kotlinx.datetime.TimeZone.currentSystemDefault())
        .hour
    return HourlyForecast(
        hour = if (hour == 0) "00:00" else "$hour:00",
        temperature = main.temp,
        iconCode = weather.firstOrNull()?.icon ?: "01d"
    )
}