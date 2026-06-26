package weatherapp.data.remote.api.shared.src.commonMain.kotlin.weatherapp.domain.usecase

import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.repository.WeatherRepository

class GetForecastByCityUseCase(private val repository: WeatherRepository) {
    suspend operator fun invoke(city: String): Result<List<HourlyForecast>> =
        repository.getForecastByCity(city)
}