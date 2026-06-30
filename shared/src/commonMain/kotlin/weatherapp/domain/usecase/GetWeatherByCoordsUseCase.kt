package weatherapp.domain.usecase

import weatherapp.domain.model.Weather

class GetWeatherByCoordsUseCase(
    private val repository: WeatherRepository
) {
    suspend operator fun invoke(lat: Double, lon: Double): Result<Weather> =
        repository.getWeatherByCoords(lat, lon)
}