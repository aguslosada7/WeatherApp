package weatherapp.domain.usecase

import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.repository.WeatherRepository

class GetForecastByCoordsUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<List<HourlyForecast>> =
        repository.getForecastByCoords(lat, lon)
}