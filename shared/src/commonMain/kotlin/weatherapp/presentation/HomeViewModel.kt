package weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weatherapp.domain.model.DailyForecast
import weatherapp.domain.model.HourlyForecast
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.GetDailyForecastByCityUseCase
import weatherapp.domain.usecase.GetDailyForecastByCoordsUseCase
import weatherapp.domain.usecase.GetForecastByCityUseCase
import weatherapp.domain.usecase.GetForecastByCoordsUseCase
import weatherapp.domain.usecase.GetWeatherByCityUseCase
import weatherapp.domain.usecase.GetWeatherByCoordsUseCase
import io.github.jan.supabase.auth.auth
import weatherapp.data.local.supabaseClient
import weatherapp.data.CachedWeatherData
import weatherapp.data.WeatherCache

class HomeViewModel(
    private val getWeatherByCity: GetWeatherByCityUseCase,
    private val getWeatherByCoords: GetWeatherByCoordsUseCase,
    private val getForecastByCity: GetForecastByCityUseCase,
    private val getForecastByCoords: GetForecastByCoordsUseCase,
    private val getDailyForecastByCity: GetDailyForecastByCityUseCase,
    private val getDailyForecastByCoords: GetDailyForecastByCoordsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                supabaseClient.auth.awaitInitialization()
                if (supabaseClient.auth.currentSessionOrNull() == null) {
                    supabaseClient.auth.signInAnonymously()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun loadWeatherByCoords(lat: Double, lon: Double) {
        val cacheKey = "coords_${lat}_$lon"
        viewModelScope.launch {
            val cached = WeatherCache.get(cacheKey)
            if (cached != null) {
                _uiState.value = HomeUiState.Success(
                    weather = cached.weather,
                    hourlyForecast = cached.hourlyForecast,
                    dailyForecast = cached.dailyForecast,
                    isStale = true
                )
            } else {
                _uiState.value = HomeUiState.Loading
            }

            val weatherDeferred = async { getWeatherByCoords(lat, lon) }
            val forecastDeferred = async { getForecastByCoords(lat, lon) }
            val dailyDeferred = async { getDailyForecastByCoords(lat, lon) }
            val weather = weatherDeferred.await()
            val forecast = forecastDeferred.await()
            val daily = dailyDeferred.await()

            weather
                .onSuccess { w ->
                    val data = CachedWeatherData(
                        weather = w,
                        hourlyForecast = forecast.getOrDefault(emptyList()),
                        dailyForecast = daily.getOrDefault(emptyList())
                    )
                    WeatherCache.put(cacheKey, data)
                    _uiState.value = HomeUiState.Success(
                        weather = data.weather,
                        hourlyForecast = data.hourlyForecast,
                        dailyForecast = data.dailyForecast,
                        isStale = false
                    )
                }
                .onFailure {
                    if (cached == null) {
                        _uiState.value = HomeUiState.Error(it.message ?: "Error")
                    }
                }
        }
    }

    fun loadWeatherByCity(city: String) {
        val cacheKey = "city_${city.lowercase()}"
        viewModelScope.launch {
            val cached = WeatherCache.get(cacheKey)
            if (cached != null) {
                _uiState.value = HomeUiState.Success(
                    weather = cached.weather,
                    hourlyForecast = cached.hourlyForecast,
                    dailyForecast = cached.dailyForecast,
                    isStale = true
                )
            } else {
                _uiState.value = HomeUiState.Loading
            }

            val weatherDeferred = async { getWeatherByCity(city) }
            val forecastDeferred = async { getForecastByCity(city) }
            val dailyDeferred = async { getDailyForecastByCity(city) }
            val weather = weatherDeferred.await()
            val forecast = forecastDeferred.await()
            val daily = dailyDeferred.await()

            weather
                .onSuccess { w ->
                    val data = CachedWeatherData(
                        weather = w,
                        hourlyForecast = forecast.getOrDefault(emptyList()),
                        dailyForecast = daily.getOrDefault(emptyList())
                    )
                    WeatherCache.put(cacheKey, data)
                    _uiState.value = HomeUiState.Success(
                        weather = data.weather,
                        hourlyForecast = data.hourlyForecast,
                        dailyForecast = data.dailyForecast,
                        isStale = false
                    )
                }
                .onFailure {
                    if (cached == null) {
                        _uiState.value = HomeUiState.Error(it.message ?: "Error")
                    }
                }
        }
    }
}

sealed class HomeUiState {
    data object Loading : HomeUiState()
    data class Success(
        val weather: Weather,
        val hourlyForecast: List<HourlyForecast>,
        val dailyForecast: List<DailyForecast>,
        val isStale: Boolean = false
    ) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}
