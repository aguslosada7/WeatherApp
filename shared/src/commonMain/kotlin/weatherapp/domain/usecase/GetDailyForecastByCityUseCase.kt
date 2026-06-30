package weatherapp.domain.usecase

import weatherapp.domain.model.DailyForecast

class GetDailyForecastByCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): Result<List<DailyForecast>> =
        repository.getDailyForecastByCity(city)
}