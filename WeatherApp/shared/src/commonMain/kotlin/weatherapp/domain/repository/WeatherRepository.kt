package weatherapp.domain.repository

import weatherapp.domain.model.Weather

interface WeatherRepository {
    suspend fun getWeatherByCity(city: String): Result<Weather>
    suspend fun getWeatherByCoords(lat: Double, lon: Double): Result<Weather>
}