package com.fishtrail.app.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WeatherUiState(
    val isLoading: Boolean = false,
    val weather: WeatherResponse? = null,
    val error: String? = null
)

class WeatherViewModel(
    private val repository: WeatherRepository
) : ViewModel() {

    private val _uiState =
        MutableStateFlow(
            WeatherUiState()
        )

    val uiState: StateFlow<WeatherUiState> =
        _uiState.asStateFlow()

    fun loadForecast(
        latitude: Double,
        longitude: Double
    ) {

        viewModelScope.launch {

            _uiState.value =
                WeatherUiState(
                    isLoading = true
                )

            val result =
                repository.getForecast(
                    latitude = latitude,
                    longitude = longitude
                )

            _uiState.value =
                if (result.isSuccess) {

                    WeatherUiState(
                        weather =
                            result.getOrNull()
                    )

                } else {

                    WeatherUiState(
                        error =
                            "Unable to load the weather forecast. Check your internet connection and try again."
                    )
                }
        }
    }
}