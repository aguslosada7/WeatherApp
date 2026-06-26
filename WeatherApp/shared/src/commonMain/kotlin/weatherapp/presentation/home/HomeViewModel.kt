package weatherapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.GetForecastByCityUseCase
import weatherapp.domain.usecase.GetForecastByCoordsUseCase
import weatherapp.domain.usecase.GetWeatherByCityUseCase
import weatherapp.domain.usecase.GetWeatherByCoordsUseCase

class HomeViewModel(
    private val getWeatherByCity: GetWeatherByCityUseCase,
    private val getWeatherByCoords: GetWeatherByCoordsUseCase,
    private val getForecastByCity: GetForecastByCityUseCase,
    private val getForecastByCoords: GetForecastByCoordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    fun loadWeatherByCoords(lat: Double, lon: Double) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val weatherDeferred = async { getWeatherByCoords(lat, lon) }
            val forecastDeferred = async { getForecastByCoords(lat, lon) }
            val weather = weatherDeferred.await()
            val forecast = forecastDeferred.await()
            weather
                .onSuccess { w ->
                    _uiState.value = HomeUiState.Success(
                        weather = w,
                        hourlyForecast = forecast.getOrDefault(emptyList())
                    )
                }
                .onFailure { _uiState.value = HomeUiState.Error(it.message ?: "Error") }
        }
    }

    fun loadWeatherByCity(city: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            val weatherDeferred = async { getWeatherByCity(city) }
            val forecastDeferred = async { getForecastByCity(city) }
            val weather = weatherDeferred.await()
            val forecast = forecastDeferred.await()
            weather
                .onSuccess { w ->
                    _uiState.value = HomeUiState.Success(
                        weather = w,
                        hourlyForecast = forecast.getOrDefault(emptyList())
                    )
                }
                .onFailure { _uiState.value = HomeUiState.Error(it.message ?: "Error") }
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val weather: Weather,
        val hourlyForecast: List<HourlyForecast>
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
