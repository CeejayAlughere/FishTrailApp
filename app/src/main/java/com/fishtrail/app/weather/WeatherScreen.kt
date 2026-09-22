package com.fishtrail.app.weather

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishtrail.app.viewmodel.SavedFishingSpotViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun WeatherScreen(
    spotId: Int,
    savedFishingSpotViewModel: SavedFishingSpotViewModel,
    weatherViewModel: WeatherViewModel
) {

    val savedSpots by
    savedFishingSpotViewModel.savedSpots.collectAsState()

    val uiState by
    weatherViewModel.uiState.collectAsState()

    val spot =
        savedSpots.firstOrNull {
            it.id == spotId
        }

    LaunchedEffect(
        spot?.id,
        spot?.latitude,
        spot?.longitude
    ) {

        if (spot != null) {

            weatherViewModel.loadForecast(
                latitude = spot.latitude,
                longitude = spot.longitude
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(
                rememberScrollState()
            )
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        if (spot == null) {

            Text(
                text = "Saved fishing spot could not be found.",
                color =
                    MaterialTheme.colorScheme.error
            )

            return@Column
        }

        Text(
            text = "Weather Forecast",
            style =
                MaterialTheme.typography.headlineMedium
        )

        Text(
            text = spot.name,
            style =
                MaterialTheme.typography.titleLarge
        )

        Text(
            text = spot.address,
            style =
                MaterialTheme.typography.bodyMedium
        )

        Text(
            text =
                "GPS: %.6f, %.6f"
                    .format(
                        spot.latitude,
                        spot.longitude
                    ),
            style =
                MaterialTheme.typography.bodyMedium
        )

        if (uiState.isLoading) {

            Text(
                text = "Loading weather..."
            )
        }

        if (uiState.error != null) {

            Card(
                modifier =
                    Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier =
                        Modifier.padding(16.dp),
                    verticalArrangement =
                        Arrangement.spacedBy(12.dp)
                ) {

                    Text(
                        text = uiState.error
                            ?: "Weather error.",
                        color =
                            MaterialTheme.colorScheme.error
                    )

                    Button(
                        onClick = {

                            weatherViewModel
                                .loadForecast(
                                    spot.latitude,
                                    spot.longitude
                                )
                        },
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Text("Try Again")
                    }
                }
            }
        }

        val weather = uiState.weather

        if (weather != null) {

            val current = weather.current

            if (current != null) {

                Card(
                    modifier =
                        Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier =
                            Modifier.padding(20.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = "Current Weather",
                            style =
                                MaterialTheme.typography.titleLarge
                        )

                        Text(
                            text =
                                weatherDescription(
                                    current.weatherCode
                                ),
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text =
                                "Temperature: ${
                                    formatNumber(
                                        current.temperature
                                    )
                                } °C"
                        )

                        Text(
                            text =
                                "Feels like: ${
                                    formatNumber(
                                        current.apparentTemperature
                                    )
                                } °C"
                        )

                        Text(
                            text =
                                "Humidity: ${
                                    current.relativeHumidity
                                        .roundToInt()
                                }%"
                        )

                        Text(
                            text =
                                "Precipitation: ${
                                    formatNumber(
                                        current.precipitation
                                    )
                                } mm"
                        )

                        Text(
                            text =
                                "Wind: ${
                                    formatNumber(
                                        current.windSpeed
                                    )
                                } km/h ${
                                    cardinalDirection(
                                        current.windDirection
                                    )
                                }"
                        )

                        Text(
                            text =
                                "Timezone: ${weather.timezone}"
                        )
                    }
                }
            }

            val daily = weather.daily

            if (daily != null) {

                HorizontalDivider()

                Text(
                    text = "7-Day Forecast",
                    style =
                        MaterialTheme.typography.titleLarge
                )

                val numberOfDays =
                    minOf(
                        7,
                        daily.time.size,
                        daily.weatherCode.size,
                        daily.temperatureMax.size,
                        daily.temperatureMin.size,
                        daily.precipitationProbability.size,
                        daily.precipitationSum.size,
                        daily.windSpeedMax.size
                    )

                for (index in 0 until numberOfDays) {

                    Card(
                        modifier =
                            Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier =
                                Modifier.padding(16.dp),
                            verticalArrangement =
                                Arrangement.spacedBy(6.dp)
                        ) {

                            Text(
                                text =
                                    formatDate(
                                        daily.time[index]
                                    ),
                                style =
                                    MaterialTheme.typography.titleMedium
                            )

                            Text(
                                text =
                                    weatherDescription(
                                        daily.weatherCode[index]
                                    )
                            )

                            Text(
                                text =
                                    "High: ${
                                        formatNumber(
                                            daily.temperatureMax[index]
                                        )
                                    } °C"
                            )

                            Text(
                                text =
                                    "Low: ${
                                        formatNumber(
                                            daily.temperatureMin[index]
                                        )
                                    } °C"
                            )

                            Text(
                                text =
                                    "Rain chance: ${
                                        daily.precipitationProbability[index]
                                            .roundToInt()
                                    }%"
                            )

                            Text(
                                text =
                                    "Rain: ${
                                        formatNumber(
                                            daily.precipitationSum[index]
                                        )
                                    } mm"
                            )

                            Text(
                                text =
                                    "Max wind: ${
                                        formatNumber(
                                            daily.windSpeedMax[index]
                                        )
                                    } km/h"
                            )

                            if (
                                daily.sunrise.size > index &&
                                daily.sunset.size > index
                            ) {

                                Text(
                                    text =
                                        "Sunrise: ${
                                            formatTime(
                                                daily.sunrise[index]
                                            )
                                        }"
                                )

                                Text(
                                    text =
                                        "Sunset: ${
                                            formatTime(
                                                daily.sunset[index]
                                            )
                                        }"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun formatNumber(
    value: Double
): String {

    return String.format(
        Locale.getDefault(),
        "%.1f",
        value
    )
}

private fun formatDate(
    dateString: String
): String {

    return try {

        val input =
            SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            )

        val output =
            SimpleDateFormat(
                "EEE, dd MMM",
                Locale.getDefault()
            )

        val date =
            input.parse(dateString)

        if (date != null) {
            output.format(date)
        } else {
            dateString
        }

    } catch (exception: Exception) {

        dateString
    }
}

private fun formatTime(
    dateTimeString: String
): String {

    return if (dateTimeString.length >= 16) {

        dateTimeString.substring(
            11,
            16
        )

    } else {

        dateTimeString
    }
}

private fun cardinalDirection(
    degrees: Double
): String {

    val directions =
        arrayOf(
            "N",
            "NE",
            "E",
            "SE",
            "S",
            "SW",
            "W",
            "NW"
        )

    val index =
        ((degrees + 22.5) / 45.0)
            .toInt() % 8

    return directions[index]
}

private fun weatherDescription(
    code: Int
): String {

    return when (code) {

        0 ->
            "Clear sky"

        1, 2, 3 ->
            "Partly cloudy"

        45, 48 ->
            "Fog"

        51, 53, 55 ->
            "Drizzle"

        56, 57 ->
            "Freezing drizzle"

        61, 63, 65 ->
            "Rain"

        66, 67 ->
            "Freezing rain"

        71, 73, 75, 77 ->
            "Snow"

        80, 81, 82 ->
            "Rain showers"

        85, 86 ->
            "Snow showers"

        95 ->
            "Thunderstorm"

        96, 99 ->
            "Thunderstorm with hail"

        else ->
            "Unknown weather"
    }
}