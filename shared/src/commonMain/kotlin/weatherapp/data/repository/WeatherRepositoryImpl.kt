@file:Suppress("DEPRECATION")

package weatherapp.data.repository

import weatherapp.data.api.WeatherApi
import weatherapp.data.api.ForecastItemDto
import weatherapp.data.api.WeatherDto
import weatherapp.domain.model.DailyForecast
import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.WeatherRepository
import kotlin.time.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.datetime.DayOfWeek

class WeatherRepositoryImpl(
    private val api: WeatherApi
) : WeatherRepository {

    override suspend fun getWeatherByCity(city: String): Result<Weather> =
        runCatching { api.getWeatherByCity(city).toDomain() }

    override suspend fun getWeatherByCoords(lat: Double, lon: Double): Result<Weather> =
        runCatching { api.getWeatherByCoords(lat, lon).toDomain() }

    override suspend fun getForecastByCity(city: String): Result<List<HourlyForecast>> =
        runCatching {
            val now = Clock.System.now().epochSeconds
            val in24hs = now + 86400
            api.getForecastByCity(city).list
                .filter { it.dt in now..in24hs }
                .map { it.toHourlyForecast() }
        }

    override suspend fun getForecastByCoords(lat: Double, lon: Double): Result<List<HourlyForecast>> =
        runCatching {
            val now = Clock.System.now().epochSeconds
            val in24hs = now + 86400
            api.getForecastByCoords(lat, lon).list
                .filter { it.dt in now..in24hs }
                .map { it.toHourlyForecast() }
        }

    override suspend fun getDailyForecastByCity(city: String): Result<List<DailyForecast>> =
        runCatching { api.getForecastByCity(city).list.toDailyForecasts() }

    override suspend fun getDailyForecastByCoords(lat: Double, lon: Double): Result<List<DailyForecast>> =
        runCatching { api.getForecastByCoords(lat, lon).list.toDailyForecasts() }
}

fun List<ForecastItemDto>.toDailyForecasts(): List<DailyForecast> {
    val tz = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(tz).date

    return this
        .groupBy {
            Instant.fromEpochSeconds(it.dt).toLocalDateTime(tz).date
        }
        .entries
        .sortedBy { it.key }
        .take(5)
        .map { (date, items) ->
            val isToday = date == today
            val dayName = when {
                isToday -> "Hoy"
                else -> when (date.dayOfWeek) {
                    DayOfWeek.MONDAY -> "Lunes"
                    DayOfWeek.TUESDAY -> "Martes"
                    DayOfWeek.WEDNESDAY -> "Miércoles"
                    DayOfWeek.THURSDAY -> "Jueves"
                    DayOfWeek.FRIDAY -> "Viernes"
                    DayOfWeek.SATURDAY -> "Sábado"
                    DayOfWeek.SUNDAY -> "Domingo"
                }
            }
            DailyForecast(
                dayName = dayName,
                tempMin = items.minOf { it.main.temp },
                tempMax = items.maxOf { it.main.temp },
                iconCode = items.firstOrNull {
                    val h = Instant.fromEpochSeconds(it.dt).toLocalDateTime(tz).hour
                    h in 11..14
                }?.weather?.firstOrNull()?.icon
                    ?: items.first().weather.firstOrNull()?.icon
                    ?: "01d",
                rainProbability = (items.maxOf { it.pop } * 100).toInt()
            )
        }
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
    val hour = Instant.fromEpochSeconds(dt)
        .toLocalDateTime(TimeZone.currentSystemDefault())
        .hour
    return HourlyForecast(
        hour = if (hour == 0) "00:00" else "$hour:00",
        temperature = main.temp,
        iconCode = weather.firstOrNull()?.icon ?: "01d"
    )
}