package weatherapp.domain.usecase

import weatherapp.domain.model.Weather
import weatherapp.domain.repository.WeatherRepository

class GetWeatherByCoordsUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> =
        repository.getWeatherByCoords(lat, lon)
}