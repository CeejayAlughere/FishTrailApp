
package com.fishtrail.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fishtrail.app.data.Catch
import com.fishtrail.app.data.CatchDao
import com.fishtrail.app.sync.CatchSyncScheduler
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CatchViewModel(
    private val catchDao: CatchDao,
    private val syncScheduler: CatchSyncScheduler
) : ViewModel() {

    private val _catches = MutableStateFlow<List<Catch>>(emptyList())
    val catches: StateFlow<List<Catch>> = _catches.asStateFlow()

    private var catchesJob: Job? = null

    fun saveCatch(
        username: String,
        species: String,
        weight: Double,
        length: Double,
        bait: String,
        notes: String,
        date: String,
        time: String,
        locationName: String,
        latitude: Double?,
        longitude: Double?,
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val newCatch = Catch(
                    username = username,
                    species = species,
                    weight = weight,
                    length = length,
                    bait = bait,
                    notes = notes,
                    date = date,
                    time = time,
                    locationName = locationName,
                    latitude = latitude,
                    longitude = longitude
                )

                catchDao.insertCatch(newCatch)

                loadCatches(username)

                syncScheduler.scheduleSync()

                onResult(
                    true,
                    "Catch saved successfully. Sync will happen when internet is available."
                )

            } catch (exception: Exception) {
                onResult(
                    false,
                    exception.message ?: "Unable to save catch."
                )
            }
        }
    }

    fun syncPendingCatches() {
        syncScheduler.scheduleSync()
    }

    // Observe Room so sync-status changes appear automatically.
    fun loadCatches(username: String) {
        catchesJob?.cancel()

        catchesJob = viewModelScope.launch {
            catchDao.getCatchesByUsername(username).collect { updatedCatches ->
                _catches.value = updatedCatches
            }
        }
    }
}
