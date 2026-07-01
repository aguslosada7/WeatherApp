package weatherapp.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.GetWeatherByCityUseCase
import kotlin.time.Duration.Companion.milliseconds
import weatherapp.data.CachedWeatherData
import weatherapp.data.WeatherCache

@OptIn(FlowPreview::class)
class SearchViewModel(
    private val getWeatherByCity: GetWeatherByCityUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    init {
        _query
            .debounce(500.milliseconds)
            .map { it.trim() }
            .distinctUntilChanged()
            .filter { it.isNotBlank() }
            .onEach { city -> searchCity(city) }
            .launchIn(viewModelScope)
    }

    fun onQueryChange(query: String) {
        _query.value = query
        if (query.isBlank()) {
            _uiState.value = SearchUiState.Idle
        }
    }

    fun search() {
        val city = _query.value.trim()
        if (city.isBlank()) return
        viewModelScope.launch { searchCity(city) }
    }

    private suspend fun searchCity(city: String) {
        val cacheKey = "city_${city.lowercase()}"
        val cached = WeatherCache.get(cacheKey)

        if (cached != null) {
            _uiState.value = SearchUiState.Success(cached.weather, isStale = true)
        } else {
            _uiState.value = SearchUiState.Loading
        }

        getWeatherByCity(city)
            .onSuccess { w ->
                WeatherCache.put(cacheKey, CachedWeatherData(w, emptyList(), emptyList()))
                _uiState.value = SearchUiState.Success(w, isStale = false)
            }
            .onFailure {
                if (cached == null) {
                    _uiState.value = SearchUiState.Error("Ciudad no encontrada")
                }
            }
    }

    fun reset() {
        _uiState.value = SearchUiState.Idle
        _query.value = ""
    }
}

sealed class SearchUiState {
    data object Idle : SearchUiState()
    data object Loading : SearchUiState()
    data class Success(val weather: Weather, val isStale: Boolean = false) : SearchUiState()
    data class Error(val message: String) : SearchUiState()
}
