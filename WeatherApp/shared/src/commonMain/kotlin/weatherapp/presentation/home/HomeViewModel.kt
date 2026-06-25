package weatherapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import weatherapp.domain.model.Weather
import weatherapp.domain.usecase.GetWeatherByCityUseCase

class HomeViewModel(
    private val getWeatherByCity: GetWeatherByCityUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadWeather("Buenos Aires")
    }

    fun loadWeather(city: String) {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            getWeatherByCity(city)
                .onSuccess { _uiState.value = HomeUiState.Success(it) }
                .onFailure { _uiState.value = HomeUiState.Error(it.message ?: "Error") }
        }
    }
}

sealed class HomeUiState {
    object Loading : HomeUiState()
    data class Success(val weather: Weather) : HomeUiState()
    data class Error(val message: String) : HomeUiState()
}