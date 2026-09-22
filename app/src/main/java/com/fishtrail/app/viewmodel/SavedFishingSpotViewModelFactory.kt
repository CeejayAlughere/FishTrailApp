package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fishtrail.app.data.SavedFishingSpotDao
import com.google.android.libraries.places.api.net.PlacesClient

class SavedFishingSpotViewModelFactory(
    private val dao: SavedFishingSpotDao,
    private val placesClient: PlacesClient
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(
        modelClass: Class<T>
    ): T {

        if (
            modelClass.isAssignableFrom(
                SavedFishingSpotViewModel::class.java
            )
        ) {

            return SavedFishingSpotViewModel(
                dao,
                placesClient
            ) as T
        }

        throw IllegalArgumentException(
            "Unknown ViewModel class"
        )
    }
}