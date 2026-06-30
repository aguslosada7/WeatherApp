package weatherapp.domain.usecase

import weatherapp.domain.model.DailyForecast

class GetDailyForecastByCoordsUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<List<DailyForecast>> =
        repository.getDailyForecastByCoords(lat, lon)
}