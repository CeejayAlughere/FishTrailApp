package com.fishtrail.app.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
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
fun HomeScreen(
    catchViewModel: CatchViewModel,
    username: String,
    onLogCatchClick: () -> Unit,
    onOpenMapClick: () -> Unit,
    onLogoutClick: () -> Unit
) {

    /*
     * Get the logged-in user's saved catches
     * from the existing CatchViewModel.
     */
    val catches by catchViewModel.catches.collectAsState()

    /*
     * Load this user's catches whenever the username
     * changes or the HomeScreen is entered.
     */
    LaunchedEffect(username) {

        if (username.isNotBlank()) {
            catchViewModel.loadCatches(username)
        }
    }

    /*
     * --------------------------------------------------
     * FISHING STATISTICS
     * --------------------------------------------------
     */

    val totalCatches = catches.size

    val largestCatch = catches.maxByOrNull {
        it.weight
    }

    val favouriteSpecies = catches
        .filter { it.species.isNotBlank() }
        .groupingBy {
            it.species.trim()
        }
        .eachCount()
        .maxByOrNull {
            it.value
        }
        ?.key

    /*
     * CatchViewModel already receives catches ordered
     * newest first from CatchDao.
     */
    val recentCatch = catches.firstOrNull()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        /*
         * --------------------------------------------------
         * WELCOME MESSAGE
         * --------------------------------------------------
         */

        Text(
            text = "Welcome back, $username!",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "Track your fishing journey with FishTrail.",
            style = MaterialTheme.typography.bodyLarge
        )

        /*
         * --------------------------------------------------
         * FISHING STATISTICS
         * --------------------------------------------------
         */

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Your Fishing Statistics",
                    style = MaterialTheme.typography.titleLarge
                )

                Text(
                    text = "Total Catches: $totalCatches"
                )

                Text(
                    text = if (largestCatch != null) {
                        "Largest Catch: ${largestCatch.weight} kg"
                    } else {
                        "Largest Catch: --"
                    }
                )

                Text(
                    text = if (!favouriteSpecies.isNullOrBlank()) {
                        "Favourite Species: $favouriteSpecies"
                    } else {
                        "Favourite Species: --"
                    }
                )
            }
        }

        /*
         * --------------------------------------------------
         * QUICK ACTIONS
         * --------------------------------------------------
         */

        Text(
            text = "Quick Actions",
            style = MaterialTheme.typography.titleLarge
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Button(
                onClick = onLogCatchClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Log Catch")
            }

            Button(
                onClick = onOpenMapClick,
                modifier = Modifier.weight(1f)
            ) {
                Text("Open Map")
            }
        }

        /*
         * --------------------------------------------------
         * RECENT CATCH
         * --------------------------------------------------
         */

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {

            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {

                Text(
                    text = "Recent Catch",
                    style = MaterialTheme.typography.titleLarge
                )

                if (recentCatch == null) {

                    Text(
                        text = "No catches recorded yet."
                    )

                } else {

                    Text(
                        text = recentCatch.species,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = "Weight: ${recentCatch.weight} kg"
                    )

                    Text(
                        text = "Length: ${recentCatch.length} cm"
                    )

                    Text(
                        text = "Date: ${recentCatch.date}"
                    )

                    Text(
                        text = "Time: ${recentCatch.time}"
                    )

                    Text(
                        text = "Location: ${
                            if (recentCatch.locationName.isNotBlank()) {
                                recentCatch.locationName
                            } else {
                                "Not specified"
                            }
                        }"
                    )
                }
            }
        }
    }
}