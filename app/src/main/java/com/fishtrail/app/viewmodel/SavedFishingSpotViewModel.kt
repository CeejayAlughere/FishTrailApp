package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishtrail.app.data.SavedFishingSpot
import com.fishtrail.app.data.SavedFishingSpotDao
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.PlacesClient
import com.google.android.libraries.places.api.net.kotlin.awaitSearchByText
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class PlaceSearchResult(
    val name: String,
    val address: String,
    val latitude: Double,
    val longitude: Double
)

class SavedFishingSpotViewModel(
    private val dao: SavedFishingSpotDao,
    private val placesClient: PlacesClient
) : ViewModel() {

    private val _savedSpots =
        MutableStateFlow<List<SavedFishingSpot>>(emptyList())

    val savedSpots: StateFlow<List<SavedFishingSpot>> =
        _savedSpots.asStateFlow()

    private val _searchResults =
        MutableStateFlow<List<PlaceSearchResult>>(emptyList())

    val searchResults: StateFlow<List<PlaceSearchResult>> =
        _searchResults.asStateFlow()

    private val _isSearching =
        MutableStateFlow(false)

    val isSearching: StateFlow<Boolean> =
        _isSearching.asStateFlow()

    private val _searchError =
        MutableStateFlow<String?>(null)

    val searchError: StateFlow<String?> =
        _searchError.asStateFlow()

    private var observeJob: Job? = null

    fun loadSavedSpots(username: String) {

        observeJob?.cancel()

        if (username.isBlank()) {
            _savedSpots.value = emptyList()
            return
        }

        observeJob = viewModelScope.launch {

            dao.getSavedSpotsByUsername(username)
                .collect { spots ->

                    _savedSpots.value = spots
                }
        }
    }

    fun searchFishingSpots(
        query: String
    ) {

        val cleanQuery = query.trim()

        if (cleanQuery.length < 2) {

            _searchResults.value = emptyList()

            _searchError.value =
                "Enter at least 2 characters to search."

            return
        }

        viewModelScope.launch {

            _isSearching.value = true
            _searchError.value = null

            try {

                val placeFields = listOf(
                    Place.Field.ID,
                    Place.Field.DISPLAY_NAME,
                    Place.Field.FORMATTED_ADDRESS,
                    Place.Field.LOCATION
                )

                val response =
                    placesClient.awaitSearchByText(
                        textQuery = cleanQuery,
                        placeFields = placeFields
                    ) {
                        maxResultCount = 10

                        /*
                         * This helps bias generic searches toward
                         * South Africa without preventing explicit
                         * location searches.
                         */
                        regionCode = "ZA"
                    }

                val results =
                    response.places.mapNotNull { place ->

                        val location =
                            place.location
                                ?: return@mapNotNull null

                        val name =
                            place.displayName
                                ?.toString()
                                ?.trim()
                                ?.takeIf {
                                    it.isNotBlank()
                                }
                                ?: return@mapNotNull null

                        PlaceSearchResult(
                            name = name,

                            address =
                                place.formattedAddress
                                    ?.trim()
                                    ?.takeIf {
                                        it.isNotBlank()
                                    }
                                    ?: "Address unavailable",

                            latitude = location.latitude,

                            longitude = location.longitude
                        )
                    }

                _searchResults.value = results

                if (results.isEmpty()) {

                    _searchError.value =
                        "No matching locations were found."
                }

            } catch (exception: com.google.android.gms.common.api.ApiException) {

                android.util.Log.e(
                    "FishTrailPlaces",
                    "Places search failed. " +
                            "Status code: ${exception.statusCode}, " +
                            "Message: ${exception.message}",
                    exception
                )

                _searchResults.value = emptyList()

                _searchError.value =
                    when (exception.statusCode) {

                        7 ->
                            "Google Places access was denied. Check that Places API (New) is enabled and that your API key is allowed to use it."

                        8 ->
                            "Google Places service is unavailable. Please try again."

                        13 ->
                            "Google Places encountered an internal error. Please try again."

                        3 ->
                            "The Google Places request was invalid."

                        12 ->
                            "This Google Places feature is not available."

                        else ->
                            "Google Places search failed. Error code: ${exception.statusCode}"
                    }

            } catch (exception: Exception) {

                android.util.Log.e(
                    "FishTrailPlaces",
                    "Unexpected Places search error",
                    exception
                )

                _searchResults.value = emptyList()

                _searchError.value =
                    "Unable to search for fishing locations. Check your internet connection."
            } finally {

                _isSearching.value = false
            }
        }
    }

    fun addSavedSpot(
        username: String,
        result: PlaceSearchResult,
        onResult: (Boolean, String) -> Unit
    ) {

        viewModelScope.launch {

            if (username.isBlank()) {

                onResult(
                    false,
                    "No logged-in user was found."
                )

                return@launch
            }

            val existingCount =
                dao.countSpot(
                    username = username,
                    latitude = result.latitude,
                    longitude = result.longitude
                )

            if (existingCount > 0) {

                onResult(
                    false,
                    "This fishing spot is already saved."
                )

                return@launch
            }

            val savedSpot =
                SavedFishingSpot(
                    username = username,
                    name = result.name,
                    address = result.address,
                    latitude = result.latitude,
                    longitude = result.longitude
                )

            dao.insertSpot(savedSpot)

            onResult(
                true,
                "${result.name} saved successfully."
            )
        }
    }

    fun deleteSavedSpot(
        spot: SavedFishingSpot
    ) {

        viewModelScope.launch {

            dao.deleteSpot(spot)
        }
    }

    fun clearSearchResults() {

        _searchResults.value = emptyList()
        _searchError.value = null
    }
}