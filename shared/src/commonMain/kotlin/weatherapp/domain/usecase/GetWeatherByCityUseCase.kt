package weatherapp.domain.usecase

import weatherapp.domain.model.Weather
import weatherapp.domain.repository.WeatherRepository

class GetWeatherByCityUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(city: String): Result<Weather> =
        repository.getWeatherByCity(city)
}