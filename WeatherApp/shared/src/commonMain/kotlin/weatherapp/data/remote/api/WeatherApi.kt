package weatherapp.data.remote.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import weatherapp.config.AppConfig
import weatherapp.data.remote.dto.WeatherDto

class WeatherApi(private val client: HttpClient) {

    private val apiKey = AppConfig.WEATHER_API_KEY
    private val baseUrl = "https://api.openweathermap.org/data/2.5"

    suspend fun getWeatherByCity(city: String): WeatherDto =
        client.get("$baseUrl/weather") {
            parameter("q", city)
            parameter("appid", apiKey)
            parameter("units", "metric")
            parameter("lang", "es")
        }.body()

    suspend fun getWeatherByCoords(lat: Double, lon: Double): WeatherDto =
        client.get("$baseUrl/weather") {
            parameter("lat", lat)
            parameter("lon", lon)
            parameter("appid", apiKey)
            parameter("units", "metric")
            parameter("lang", "es")
        }.body()
}