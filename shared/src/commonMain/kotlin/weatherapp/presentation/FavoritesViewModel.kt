package weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weatherapp.domain.model.FavoriteCity
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.AddFavoriteUseCase
import weatherapp.domain.usecase.GetFavoritesUseCase
import weatherapp.domain.usecase.IsFavoriteUseCase
import weatherapp.domain.usecase.RemoveFavoriteUseCase

class FavoritesViewModel(
    private val getFavorites: GetFavoritesUseCase,
    private val addFavorite: AddFavoriteUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
    private val isFavorite: IsFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<FavoritesUiState>(FavoritesUiState.Loading)
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavoriteState: StateFlow<Boolean> = _isFavorite.asStateFlow()

    fun loadFavorites() {
        viewModelScope.launch {
            _uiState.value = FavoritesUiState.Loading
            getFavorites()
                .onSuccess { _uiState.value = FavoritesUiState.Success(it) }
                .onFailure { _uiState.value = FavoritesUiState.Error(it.message ?: "Error") }
        }
    }

    fun checkIsFavorite(cityName: String) {
        viewModelScope.launch {
            isFavorite(cityName).onSuccess { _isFavorite.value = it }
        }
    }

    fun toggleFavorite(city: FavoriteCity) {
        viewModelScope.launch {
            if (_isFavorite.value) {
                // Buscar el id directo desde Supabase, no desde el estado local
                getFavorites().onSuccess { favorites ->
                    val id = favorites.find { it.cityName == city.cityName }?.id
                    if (id != null) {
                        removeFavorite(id).onSuccess {
                            _isFavorite.value = false
                            loadFavorites()
                        }
                    }
                }
            } else {
                addFavorite(city).onSuccess {
                    _isFavorite.value = true
                    loadFavorites()
                }
            }
        }
    }

    fun removeById(id: String) {
        viewModelScope.launch {
            removeFavorite(id).onSuccess { loadFavorites() }
        }
    }

    private val _expandedWeather = MutableStateFlow<Map<String, ExpandedWeatherState>>(emptyMap())
    val expandedWeather: StateFlow<Map<String, ExpandedWeatherState>> = _expandedWeather.asStateFlow()

    private val _selectedCityId = MutableStateFlow<String?>(null)
    val selectedCityId: StateFlow<String?> = _selectedCityId.asStateFlow()

    fun selectCity(city: FavoriteCity, getWeatherByCity: suspend (String) -> Result<Weather>) {
        viewModelScope.launch {
            if (_selectedCityId.value == city.id) {
                _selectedCityId.value = null
                return@launch
            }
            _selectedCityId.value = city.id

            val cacheKey = "city_${city.cityName.lowercase()}"
            val cached = weatherapp.data.WeatherCache.get(cacheKey)

            if (cached != null) {
                _expandedWeather.value += (city.id to ExpandedWeatherState.Success(cached.weather))
            } else if (_expandedWeather.value[city.id] == null) {
                _expandedWeather.value += (city.id to ExpandedWeatherState.Loading)
            }

            getWeatherByCity(city.cityName)
                .onSuccess { weather ->
                    weatherapp.data.WeatherCache.put(
                        cacheKey,
                        weatherapp.data.CachedWeatherData(weather, emptyList(), emptyList())
                    )
                    _expandedWeather.value += (city.id to ExpandedWeatherState.Success(weather))
                }
                .onFailure {
                    if (cached == null) {
                        _expandedWeather.value += (city.id to ExpandedWeatherState.Error)
                    }
                }
        }
    }
}

sealed class FavoritesUiState {
    data object Loading : FavoritesUiState()
    data class Success(val favorites: List<FavoriteCity>) : FavoritesUiState()
    data class Error(val message: String) : FavoritesUiState()
}

sealed class ExpandedWeatherState {
    data object Loading : ExpandedWeatherState()
    data class Success(val weather: Weather) : ExpandedWeatherState()
    data object Error : ExpandedWeatherState()
}