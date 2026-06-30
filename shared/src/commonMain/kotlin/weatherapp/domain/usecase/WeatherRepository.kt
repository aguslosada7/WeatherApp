package weatherapp.domain.usecase

import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.model.Weather
import weatherapp.domain.model.DailyForecast

interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Result<Weather>
    suspend fun getWeatherByCoords(lat: Double, lon: Double): Result<Weather>
    suspend fun getForecastByCity(city: String): Result<List<HourlyForecast>>
    suspend fun getForecastByCoords(lat: Double, lon: Double): Result<List<HourlyForecast>>
    suspend fun getDailyForecastByCity(city: String): Result<List<DailyForecast>>
    suspend fun getDailyForecastByCoords(lat: Double, lon: Double): Result<List<DailyForecast>>
}