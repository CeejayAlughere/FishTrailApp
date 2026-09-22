package com.fishtrail.app.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.fishtrail.app.viewmodel.CatchViewModel

@Composable
fun HistoryScreen(
    catchViewModel: CatchViewModel,
    username: String
) {

    val catches by catchViewModel.catches.collectAsState()

    LaunchedEffect(username) {
        catchViewModel.loadCatches(username)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {

        Text(
            text = "Catch History",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Your recorded catches",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
        )

        if (catches.isEmpty()) {

            Text(
                text = "No catches recorded yet.",
                style = MaterialTheme.typography.bodyLarge
            )

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(catches) { catch ->

                    Card(
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {

                            Text(
                                text = catch.species,
                                style = MaterialTheme.typography.titleLarge
                            )

                            Text(
                                text = "Weight: ${catch.weight} kg"
                            )

                            Text(
                                text = "Length: ${catch.length} cm"
                            )

                            Text(
                                text = "Date: ${catch.date}"
                            )

                            Text(
                                text = "Time: ${catch.time}"
                            )

                            Text(
                                text = "Location: ${catch.locationName}"
                            )

                            if (catch.latitude != null && catch.longitude != null) {

                                Text(
                                    text = "Latitude: ${catch.latitude}"
                                )

                                Text(
                                    text = "Longitude: ${catch.longitude}"
                                )
                            }

                            Text(
                                text = "Bait / Lure: ${catch.bait}"
                            )

                            if (catch.notes.isNotBlank()) {

                                Text(
                                    text = "Notes: ${catch.notes}"
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}