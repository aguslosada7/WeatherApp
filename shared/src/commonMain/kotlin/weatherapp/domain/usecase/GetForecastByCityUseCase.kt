package weatherapp.domain.usecase

import weatherapp.domain.model.HourlyForecast

class GetForecastByCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): Result<List<HourlyForecast>> =
        repository.getForecastByCity(city)
}