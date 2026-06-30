package weatherapp.domain.usecase

import weatherapp.domain.model.Weather

class GetWeatherByCityUseCase(
    private val repository: WeatherRepository,
) {
    suspend operator fun invoke(city: String): Result<Weather> =
        repository.getWeatherByCity(city)
}