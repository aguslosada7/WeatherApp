package weatherapp.data

import weatherapp.domain.model.DailyForecast
import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.model.Weather

data class CachedWeatherData(
    val weather: Weather,
    val hourlyForecast: List<HourlyForecast>,
    val dailyForecast: List<DailyForecast>
)

/**
 * Cache en memoria simple, por clave (nombre de ciudad o "current_location").
 * Vive durante toda la sesión de la app y se pierde al cerrarla.
 */
object WeatherCache {
    private val cache = mutableMapOf<String, CachedWeatherData>()

    fun get(key: String): CachedWeatherData? = cache[key]

    fun put(key: String, data: CachedWeatherData) {
        cache[key] = data
    }
}