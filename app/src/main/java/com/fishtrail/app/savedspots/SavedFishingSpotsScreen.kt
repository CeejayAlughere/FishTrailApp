package com.fishtrail.app.savedspots

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishtrail.app.viewmodel.SavedFishingSpotViewModel

@Composable
fun SavedFishingSpotsScreen(
    username: String,
    savedFishingSpotViewModel: SavedFishingSpotViewModel,
    onForecastClick: (Int) -> Unit
) {

    val savedSpots by
    savedFishingSpotViewModel.savedSpots.collectAsState()

    val searchResults by
    savedFishingSpotViewModel.searchResults.collectAsState()

    val isSearching by
    savedFishingSpotViewModel.isSearching.collectAsState()

    val searchError by
    savedFishingSpotViewModel.searchError.collectAsState()

    var searchQuery by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }

    LaunchedEffect(username) {

        savedFishingSpotViewModel.loadSavedSpots(
            username
        )
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

        Text(
            text = "Saved Fishing Spots",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Search for a fishing location, save its GPS coordinates, and view its forecast.",
            style = MaterialTheme.typography.bodyLarge
        )

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                Text(
                    text = "Find a Fishing Spot",
                    style = MaterialTheme.typography.titleLarge
                )

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                    },
                    label = {
                        Text("Search location")
                    },
                    placeholder = {
                        Text(
                            "e.g. Vaal Dam"
                        )
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Button(
                    onClick = {

                        savedFishingSpotViewModel
                            .searchFishingSpots(
                                searchQuery
                            )
                    },

                    enabled =
                        searchQuery.trim().length >= 2 &&
                                !isSearching,

                    modifier = Modifier.fillMaxWidth()
                ) {

                    Text(
                        if (isSearching) {
                            "Searching..."
                        } else {
                            "Search"
                        }
                    )
                }

                if (searchError != null) {

                    Text(
                        text = searchError ?: "",
                        color =
                            MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (message.isNotBlank()) {

            Text(
                text = message,
                color =
                    MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (searchResults.isNotEmpty()) {

            Text(
                text = "Search Results",
                style = MaterialTheme.typography.titleLarge
            )

            searchResults.forEach { result ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = result.name,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = result.address,
                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text =
                                "Latitude: %.6f"
                                    .format(
                                        result.latitude
                                    )
                        )

                        Text(
                            text =
                                "Longitude: %.6f"
                                    .format(
                                        result.longitude
                                    )
                        )

                        Button(
                            onClick = {

                                savedFishingSpotViewModel
                                    .addSavedSpot(
                                        username = username,
                                        result = result
                                    ) { _, resultMessage ->

                                        message =
                                            resultMessage
                                    }
                            },

                            modifier =
                                Modifier.fillMaxWidth()
                        ) {

                            Text("Add to Saved Fishing Spots")
                        }
                    }
                }
            }
        }

        HorizontalDivider()

        Text(
            text = "My Saved Fishing Spots",
            style = MaterialTheme.typography.titleLarge
        )

        if (savedSpots.isEmpty()) {

            Text(
                text = "You have no saved fishing spots yet.",
                style =
                    MaterialTheme.typography.bodyLarge
            )

        } else {

            savedSpots.forEach { spot ->

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement =
                            Arrangement.spacedBy(8.dp)
                    ) {

                        Text(
                            text = spot.name,
                            style =
                                MaterialTheme.typography.titleMedium
                        )

                        Text(
                            text = spot.address,
                            style =
                                MaterialTheme.typography.bodyMedium
                        )

                        Text(
                            text =
                                "Latitude: %.6f"
                                    .format(
                                        spot.latitude
                                    )
                        )

                        Text(
                            text =
                                "Longitude: %.6f"
                                    .format(
                                        spot.longitude
                                    )
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.spacedBy(8.dp)
                        ) {

                            Button(
                                onClick = {
                                    onForecastClick(
                                        spot.id
                                    )
                                },
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text("Forecast")
                            }

                            TextButton(
                                onClick = {

                                    savedFishingSpotViewModel
                                        .deleteSavedSpot(
                                            spot
                                        )

                                    message =
                                        "${spot.name} removed."
                                },
                                modifier =
                                    Modifier.weight(1f)
                            ) {

                                Text("Delete")
                            }
                        }
                    }
                }
            }
        }
    }
}